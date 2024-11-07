package lab2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class AirportIDOnlyPartitioner extends Partitioner<ComponentKey, Text> {
    /*u. Разрабатываем Partitioner, который учитывает только код аэропорта */
    public AirportIDOnlyPartitioner() {
    }

    public int getPartition(ComponentKey key, Text value, int numReduceTasks) {
        int b = new Integer(key.getAirportID());
        return b % numReduceTasks;
    }
}
