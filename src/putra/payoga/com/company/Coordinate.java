package putra.payoga.com.company;

public class Coordinate {
    private final int col;
    private final int row;
    private int pointType;

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }
}
