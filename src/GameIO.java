import java.io.*;
import java.util.Scanner;

public class GameIO {
    public static void writeNextMove(String nextMove) {
        try (FileWriter writer = new FileWriter(GameConfig.OUTPUT_FILE_NAME);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write(nextMove);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readInput(int[] currPlayerBuffer, int[][] prevBoardBuffer, int[][] currBoardBuffer) {
        try {
            FileInputStream f = new FileInputStream(GameConfig.INPUT_FILE_NAME);

            Scanner scanner = new Scanner(f);

            currPlayerBuffer[0] = Integer.parseInt(scanner.nextLine());

            readBoard(scanner, prevBoardBuffer);

            readBoard(scanner, currBoardBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readBoard(Scanner scanner, int[][] boardBuffer) {
        for (int i = 0; i < boardBuffer.length; i++) {
            String token = scanner.nextLine();

            String[] tokenCharArr = token.split("");

            for (int j = 0; j < boardBuffer[0].length; j++) {
                boardBuffer[i][j] = Integer.parseInt(tokenCharArr[j]);
            }
        }
    }

    public static void visualizeBoard(int[][] board) {
        for (int i = 0; i < GameConfig.BOARD_ROW_SIZE * 2; i++) {
            System.out.print("-");
        }
        System.out.println();
        for (int row = 0; row < GameConfig.BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < GameConfig.BOARD_COL_SIZE; col++) {
                final int currPiece = board[row][col];
                System.out.print(currPiece);
            }
            System.out.println();
        }

        for (int i = 0; i < GameConfig.BOARD_ROW_SIZE * 2; i++) {
            System.out.print("-");
        }

        System.out.println();
    }

    private static void writeNumStep(String numStep) {
        try (FileWriter writer = new FileWriter(GameConfig.NUM_STEP_FILE_NAME);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write(numStep);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int readNumSteps(int numPieces, int pieceType) {
        if (numPieces == 0) {
            final int currNumStep =  pieceType == PieceTypes.BLACK ? 1 : 2;

            writeNumStep(Integer.toString(currNumStep + 2));

            return currNumStep;
        }

        try {
            FileInputStream f = new FileInputStream(GameConfig.NUM_STEP_FILE_NAME);

            Scanner scanner = new Scanner(f);

            final int currNumStep =  Integer.parseInt(scanner.nextLine());

            writeNumStep(Integer.toString(currNumStep + 2));

            return currNumStep;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return -1;
        }
    }
}
