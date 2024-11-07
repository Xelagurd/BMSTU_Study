package lab2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class ReduceSideJoinReducer extends Reducer<ComponentKey, Text, Text, Text> {
    /*е. Разрабатываем reduce функцию, которая берет первую строку, извлекает из нее
     имя аэропорта, далее рассчитывает из последующих строк среднее минимальное и
      максимальное время задержки и печатает результат. */
    @Override
    protected void reduce(ComponentKey key, Iterable<Text> values, Context context) throws
            IOException, InterruptedException {
        Iterator it = values.iterator();
        Double min = 2e9, max = -2e9, currentDelay, sum = 0.0;
        int counter = 0;
        String airportName = it.next().toString();

        while (it.hasNext()) {
            String s = it.next().toString();
            if (s.length() == 0) {
                continue;
            }
            currentDelay = new Double(s);
            if (currentDelay == 0.0) {
                continue;
            }
            counter++;
            if (counter == 0 || max < currentDelay) {
                max = currentDelay;
            }
            if (counter == 0 || min > currentDelay) {
                min = currentDelay;
            }
            sum += currentDelay;
        }

        if (counter > 0) {
            String result = "".concat("Min: ".concat(min.toString().concat(", ")));
            result = result.concat("Max: ".concat(max.toString().concat(", ")));
            result = result.concat("Average: ".concat((new Double(Math.round((sum / counter) * 100.0) / 100.0)).toString()));

            context.write(new Text(airportName), new Text(result));
        }
    }
}
