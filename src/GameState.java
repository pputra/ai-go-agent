import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameState {
    private final int pieceType;
    private final int[][] board;
    private final int[][] prevBoard;
    private final List<Coordinate> deadEnemiesCoordinateList;
    private List<Coordinate> libertyList;
    private List<Coordinate> enemiesLibertyList;

    public GameState(int pieceType, int[][] board, int[][] prevBoard, List<Coordinate> deadEnemiesCoordinateList) {
        this.pieceType = pieceType;
        this.board = board;
        this.prevBoard = prevBoard;
        this.deadEnemiesCoordinateList = deadEnemiesCoordinateList;
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

    public double evaluateUtility(final int numPieces) {
        final double piecesCountDiff = getPiecesCountDiff();

        final int eyesCountDiff = getEyesCountDiff();

        initAllLibertyList();

        if (numPieces < 10) {
            return eyesCountDiff + libertyList.size() - enemiesLibertyList.size();
        }

        return eyesCountDiff  + piecesCountDiff + 0.4 * libertyList.size() - 0.6 * enemiesLibertyList.size();
    }

    private void initAllLibertyList() {
        final Go testGo = new Go();

        testGo.setBoards(this.board, prevBoard);

        libertyList = testGo.getLibertyList(pieceType);

        enemiesLibertyList = testGo.getLibertyList(3 - pieceType);
    }

    private double getPiecesCountDiff() {
        int allyCount = 0;
        int enemiesCount = 0;

        for (int[] rows : board) {
            for (int col = 0; col < board.length; col++) {
                if (rows[col] == pieceType) {
                    allyCount++;
                } else if (rows[col] == (3 - pieceType)) {
                    enemiesCount++;
                }
            }
        }

        if (pieceType == PieceTypes.WHITE) {
            allyCount += GameConfig.KOMI;
        } else {
            enemiesCount += GameConfig.KOMI;
        }

        return allyCount - enemiesCount;
    }

    private int getEyesCountDiff() {
        int allyCount = 0;
        int enemiesCount = 0;

        final Go testGo = new Go();

        testGo.setBoards(board, prevBoard);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (pieceType == PieceTypes.EMPTY) {
                    final List<Coordinate> emptyNeighborsList = testGo.getNeighbors(row, col);

                    Set<Integer> neighborSet = new HashSet<>();

                    for (Coordinate neighbor : emptyNeighborsList) {
                        neighborSet.add(board[neighbor.getRow()][neighbor.getCol()]);
                    }

                    if (neighborSet.size() == 1) {
                        if (neighborSet.contains(pieceType)) {
                            allyCount++;
                        } else if (neighborSet.contains(3 - pieceType)) {
                            enemiesCount++;
                        }
                    }
                }
            }
        }

        return allyCount - enemiesCount;
    }
}
