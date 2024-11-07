package lab4;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class JSTester extends AllDirectives {

    static ActorRef master;

    public static void main(String[] args) throws IOException {
        /*а. Создаем actor system*/
        ActorSystem systemq = ActorSystem.create("routes");
        /*в. После инициализации actor system — создаем актор роутер который в свою очередь создает все дочерние акторы */
        master = systemq.actorOf(Props.create(MasterActor.class));
        final Http httpser = Http.get(systemq);
        /*г. Создаем ActorMaterializer и инициализируем http систему */
        final ActorMaterializer materializer = ActorMaterializer.create(systemq);

        JSTester instance = new JSTester();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                instance.createRoute().flow(systemq, materializer);
        final CompletionStage<ServerBinding> binding = httpser.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> systemq.terminate());

    }

    private Route createRoute() {
        /*д. Cтроим дерево route и пишем обработчики запросов. */
        return route(
                get(() ->
                        /*ё. В случае запроса на получение информации о тесте — используем Putterns.ask и возвращаем Future с ответом */
                        parameter("packageId", (packageId) -> {
                            Future<Object> result = Patterns.ask(master,
                                    new GetMessage(packageId), 5000);
                            return completeOKWithFuture(result, Jackson.marshaller());
                        })),
                post(() -> {
                    /*е. Когда приходит запрос на запуск теста — запускаем тест и сразу овтечаем константным ответом. */
                    System.out.println("get Post message");
                    return entity(Jackson.unmarshaller(JSProgram.class), msg -> {
                        master.tell(msg, ActorRef.noSender());
                        return complete("Test started");
                    });
                }));
    }
}

