package lab7;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;

public class Proxy {
    public static final int CACHE_MSG = 1;
    public static final int CLIENT_MSG = 0;
    public static final String EMPTY_STRING = "";

    private ZMQ.Poller poller;
    private ZMQ.Socket client;
    private ZMQ.Socket cacheSocket;
    Map<ZFrame, Cache> caches;

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        proxy.proxyInitialization();
        proxy.waitAndDoRequests();
    }

    private void proxyInitialization() {
        ZContext context = new ZContext();
        cacheSocket = context.createSocket(SocketType.ROUTER);
        client = context.createSocket(SocketType.ROUTER);
        cacheSocket.setHWM(0);
        client.setHWM(0);
        cacheSocket.bind(CacheStorage.DEALER_SOCKET);
        client.bind(Client.CLIENT_SOCKET);

        poller = context.createPoller(2);
        poller.register(client, ZMQ.Poller.POLLIN);
        poller.register(cacheSocket, ZMQ.Poller.POLLIN);
    }

    private void waitAndDoRequests() {
        caches = new HashMap<>();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            if (getClientRequest() == -1)
                break;
            if (getCacheStorageRequest() == -1)
                break;
        }
    }

    private int getClientRequest() {
        if (poller.pollin(CLIENT_MSG)) {
            ZMsg msg = ZMsg.recvMsg(client);
            if (msg == null) {
                return -1;
            }

            if (caches.isEmpty()) {
                ZMsg errMsg = new ZMsg();
                errMsg.add(msg.getFirst());
                errMsg.add(EMPTY_STRING);
                errMsg.add("no caches");
                errMsg.send(client);
            } else {
                String[] data = msg.getLast().toString().split(CacheStorage.SPACE_DELIMITER);
                if (data[0].equals(CacheStorage.GET)) {
                    for (Map.Entry<ZFrame, Cache> map : caches.entrySet()) {
                        if (map.getValue().isIntersect(data[1])) {
                            ZFrame cacheFrame = map.getKey().duplicate();
                            msg.addFirst(cacheFrame);
                            System.out.println("Get message: " + msg);
                            msg.send(cacheSocket);
                            break;
                        }
                    }
                } else {
                    if (data[0].equals(CacheStorage.PUT)) {
                        for (Map.Entry<ZFrame, Cache> map : caches.entrySet()) {
                            if (map.getValue().isIntersect(data[1])) {
                                ZMsg msgCopy = msg.duplicate();
                                ZFrame cacheFrame = map.getKey().duplicate();
                                msgCopy.addFirst(cacheFrame);
                                System.out.println("Put message: " + msgCopy);
                                msgCopy.send(cacheSocket);
                            }
                        }
                    } else {
                        ZMsg errMsg = new ZMsg();
                        errMsg.add(msg.getFirst());
                        errMsg.add(EMPTY_STRING);
                        errMsg.add("bad message");
                        errMsg.send(client);
                    }
                }
            }
        }
        return 0;
    }

    private int getCacheStorageRequest() {
        if (poller.pollin(CACHE_MSG)) {
            ZMsg msg = ZMsg.recvMsg(cacheSocket);
            if (msg == null) {
                return -1;
            }

            if (msg.getLast().toString().contains(CacheStorage.HEARTBEAT)) {
                if (!caches.containsKey(msg.getFirst())) {
                    ZFrame data = msg.getLast();
                    String[] dataToArray = data.toString().split(CacheStorage.SPACE_DELIMITER);
                    Cache cache = new Cache(
                            dataToArray[1],
                            dataToArray[2],
                            System.currentTimeMillis()
                    );
                    caches.put(msg.getFirst().duplicate(), cache);
                    System.out.println("Created cache: " + msg.getFirst() + " " + cache.getLeftBorder() +
                            " " + cache.getRightBorder());
                } else {
                    caches.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
                }
            } else {
                msg.pop();
                System.out.println("Answer: " + msg);
                msg.send(client);
            }
        }

        return 0;
    }
}
