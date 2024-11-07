package lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class AkkaServer extends AllDirectives {
    private static int port;
    private static ActorRef storageActor;
    private static ZooKeeper zoo;
    private static Http http;

    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final String SERVER_ONLINE = "Server online on localhost:";
    private static final String ZOOKEEPER_HOST = "127.0.0.1:2181";
    static final String ZOOKEEPER_SERVERS_DIR = "/servers";
    static final String ZOOKEEPER_SERVER_DIR = "/servers/";
    private static final String URL = "url";
    private static final String COUNT = "count";
    private static final String URL_ERROR = "Cant connect to url";
    private static final String NOT_FOUND = "404";
    private static final int TIMEOUT = 5000;

    public static void main(String[] args) throws Exception {
        AkkaServer akkaServer = new AkkaServer();

        Scanner in = new Scanner(System.in);
        port = in.nextInt();

        ActorSystem system = ActorSystem.create(ROUTES);
        storageActor = system.actorOf(Props.create(StorageActor.class));

        zoo = new ZooKeeper(
                ZOOKEEPER_HOST,
                TIMEOUT,
                new CustomWatcher()
        );
        /*
        zoo.create(
                ZOOKEEPER_SERVERS_DIR,
                Integer.toString(0).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
        */
        zoo.create(
                ZOOKEEPER_SERVER_DIR + port,
                Integer.toString(port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL
        );

        zoo.getChildren(ZOOKEEPER_SERVERS_DIR, new CustomWatcher());

        http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = akkaServer.route().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(LOCALHOST, port),
                materializer
        );

        System.out.println(SERVER_ONLINE + port);
        System.in.read();

        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    public static class CustomWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            List<String> servers = new ArrayList<>();
            try {
                servers = zoo.getChildren(ZOOKEEPER_SERVERS_DIR, this);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<String> serversData = new ArrayList<>();

            for (String server : servers) {
                byte[] data = new byte[0];
                try {
                    data = zoo.getData(ZOOKEEPER_SERVER_DIR + server, false, null);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                serversData.add(new String(data));
            }

            storageActor.tell(new StoreServersMessage(serversData), ActorRef.noSender());
        }
    }

    private Route route() {
        return concat(
                get(
                        () -> parameter(URL, url ->
                                parameter(COUNT, count -> {
                                            int countInt = Integer.parseInt(count);
                                            if (countInt != 0) {
                                                CompletionStage<HttpResponse> response = Patterns
                                                        .ask(
                                                                storageActor,
                                                                new GetRandomServerMessage(Integer.toString(port)),
                                                                java.time.Duration.ofMillis(TIMEOUT)
                                                        )
                                                        .thenCompose(
                                                                port -> fetchToServer(
                                                                        (int) port,
                                                                        url,
                                                                        countInt
                                                                )
                                                        );
                                                return completeWithFuture(response);
                                            }

                                            try {
                                                return complete(fetch(url).toCompletableFuture().get());
                                            } catch (InterruptedException | ExecutionException e) {
                                                e.printStackTrace();
                                                return complete(URL_ERROR);
                                            }
                                        }
                                )
                        )
                )
        );

    }

    CompletionStage<HttpResponse> fetchToServer(int port, String url, int count) {
        try {
            return http.singleRequest(HttpRequest.create("http://localhost:" + port + "/?url=" + url + "&count=" + (count - 1)));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(HttpResponse.create().withEntity(NOT_FOUND));
        }
    }

    CompletionStage<HttpResponse> fetch(String url) {
        try {
            return http.singleRequest(HttpRequest.create(url));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(HttpResponse.create().withEntity(NOT_FOUND));
        }
    }
}
