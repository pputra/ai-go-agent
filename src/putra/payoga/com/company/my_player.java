package putra.payoga.com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class my_player {
    static class Agent {
        private int currPlayerType;
        private final int[][] prevBoard;
        private final int[][] currBoard;
        private final Go go;

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
        }

        public int getCurrPlayerType() {
            return currPlayerType;
        }

        public int[][] getPrevBoard() {
            return prevBoard;
        }

        public int[][] getCurrBoard() {
            return currBoard;
        }

        public void setCurrPlayerType(int currPlayerType) {
            this.currPlayerType = currPlayerType;
        }

        private String getNextMove() {
            final List<Coordinate> possibleMoveList = new ArrayList<>();

            for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
                for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                    if (go.isValidCoordinate(row, col, this)) {
                        possibleMoveList.add(new Coordinate(row, col));
                    }
                }
            }

            if (possibleMoveList.isEmpty()) {
                return GameConfig.PASS_MOVE;
            }

            return possibleMoveList.get(new Random().nextInt(possibleMoveList.size())).toString();
        }

        private void printCurrState() {
            System.out.println(currPlayerType);
            System.out.println(Arrays.deepToString(prevBoard));
            System.out.println(Arrays.deepToString(currBoard));
        }
    }
    public static void main(String[] args) {
        final Go go = new Go();
        final Agent agent = new Agent(go);
        GameIO.writeNextMove(agent.getNextMove());
    }
}
