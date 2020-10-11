import java.util.ArrayList;
import java.util.List;

public class my_player {
    static class Agent {
        private final int currPlayerType;
        private final int[][] prevBoard;
        private final int[][] currBoard;
        private final Go go;
        private final List<Coordinate> bestEarlyMovesList = new ArrayList<>();
        private final int BEST_MOVES_USAGE_THRESHOLD = 3;

        public Agent(Go go) {
            this.go = go;
            final int[] currPlayerBuffer = new int[1];
            final int[][] prevBoardBuffer = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];
            final int[][] currBoardBuffer = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

            GameIO.readInput(currPlayerBuffer, prevBoardBuffer, currBoardBuffer);
            currPlayerType = currPlayerBuffer[0];
            prevBoard = prevBoardBuffer;
            currBoard = currBoardBuffer;

            this.go.setBoards(prevBoard, currBoard);
            initBestEarlyMoves();
        }

        private void initBestEarlyMoves() {
            bestEarlyMovesList.add(new Coordinate(2, 2));
//            bestEarlyMovesList.add(new Coordinate(2, 1));
//            bestEarlyMovesList.add(new Coordinate(2, 3));
//            bestEarlyMovesList.add(new Coordinate(1, 1));
//            bestEarlyMovesList.add(new Coordinate(1, 2));
            bestEarlyMovesList.add(new Coordinate(1, 3));
//            bestEarlyMovesList.add(new Coordinate(3, 1));
//            bestEarlyMovesList.add(new Coordinate(3, 2));
            bestEarlyMovesList.add(new Coordinate(3, 3));
        }

        public int getCurrPlayerType() {
            return currPlayerType;
        }

        public int[][] getCurrBoard() {
            return currBoard;
        }

        private String getNextMove() {
            final int numPieces = go.getTotalPieces();

            GameState bestState = null;

            if (numPieces < BEST_MOVES_USAGE_THRESHOLD) {
                for (Coordinate coordinate : bestEarlyMovesList) {
                    if (go.isEmpty(coordinate) && go.isValidCoordinate(coordinate, this)) {
                        return coordinate.toString();
                    }
                }
            }

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    GameState state = go.generateGameState(row, col, this);

                    if (state != null && (bestState == null || state.evaluateUtility() > bestState.evaluateUtility())) {
                        bestState = state;
                    }
                }
            }

            if (bestState == null) {
                return GameConfig.PASS_MOVE;
            }

//            System.out.println("my liberty");
//            GameIO.visualizeBoard(bestState.getBoard());
//            System.out.println(bestState.getLibertyList().size());

            return bestState.getCoordinate().toString();
        }
    }
    public static void main(String[] args) {
        final Go go = new Go();
        final Agent agent = new Agent(go);
        GameIO.writeNextMove(agent.getNextMove());
    }
}
