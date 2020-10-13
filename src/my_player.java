public class my_player {
    static class Agent {
        private final int currPieceType;
        private final int[][] prevBoard;
        private final int[][] currBoard;
        private final Go go;
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
        }

        public int getCurrPieceType() {
            return currPieceType;
        }

        public int[][] getCurrBoard() {
            return currBoard;
        }

        private String getNextMinMaxMove() {
            final GameState rootGameState = new GameState(currPieceType, currBoard, prevBoard);

            int depth = 3;

            Coordinate bestCoordinate = null;

            double maxValue = -1 * Double.MAX_VALUE;

//            if (numPieces > 10) {
//                depth = 4;
//            }

            if (numPieces > 15) {
                depth = 5;
            }

            final double ALPHA = -1 * Double.MAX_VALUE;

            final double BETA = Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, rootGameState);

                    if (nextState == null) {
                        continue;
                    }

                    final double currValue = minValue(depth, nextState, ALPHA, BETA);

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
            if (depth == 0) {
                return gameState.evaluateUtility(numPieces);
            }

            double value = -1 * Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.max(value, minValue(depth - 1, nextState, alpha, beta));
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
            if (depth == 0) {
                return gameState.evaluateUtility(numPieces);
            }

            double value = Double.MAX_VALUE;

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    final Coordinate coordinate = new Coordinate(row, col);

                    final GameState nextState = go.getNextState(coordinate, gameState);

                    if (nextState != null) {
                        value = Math.min(value, maxValue(depth - 1, nextState, alpha, beta));
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
