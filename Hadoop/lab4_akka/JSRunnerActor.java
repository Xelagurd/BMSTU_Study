package lab4;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class JSRunnerActor extends AbstractActor {
    /*б. В приложении будем использовать следующие акторы :
      - актор который исполняет один тест из пакета.
      После исполнения теста результат передается актору хранилищу */
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(JSProgram.class, program -> {
                    sender().tell(
                            new StoreMessage(program.getPackageId(), program.getTest().getTestName(), program.run()), self());
                    System.out.println("JSRunnerActor get task: " + program.getTest().getTestName() +
                            "  with packageId: " + program.getPackageId() + "with result: " + program.run());
                })
                .build();
    }
}
