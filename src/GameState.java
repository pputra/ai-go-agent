import java.util.List;

public class GameState {
    private final int pieceType;
    private final int[][] board;
    private final int[][] prevBoard;
    private final List<Coordinate> deadEnemiesCoordinateList;
    private final List<Coordinate> libertyList;
    private final List<Coordinate> enemiesLibertyList;

    public GameState(int pieceType, int[][] board, int[][] prevBoard, List<Coordinate> deadEnemiesCoordinateList, List<Coordinate> libertyList, List<Coordinate> enemiesLibertyList) {
        this.pieceType = pieceType;
        this.board = board;
        this.prevBoard = prevBoard;
        this.deadEnemiesCoordinateList = deadEnemiesCoordinateList;
        this.libertyList = libertyList;
        this.enemiesLibertyList = enemiesLibertyList;
    }

    public GameState(int pieceType, int[][] board, int[][] prevBoard) {
        this.pieceType = pieceType;
        this.board = board;
        this.prevBoard = prevBoard;
        deadEnemiesCoordinateList = null;
        libertyList = null;
        enemiesLibertyList = null;
    }

    public int getPieceType() {
        return pieceType;
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][] getPrevBoard() {
        return prevBoard;
    }

    public double evaluateUtility() {
        return deadEnemiesCoordinateList.size() + 0.4 * libertyList.size() - 0.6 * enemiesLibertyList.size();
    }
}
