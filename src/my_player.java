import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class my_player {
    static class Agent {
        private final int currPieceType;
        private final int[][] prevBoard;
        private final int[][] currBoard;
        private final Go go;
        private final List<Coordinate> bestEarlyMovesList = new ArrayList<>();
        private final int BEST_MOVES_USAGE_THRESHOLD = 3;
        final int numPieces;

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
            numPieces = go.getTotalPieces();
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

        private String getNextGreedyMove() {
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

                    if (state != null && (bestCoordinate == null || state.evaluateUtility(numPieces) > bestState.evaluateUtility(numPieces))) {
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

        private String getNextMinMaxMove() {
            final GameState rootGameState = new GameState(currPieceType, currBoard, prevBoard);
            int depth = 3;

            Coordinate bestCoordinate = null;
            double maxValue = -1 * Double.MAX_VALUE;

            if (numPieces > 10) {
                depth = 5;
            }

            if (numPieces > 15) {
                depth = 7;
            }

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, rootGameState);

                    if (nextState == null) {
                        continue;
                    }

                    final double currValue = minValue(depth, nextState);

                    if (currValue > maxValue) {
                        bestCoordinate = coordinate;
                        maxValue = currValue;
                    }
                }
            }

            if (bestCoordinate == null) {
                return GameConfig.PASS_MOVE;
            }

            return bestCoordinate.toString();
        }

        private double maxValue(final int depth, final GameState gameState) {
            if (depth == 0) {
                return gameState.evaluateUtility(numPieces);
            }

            double value = -1 * Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.max(value, minValue(depth - 1, nextState));
                    }
                }
            }

            return value;
        }

        private double minValue(int depth, GameState gameState) {
            if (depth == 0) {
                return gameState.evaluateUtility(numPieces);
            }

            double value = Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.min(value, maxValue(depth - 1, nextState));
                    }
                }
            }

            return value;
        }
    }

    public static void main(String[] args) {
        final Go go = new Go();

        final Agent agent = new Agent(go);

        GameIO.writeNextMove(agent.getNextMinMaxMove());
    }
}
