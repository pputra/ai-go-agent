import java.util.*;

public class my_player {
    static class Agent {
        private final int currPieceType;
        private final int[][] prevBoard;
        private final int[][] currBoard;
        private final Go go;
        final int numPieces;
        final int numSteps;
        private int maxDepth;

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
            numSteps = GameIO.readNumSteps(numPieces, currPieceType);
            initDepth();
        }

        public int getCurrPieceType() {
            return currPieceType;
        }

        public int[][] getCurrBoard() {
            return currBoard;
        }

        private List<Coordinate> getEmptyCenterCoordinates() {
            final List<Coordinate> emptyCenterList = new ArrayList<>();

            for (int row = 1; row < GameConfig.BOARD_ROW_SIZE - 1; row++) {
                for (int col = 1; col < GameConfig.BOARD_COL_SIZE - 1; col++) {
                    if (currBoard[row][col] == PieceTypes.EMPTY) {
                        emptyCenterList.add(new Coordinate(row, col));
                    }
                }
            }

            return emptyCenterList;
        }

        private void setDepth(int depth) {
            this.maxDepth = Math.min(depth, GameConfig.TOTAL_NUM_STEPS - numSteps);
        }

        private void initDepth() {
            setDepth(3);

            if (numSteps > 5) {
                setDepth(4);
            }

            if (numSteps > 16) {
                setDepth(5);
            }
        }

        private String getNextMinMaxMove() {
//            System.out.println("step: " + numSteps);
//            System.out.println("depth: " + maxDepth);
            final GameState prevGameState = new GameState( 3 - currPieceType, currBoard, prevBoard);

            if (numSteps < 10) {
                final List<Coordinate> emptyCenterList = getEmptyCenterCoordinates();

                Collections.shuffle(emptyCenterList);

                for (Coordinate coordinate : emptyCenterList) {
                    final GameState nextGameState = go.getNextState(coordinate, prevGameState);

                    if (nextGameState != null) {
                        return coordinate.toString();
                    }
                }
            }

            Coordinate bestCoordinate = null;

            double maxValue = -1 * Double.MAX_VALUE;

            final double ALPHA = -1 * Double.MAX_VALUE;

            final double BETA = Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, prevGameState);

                    if (nextState == null) {
                        continue;
                    }

                    final double currValue = minValue(0, nextState, ALPHA, BETA);

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

        private double maxValue(final int depth, final GameState gameState, double alpha, final double beta) {
            if (depth == this.maxDepth) {
                return gameState.evaluateUtility(numSteps + depth, currPieceType);
            }

            double value = -1 * Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.max(value, minValue(depth + 1, nextState, alpha, beta));
                    }

                    if (value >= beta) {
                        return value;
                    }

                    alpha = Math.max(alpha, value);
                }
            }

            return value;
        }

        private double minValue(final int depth, final GameState gameState, final double alpha, double beta) {
            if (depth == this.maxDepth) {
                return gameState.evaluateUtility(numSteps + depth, currPieceType);
            }

            double value = Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.min(value, maxValue(depth + 1, nextState, alpha, beta));
                    }

                    if (value <= alpha) {
                        return value;
                    }

                    beta = Math.min(beta, value);
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
