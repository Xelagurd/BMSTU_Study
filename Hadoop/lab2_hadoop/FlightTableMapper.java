package lab2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

import javafx.util.Pair;

public class FlightTableMapper extends Mapper<LongWritable, Text, ComponentKey, Text> {
    /*в.разрабатываем map функцию для каждого из набора данных, которая генерирует  WritableComparable ключа и Writable данных
    для списка рейсов в качестве value эта функция отправляет время задержки (в виде строки)*/
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        FlightTableWritable obj = new FlightTableWritable(value.toString());
        Pair<String, String> pair = obj.getDelayPair();
        context.write(new ComponentKey(pair.getKey(), 1), new Text(pair.getValue()));
    }
}
