import java.util.List;

public class GameState {
    private Coordinate coordinate;
    private final int[][] board;
    private List<Coordinate> deadEnemiesCoordinateList;
    private List<Coordinate> libertyList;
    private List<Coordinate> enemiesLibertyList;

    public GameState(Coordinate coordinate, int[][] board, List<Coordinate> deadEnemiesCoordinateList, List<Coordinate> libertyList, List<Coordinate> enemiesLibertyList) {
        this.coordinate = coordinate;
        this.board = board;
        this.deadEnemiesCoordinateList = deadEnemiesCoordinateList;
        this.libertyList = libertyList;
        this.enemiesLibertyList = enemiesLibertyList;
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

    public List<Coordinate> getLibertyList() {
        return libertyList;
    }

    public List<Coordinate> getEnemiesLibertyList() {
        return enemiesLibertyList;
    }

    public double evaluateUtility() {
        return deadEnemiesCoordinateList.size() + 0.4 * libertyList.size() - 0.6 * enemiesLibertyList.size();
    }
}
