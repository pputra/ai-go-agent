import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Go {
    private int[][] currBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

    private int[][] prevBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

    private final List<Coordinate> deadCoordinateList = new ArrayList<>();

    public int[][] getCurrBoard() {
        return currBoard;
    }

    public int[][] getPrevBoard() {
        return prevBoard;
    }

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

        for (int row = 0; row < currBoard.length; row++) {
            for (int col = 0; col < currBoard[0].length; col++) {
                if (prevBoard[row][col] != currBoard[row][col]) {
                    deadCoordinateList.add(new Coordinate(row, col));
                }
            }
        }
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

    public GameState generateGameState(final int row, final int col, final my_player.Agent agent) {
        currBoard = deepCopyBoard(agent.getCurrBoard());
//        System.out.println("before placement");
//        GameIO.visualizeBoard(currBoard);
        final Go testGo = deepCopyGameState(this);
//        System.out.println("after placement");
        testGo.currBoard[row][col] = agent.getCurrPlayerType();
//        GameIO.visualizeBoard(testGo.currBoard);
        final List<Coordinate> deadPiecesCoordinateList = testGo.findDeadPiecesCoordinates(3 - agent.getCurrPlayerType());
        testGo.removePiecesFromTheBoard(deadPiecesCoordinateList);

//        if (!deadPiecesCoordinateList.isEmpty()) {
//
//            System.out.println(deadPiecesCoordinateList);
//            System.out.println("after removal");
//            GameIO.visualizeBoard(testGo.currBoard);
//        }

        return new GameState(new Coordinate(row, col), currBoard, deadPiecesCoordinateList);
    }

    public boolean isValidCoordinate(final int row, final int col, final my_player.Agent agent) {
        currBoard = deepCopyBoard(agent.getCurrBoard());

        if (!(row >= 0 && row < currBoard.length)) {
            return false;
        }

        if (!(col >= 0 && col < currBoard[0].length)) {
            return false;
        }

        if (currBoard[row][col] != PieceTypes.EMPTY) {
            return false;
        }

        final Go testGo = deepCopyGameState(this);

        testGo.currBoard[row][col] = agent.getCurrPlayerType();

        if (testGo.hasLiberty(row, col)) {
            return true;
        }

        testGo.removeDeadPieces(3 - agent.getCurrPlayerType());

        if (!testGo.hasLiberty(row, col)) {
            return false;
        }

        return deadCoordinateList.isEmpty() || !compareBoard(prevBoard, testGo.currBoard);
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
