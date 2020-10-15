import java.util.ArrayList;
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

    public double evaluateUtility(final int numSteps, final int currPieceType) {
        final int piecesCountDiff = getPiecesCountDiff(currPieceType);

        if (numSteps == GameConfig.TOTAL_NUM_STEPS) {
            return addKomi(piecesCountDiff, currPieceType);
        }

        final int eyesCountDiff = getEyesCountDiff(currPieceType);

        initAllLibertyList(currPieceType);

        return eyesCountDiff * 2 + libertyList.size() - enemiesLibertyList.size() + addKomi(piecesCountDiff, currPieceType);
    }

    private double addKomi(final int piecesCountDiff, final int pieceType) {
        return pieceType == PieceTypes.WHITE ? piecesCountDiff + GameConfig.KOMI : piecesCountDiff - GameConfig.KOMI;
    }

    private void initAllLibertyList(final int pieceType) {
        final Go testGo = new Go();

        testGo.setBoards(this.board, prevBoard);

        libertyList = testGo.getLibertyList(pieceType);

        enemiesLibertyList = testGo.getLibertyList(3 - pieceType);
    }

    private int getPiecesCountDiff(final int pieceType) {
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

        return allyCount - enemiesCount;
    }

    private int getEyesCountDiff(final int pieceType) {
        int allyCount = 0;
        int enemiesCount = 0;

        final Go testGo = new Go();

        testGo.setBoards(board, prevBoard);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == PieceTypes.EMPTY) {
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

    public boolean isRemovingEye(final Coordinate coordinate, final int pieceType) {
        final Go testGo = new Go();

        testGo.setBoards(board, prevBoard);

        final List<Coordinate> neighborList = testGo.getNeighbors(coordinate.getRow(), coordinate.getCol());

        final Set<Integer> neighborSet = new HashSet<>();

        for (Coordinate neighbor : neighborList) {
            neighborSet.add(board[neighbor.getRow()][neighbor.getCol()]);
        }

        return neighborSet.size() == 1 && neighborSet.contains(pieceType);
    }
}
