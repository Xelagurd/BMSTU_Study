package lab2;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ComponentKey implements WritableComparable<ComponentKey> {
    /*б. разрабатываем  WritableComparable ключа имеющий два столбца :
       AEROPORT_ID,индикатор набора данных (для набора данных с аэропортом = 0 , для перелетов =1)
       сортировка по умолчанию по двум столбцам */
    private String airportID;
    private int flag;

    public ComponentKey() {
        this.airportID = "init";
        this.flag = -1;
    }

    public ComponentKey(String airportID, int flag) {
        this.airportID = airportID;
        this.flag = flag;
    }

    public String getAirportID() {
        return airportID;
    }

    public int getflag() {
        return flag;
    }

    public void write(DataOutput out) throws IOException {
        out.writeChars(airportID);
        out.writeInt(flag);
    }

    public void readFields(DataInput in) throws IOException {
        String line = in.readLine();
        int size = line.length();
        flag = line.charAt(size - 1);
        airportID = line.substring(0, size - 1);
    }

    public int firstPartOnlyComparator(ComponentKey other) {
        return airportID.compareTo(other.getAirportID());
    }

    public int compareTo(ComponentKey other) {
        int x = airportID.compareTo(other.getAirportID());
        int y = other.getflag();
        if (x == 0) {
            return flag - y;
        }
        return x;
    }
}
