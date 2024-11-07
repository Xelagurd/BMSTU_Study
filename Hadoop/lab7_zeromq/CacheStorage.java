package lab7;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    public static final String DEALER_SOCKET = "tcp://localhost:2050";
    public static final int TIMEOUT = 5000;
    public static final String HEARTBEAT = "Heartbeat";
    public static final String SPACE_DELIMITER = " ";
    private static final int CACHE = 0;
    public static final String GET = "GET";
    public static final String PUT = "PUT";

    private static int leftBorder, rightBorder;
    private ZMQ.Socket cacheSocket;
    private ZMQ.Poller poller;
    private Map<Integer, String> cache;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        leftBorder = in.nextInt();
        rightBorder = in.nextInt();
        CacheStorage cacheStorage = new CacheStorage();
        cacheStorage.cacheInitialization();
        cacheStorage.waitAndDoRequests();
    }

    private void cacheInitialization() {
        cache = new HashMap<>();
        for (int i = leftBorder; i <= rightBorder; i++) {
            cache.put(i, Integer.toString(i));
        }

        ZContext context = new ZContext();
        cacheSocket = context.createSocket(SocketType.DEALER);
        cacheSocket.setHWM(0);
        cacheSocket.connect(DEALER_SOCKET);

        poller = context.createPoller(1);
        poller.register(cacheSocket, ZMQ.Poller.POLLIN);
    }

    private void waitAndDoRequests() {
        long startTime = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            heartbeat(startTime);

            if (poller.pollin(CACHE)) {
                ZMsg msg = ZMsg.recvMsg(cacheSocket);
                System.out.println("Get message: " + msg.toString());
                ZFrame info = msg.getLast();
                String[] infoToArray = info.toString().split(SPACE_DELIMITER);

                if (infoToArray[0].equals(GET)) {
                    int index = Integer.parseInt(infoToArray[1]);
                    String value = cache.get(index);
                    msg.pollLast();
                    msg.addLast(value);
                    msg.send(cacheSocket);
                }

                if (infoToArray[0].equals(PUT)) {
                    int index = Integer.parseInt(infoToArray[1]);
                    String newValue = infoToArray[2];
                    cache.put(index, newValue);
                    msg.send(cacheSocket);
                }
            }
        }
    }

    private void heartbeat(long startTime) {
        if (System.currentTimeMillis() - startTime > TIMEOUT) {
            ZMsg m = new ZMsg();
            m.addLast(HEARTBEAT + SPACE_DELIMITER + leftBorder + SPACE_DELIMITER + rightBorder);
            m.send(cacheSocket);
        }
    }
}
