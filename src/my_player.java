import java.util.ArrayList;
import java.util.List;

public class my_player {
    static class Agent {
        private final int currPieceType;
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
            currPieceType = currPlayerBuffer[0];
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

        public int getCurrPieceType() {
            return currPieceType;
        }

        public int[][] getCurrBoard() {
            return currBoard;
        }

        private String getNextMove() {
            final int numPieces = go.getTotalPieces();

            if (numPieces < BEST_MOVES_USAGE_THRESHOLD) {
                for (Coordinate coordinate : bestEarlyMovesList) {
                    if (go.isEmpty(coordinate) && go.isValidCoordinate(coordinate, this)) {
                        return coordinate.toString();
                    }
                }
            }

            final GameState rootGameState = new GameState(currPieceType, currBoard, prevBoard);

            GameState bestState = null;

            Coordinate bestCoordinate = null;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    GameState state = go.getNextState(coordinate, rootGameState);

                    if (state != null && (bestCoordinate == null || state.evaluateUtility() > bestState.evaluateUtility())) {
                        bestState = state;

                        bestCoordinate = new Coordinate(row, col);
                    }
                }
            }

            if (bestState == null) {
                return GameConfig.PASS_MOVE;
            }

            return bestCoordinate.toString();
        }
    }
    public static void main(String[] args) {
        final Go go = new Go();

        final Agent agent = new Agent(go);

        GameIO.writeNextMove(agent.getNextMove());
    }
}
