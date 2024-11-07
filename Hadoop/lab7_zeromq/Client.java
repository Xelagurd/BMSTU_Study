package lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Scanner;

public class Client {
    public static final String CLIENT_SOCKET = "tcp://localhost:2052";
    private static final String CLIENT_ALIVE = "Client is online";

    private ZMQ.Socket client;
    private Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Client client = new Client();
        client.clientInitialization();
        client.workWithProxy();
    }

    private void clientInitialization() {
        ZContext context = new ZContext();
        client = context.createSocket(SocketType.REQ);
        client.setHWM(0);
        client.connect(CLIENT_SOCKET);
        System.out.println(CLIENT_ALIVE);
    }

    private void workWithProxy() {
        while (true) {
            getRequestAndSendToProxy();
            if (getAnswerFromProxy() == -1)
                break;
        }
    }

    private void getRequestAndSendToProxy() {
        String text = in.nextLine();
        ZMsg m = new ZMsg();
        m.addString(text);
        m.send(client);
    }

    private int getAnswerFromProxy() {
        ZMsg req = ZMsg.recvMsg(client);
        if (req == null) {
            return -1;
        }
        System.out.println(req.getLast().toString());
        req.destroy();
        return 0;
    }
}