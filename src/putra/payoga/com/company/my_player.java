package putra.payoga.com.company;

import java.lang.reflect.Array;
import java.util.Arrays;

public class my_player {
    static class Agent {
        private int currPlayerType;
        private final int[][] prevBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];
        private final int[][] currBoard = new int[GameConfig.BOARD_ROW_SIZE][GameConfig.BOARD_COL_SIZE];

        public Agent() {
            int[] currPlayerBuffer = new int[1];
            GameIO.readInput(currPlayerBuffer, prevBoard, currBoard);
            currPlayerType = currPlayerBuffer[0];
            printCurrState();
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

        private void printCurrState() {
            System.out.println(currPlayerType);
            System.out.println(Arrays.deepToString(prevBoard));
            System.out.println(Arrays.deepToString(currBoard));
        }
    }
    public static void main(String[] args) {
        final Agent agent = new Agent();
    }
}
