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
}
