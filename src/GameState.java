import java.util.List;

public class GameState {
    private Coordinate coordinate;
    private final int[][] board;
    private List<Coordinate> deadEnemiesCoordinateList;

    public GameState(Coordinate coordinate, int[][] board, List<Coordinate> deadEnemiesCoordinateList) {
        this.coordinate = coordinate;
        this.board = board;
        this.deadEnemiesCoordinateList = deadEnemiesCoordinateList;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int[][] getBoard() {
        return board;
    }

    public List<Coordinate> getDeadEnemiesCoordinateList() {
        return deadEnemiesCoordinateList;
    }
}
