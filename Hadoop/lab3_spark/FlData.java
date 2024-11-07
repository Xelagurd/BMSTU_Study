package lab3;

import java.io.Serializable;

public class FlData implements Serializable {

    /*г. Создаем Java объекты для хранения данных — простые объекты реализующие интерфейс Serializable */
    private int departApId;
    private int arrivalApId;
    private boolean delayed;
    private boolean canceled;
    private double delay;

    private static final int ARRIVAL_INDEX = 14;
    private static final int DEPART_INDEX = 11;
    private static final int CANCELED_INDEX = 19;
    private static final int DELAY_INDEX = 17;

    public FlData(String flData) throws NumberFormatException {
        String[] splitData = flData.split(",");
        this.departApId = Integer.parseInt(splitData[DEPART_INDEX]);
        this.arrivalApId = Integer.parseInt(splitData[ARRIVAL_INDEX]);
        canceled = splitData[CANCELED_INDEX].charAt(0) == '1';

        if (!canceled & splitData[DELAY_INDEX].length() != 0) {
            delay = Double.parseDouble(splitData[DELAY_INDEX]);
        } else {
            delay = 0;
        }

        if (delay > 0)
            delayed = true;
    }

    public int getDepartApId() {
        return departApId;
    }

    public int getArrivalApId() {
        return arrivalApId;
    }

    public double getDelay() {
        return delay;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public int getCanceled() {
        return canceled ? 1 : 0;
    }

    public int getDelayed() {
        return delayed ? 1 : 0;
    }
}