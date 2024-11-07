package lab2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

import javafx.util.Pair;

public class AirportTableMapper extends Mapper<LongWritable, Text, ComponentKey, Text> {
    /*в.разрабатываем map функцию для каждого из набора данных, которая генерирует  WritableComparable ключа и Writable данных
    для списка аэропортов эта функция в качестве value отправляет имя аэропорта.*/
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        AirportTableWritable obj = new AirportTableWritable(value.toString());
        Pair<String, String> pair = obj.getPairWithName();
        context.write(new ComponentKey(pair.getKey(), 0), new Text(pair.getValue()));
    }
}
