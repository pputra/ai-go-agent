import java.util.*;
import java.util.stream.Collectors;

public class Go {
    private int[][] currBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

    private int[][] prevBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

    private Go deepCopyGameState(Go go) {
        final Go copiedGo = new Go();

        copiedGo.setBoards(deepCopyBoard(go.currBoard), deepCopyBoard(go.prevBoard));

        return copiedGo;
    }

    private int[][] deepCopyBoard(int[][] board) {
        int[][] copyBoard = new int[board.length][board[0].length];

        for (int row = 0; row < board.length; row++) {
            System.arraycopy(board[row], 0, copyBoard[row], 0, board.length);
        }

        return copyBoard;
    }

    public void setBoards(final int[][] currBoard, final int[][] prevBoard) {
        this.currBoard = currBoard;
        this.prevBoard = prevBoard;
    }

    public boolean compareBoard(int[][] board1, int[][] board2) {
        for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                if (board1[row][col] != board2[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }

    public GameState getNextState(final Coordinate coordinate, final GameState gameState) {
        final int row = coordinate.getRow();
        final int col = coordinate.getCol();
        final int currPieceType = gameState.getPieceType();
        final int enemiesPieceType = 3 - currPieceType;

        currBoard = deepCopyBoard(gameState.getBoard());

        if (!isWithinBoundary(row, col) || currBoard[row][col] != PieceTypes.EMPTY) {
            return null;
        }

        final Go testGo = deepCopyGameState(this);

        testGo.currBoard[row][col] = currPieceType;

        final List<Coordinate> deadPiecesCoordinateList = testGo.findDeadPiecesCoordinates(enemiesPieceType);

        testGo.removePiecesFromTheBoard(deadPiecesCoordinateList);

        if (!testGo.hasLiberty(row, col)) {
            return null;
        }

        if (compareBoard(gameState.getPrevBoard(), testGo.currBoard)) {
            return null;
        }

        final List<Coordinate> libertyList = testGo.getLibertyList(gameState.getPieceType());

        final List<Coordinate> enemiesLibertyList = testGo.getLibertyList(enemiesPieceType);

        return new GameState(enemiesPieceType, testGo.currBoard, gameState.getBoard(), deadPiecesCoordinateList, libertyList, enemiesLibertyList);
    }

    private boolean isWithinBoundary(final int row, final int col) {
        if (!(row >= 0 && row < currBoard.length)) {
            return false;
        }

        return col >= 0 && col < currBoard[0].length;
    }

    public boolean isValidCoordinate(Coordinate coordinate, final my_player.Agent agent) {
        currBoard = agent.getCurrBoard();

        final int row = coordinate.getRow();
        final int col = coordinate.getCol();

        if (!isWithinBoundary(row, col)) {
            return false;
        }

        if (currBoard[row][col] != PieceTypes.EMPTY) {
            return false;
        }

        final Go testGo = deepCopyGameState(this);

        testGo.currBoard[row][col] = agent.getCurrPieceType();

        if (testGo.hasLiberty(row, col)) {
            return true;
        }

        testGo.removeDeadPieces(3 - agent.getCurrPieceType());

        if (!testGo.hasLiberty(row, col)) {
            return false;
        }

        return !compareBoard(prevBoard, testGo.currBoard);
    }

    private boolean hasLiberty(final int row, final int col) {
        final List<Coordinate> neighboringAllyCoordinateList = findAllyUsingDfs(row, col);

        for (Coordinate ally : neighboringAllyCoordinateList) {
            final List<Coordinate> neighbors = getNeighbors(ally.getRow(), ally.getCol());

            for (Coordinate neighbor : neighbors) {
                if (currBoard[neighbor.getRow()][neighbor.getCol()] == PieceTypes.EMPTY) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Coordinate> getLibertyList(int pieceType) {
        Set<Coordinate> libertySet = new HashSet<>();

        for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                if (currBoard[row][col] == pieceType) {
                    List<Coordinate> neighborList = getNeighbors(row, col);

                    for (Coordinate neighbor : neighborList) {
                        if (currBoard[neighbor.getRow()][neighbor.getCol()] == PieceTypes.EMPTY) {
                            libertySet.add(neighbor);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(libertySet);
    }

    private List<Coordinate> findAllyUsingDfs(final int row, final int col) {
        List<Coordinate> allyMemberList = new ArrayList<>();
        final Stack<Coordinate> stack = new Stack<>();
        stack.push(new Coordinate(row, col));

        // TODO: OPTIMIZE THIS USING SET
        while (!stack.empty()) {
            final Coordinate coordinate = stack.pop();
            allyMemberList.add(coordinate);
            final List<Coordinate> neighborAllyList = getNeighboringAlly(coordinate.getRow(), coordinate.getCol());

            for (Coordinate neighborAlly : neighborAllyList) {
                if (!stack.contains(neighborAlly) && !allyMemberList.contains(neighborAlly)) {
                    stack.add(neighborAlly);
                }
            }
        }

        return allyMemberList;
    }

    private List<Coordinate> getNeighboringAlly(final int row, final int col) {
        final List<Coordinate> neighborsList = getNeighbors(row, col);

        return neighborsList.stream()
                .filter(c -> currBoard[row][col] == currBoard[c.getRow()][c.getCol()])
                .collect(Collectors.toList());
    }

    private List<Coordinate> getNeighbors(final int row, final int col) {
        final List<Coordinate> neighborsList = new ArrayList<>();

        if (row > 0) {
            neighborsList.add(new Coordinate(row - 1, col));
        }

        if (row < GameConfig.BOARD_ROW_SIZE - 1) {
            neighborsList.add(new Coordinate(row + 1, col));
        }

        if (col > 0) {
            neighborsList.add(new Coordinate(row, col - 1));
        }

        if (col < GameConfig.BOARD_COL_SIZE - 1) {
            neighborsList.add(new Coordinate(row, col + 1));
        }

        return neighborsList;
    }

    private List<Coordinate> findDeadPiecesCoordinates(final int pieceType) {
        final List<Coordinate> deadPiecesCoordinateList = new ArrayList<>();

        for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                if (currBoard[row][col] == pieceType && !hasLiberty(row, col)) {
                    deadPiecesCoordinateList.add(new Coordinate(row, col));
                }
            }
        }

        return deadPiecesCoordinateList;
    }


    private List<Coordinate> removeDeadPieces(final int pieceType) {

        return findDeadPiecesCoordinates(pieceType);
    }

    private void removePiecesFromTheBoard(final List<Coordinate> deadPiecesCoordinateList) {
        for (Coordinate coordinate : deadPiecesCoordinateList) {
            currBoard[coordinate.getRow()][coordinate.getCol()] = PieceTypes.EMPTY;
        }
    }

    public boolean isEmpty(Coordinate coordinate) {
        return currBoard[coordinate.getRow()][coordinate.getCol()] == PieceTypes.EMPTY;
    }

    public int getTotalPieces() {
        int numPieces = 0;

        for (int row = 0; row < currBoard.length; row++) {
            for (int col = 0; col < currBoard[0].length; col++) {
                if (currBoard[row][col] != PieceTypes.EMPTY) {
                    numPieces++;
                }
            }
        }

        return numPieces;
    }
}
