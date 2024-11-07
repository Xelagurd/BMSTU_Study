package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class Spark {
    public static void main(String[] args) {
        /*а. Инициализируем Spark */
        SparkConf conf = new SparkConf().setAppName("Lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        /*б. Загружаем исходные наборы данных в RDD*/
        JavaRDD<String> flFile = sc.textFile("664600583_T_ONTIME_sample.csv");
        /*в. Преобразуем RDD в RDD пару ключ значение */
        JavaPairRDD<Tuple2, FlData> flData = flFile.mapToPair(s -> {
            FlData fd = new FlData(s);
            return new Tuple2<>(new Tuple2<>(fd.getDepartApId(), fd.getArrivalApId()), fd);
        });

        /*д. В качестве ключа для пары аэропортов используем класс Tuple2 */
        JavaPairRDD<Tuple2, AirportPair> combineData = flData.combineByKey(
                a -> new AirportPair(
                        1,
                        a.getCanceled(),
                        a.getDelayed(),
                        a.getDelay()
                ),
                (pair, a) -> pair.addFl(
                        a.getCanceled(),
                        a.getDelayed(),
                        a.getDelay()
                ),
                AirportPair::add
        );

        JavaRDD<String> nameFile = sc.textFile("L_AIRPORT_ID.csv");

        /*е. для связывания с таблицей аэропортов — предварительно выкачиваем список
        аэропортов с помощью метода collectAsMap */
        Map<Integer, String> toPair = nameFile.mapToPair(
                p -> {
                    String[] splitAp = p.split(",");
                    return new Tuple2<>(Integer.parseInt(splitAp[0]), splitAp[1]);
                }
        ).collectAsMap();

        /*ё. создаем в основном методе main переменную broadcast */
        final Broadcast<Map<Integer, String>> broadcastAp = sc.broadcast(toPair);

        /*ж. в методе map преобразуем данные из итогового RDD для вывода, обогащая его именами аэропортов*/
        System.out.println(combineData.map(s -> {
            return broadcastAp.getValue().get((Integer) s._1._1) + "\" " +
                    broadcastAp.getValue().get((Integer) s._1._2) + "\" " +
                    s._2.toString() + "\n";
        }).collect());
    }
}
