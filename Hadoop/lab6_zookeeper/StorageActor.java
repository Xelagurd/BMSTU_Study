package lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Random;

public class StorageActor extends AbstractActor {
    private ArrayList<String> servers = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StoreServersMessage.class, m -> {
                    servers = m.getServers();
                })
                .match(GetRandomServerMessage.class, m -> {
                    ArrayList<String> serversCopy = new ArrayList<>(servers);
                    serversCopy.remove(m.getPort());
                    Random rand = new Random();
                    int len = serversCopy.size();
                    getSender().tell(Integer.parseInt(serversCopy.get(rand.nextInt(len))), ActorRef.noSender());
                }).build();
    }
}
