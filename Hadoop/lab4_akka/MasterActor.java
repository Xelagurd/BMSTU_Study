package lab4;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;

public class MasterActor extends AbstractActor {
    /*б. В приложении будем использовать следующие акторы :
        - актор роутер
        инициализирует актор хранилище а также пул акторов исполнителей тестов */

    private final ActorRef testers;
    private final ActorRef storage;

    public MasterActor() {
        testers = getContext().actorOf(new RoundRobinPool(5).props(Props.create(JSRunnerActor.class)), "t");
        storage = getContext().actorOf(Props.create(StorageActor.class));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(JSProgram.class, m -> {
                    for (Test test : m.getTests()) {
                        JSProgram cur = new JSProgram(m);
                        cur.setTest(test);
                        testers.tell(cur, storage);
                    }
                })
                .match(GetMessage.class, req -> storage.tell(
                        req, sender()))
                .build();
    }

}
