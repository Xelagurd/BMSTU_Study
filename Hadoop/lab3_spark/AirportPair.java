package lab3;

import java.io.Serializable;

public class AirportPair implements Serializable {
    /*г. Создаем Java объекты для хранения данных — простые объекты реализующие интерфейс Serializable */
    private int allFl;
    private int canceledFl;
    private int delayedFl;
    private double maxDelay;

    public AirportPair(int allFl, int canceledFl, int delayedFl, double maxDelay) {
        this.allFl = allFl;
        this.canceledFl = canceledFl;
        this.delayedFl = delayedFl;
        this.maxDelay = maxDelay;
    }

    public int getAllFl() {
        return allFl;
    }

    public int getDelayedFl() {
        return delayedFl;
    }

    public int getCanceledFl() {
        return canceledFl;
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public double getDelPer() {
        return 100.0 * ((double) this.delayedFl / this.allFl);
    }

    public double getCancPer() {
        return 100.0 * ((double) this.canceledFl / this.allFl);
    }

    public static AirportPair add(AirportPair a, AirportPair b) {
        return new AirportPair(
                a.allFl + b.allFl,
                a.canceledFl + b.canceledFl,
                a.delayedFl + b.delayedFl,
                Math.max(a.getMaxDelay(), b.getMaxDelay())
        );
    }

    public AirportPair addFl(int canceledFl, int delayedFl, double maxDelay) {
        return new AirportPair(
                this.allFl + 1,
                this.canceledFl + canceledFl,
                this.delayedFl + delayedFl,
                Math.max(this.maxDelay, maxDelay)
        );
    }

    public String toString() {
        return "Max delay: " + Double.toString(this.maxDelay) +
                " Percent of delayed flights: " + Double.toString(this.getDelPer()) +
                " Percent of canceled flights: " + Double.toString(this.getCancPer());
    }
}