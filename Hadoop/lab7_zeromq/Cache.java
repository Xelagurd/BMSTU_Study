package lab7;

public class Cache {
    private String leftBorder, rightBorder;
    private long time;

    public Cache(String leftBorder, String rightBorder, long time) {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.time = time;
    }

    public String getLeftBorder() {
        return leftBorder;
    }

    public String getRightBorder() {
        return rightBorder;
    }

    public boolean isIntersect(String a) {
        int req = Integer.parseInt(a);
        int leftBorderInt = Integer.parseInt(leftBorder);
        int rightBorderInt = Integer.parseInt(rightBorder);
        return leftBorderInt <= req && req <= rightBorderInt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }
}
