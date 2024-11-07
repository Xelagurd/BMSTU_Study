package lab2;

import javafx.util.Pair;

public class AirportTableWritable {
    /*а.Разрабатываем Writable для каждого из входных наборов данных, который может читать данные из csv. */
    private Pair<String, String> pair;

    public AirportTableWritable(String table) {
        int index;
        String id, name;

        if (!table.contains("Description")) {
            index = table.indexOf(",");
            id = table.substring(0, index);
            name = table.substring(index + 2, table.length() - 1);
            this.pair = new Pair<>(id, name);
        }
    }

    public javafx.util.Pair<String, String> getPairWithName() {
        return pair;
    }
}