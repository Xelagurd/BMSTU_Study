package lab2;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class AirportIDOnlyComparator extends WritableComparator {
    /*д.  Разрабатываем GroupingComparatorClass, который учитывает только  код аэропорта */
    public AirportIDOnlyComparator() {
        super(ComponentKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        ComponentKey x = (ComponentKey) a;
        ComponentKey y = (ComponentKey) b;

        return x.firstPartOnlyComparator(y);
    }
}
