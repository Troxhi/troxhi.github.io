import java.util.Arrays;

public class Minesweeper {

    public static void main(String[] args) {
        int[][] board = {
                {-1, -1, -2, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -2, -1},
        };

        int count = computeMineCount(board, 1, 1);
        System.out.println(count);

        gameTurn(board, 1, 1);
        gameTurn(board, 2, 1);
        gameTurn(board, 4, 3);
        System.out.println(Arrays.deepToString(board));

        revealAllMines(board);
        System.out.println(Arrays.deepToString(board));
    }

    // we may assume that the board is rectangular
    public static void revealAllMines(int[][] board) {
        int rows = board.length;
        int columns = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == -2) {
                    board[i][j] = 9;
                }
            }
        }
    }

    public static int computeMineCount(int[][] board, int row, int col) {
        if (board[row][col] == -2) {
            return 9;
        }

        int count = 0;
        for (int d = -1; d <= 1; d++) {
            for (int e = -1; e <= 1; e++) {
                if (!isCellAccessible(board, row+d, col+e)) {
                    continue;
                }
                int cell = board[row+d][col+e];
                // Since we assume that the game hasn't ended we can safely
                // assume that no mine was uncovered at this time
                if (cell == -2) {
                    count++;
                }
            }
        }

        return count;
    }

    private static boolean isCellAccessible(int[][] board, int row, int col) {
        int rows = board.length;
        int columns = board[0].length;
        boolean validRow = 0 <= row && row < rows;
        boolean validColumn = 0 <= col && col < columns;

        return validRow && validColumn;
    }

    public static void gameTurn(int[][] board, int row, int col) {
        boolean alreadyUncovered = 0 <= board[row][col] && board[row][col] <= 8;
        if (alreadyUncovered) {
            return;
        }
        boolean isMineCell = board[row][col] == -2;
        if (isMineCell) {
            revealAllMines(board);
            return;
        }

        int mineCount = computeMineCount(board, row, col);
        board[row][col] = mineCount;
        if (mineCount != 0) {
            return;
        }
        for (int d = -1; d <= 1; d++) {
            for (int e = -1; e <= 1; e++) {
                if ((d == 0 && e == 0) || !isCellAccessible(board, row+d, col+e)) {
                    continue;
                }

                gameTurn(board, row+d, col+e);
            }
        }
    }
}