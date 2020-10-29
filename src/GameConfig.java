public class GameConfig {
    public static final int BOARD_ROW_SIZE = 5;
    public static final int BOARD_COL_SIZE = 5;
    public static final String INPUT_FILE_NAME = "input.txt";
    public static final String OUTPUT_FILE_NAME = "output.txt";
    public static final String NUM_STEP_FILE_NAME = "num_step.txt";
    public static final String PASS_MOVE = "PASS";
    public static final double KOMI = 2.5;
    private static final int TOTAL_NUM_STEPS_WHITE = 24;
    private static final int TOTAL_NUM_STEPS_BLACK = 23;

    public static int getTotalNumSteps(final int pieceType) {
        if (pieceType == PieceTypes.BLACK) {
            return TOTAL_NUM_STEPS_BLACK;
        }

        return TOTAL_NUM_STEPS_WHITE;
    }
}
