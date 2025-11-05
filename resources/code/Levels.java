public class Levels {

    public static int sumLevel(int[][] matrix, int level) {
        if (matrix == null || matrix.length == 0 || !isMatrix(matrix)) return -2;
        int n = matrix.length;
        // PRE: n >= 1
        int maxLevel = (n - 1) / 2;
        // POST: (n % 2 == 1) => (maxLevel >= 0 && maxLevel <= (n - 1) / 2)
        //       (n % 2 == 0) => (maxLevel >= 0 && maxLevel <= n / 2 - 1)

        if (level > maxLevel || level < 0) return -2;
        int levelOffset = maxLevel - level;
        int rim = n - levelOffset;

        // O(n^2) but easy to understand :)
        /*
        int outerSquare = sumSquare(matrix, levelOffset, rim);
        int innerSquare = sumSquare(matrix, levelOffset + 1, rim - 1);
        int result = outerSquare - innerSquare;
        return result;
        */

        /*
        int result = 0;
        for (int i = levelOffset; i < rim; i++) {
            for (int j = levelOffset; j < rim; j++) {
                if (i == levelOffset || i == rim - 1 || j == levelOffset || j == rim - 1) {
                    result += matrix[i][j];
                }
            }
        }
        return result;
        */

        // O(n) solution ;^)
        int result = 0;

        for (int i = levelOffset; i < rim; i++) {
            result += matrix[levelOffset][i];
            // we have to keep the case where level = 0 in mind
            // otherwise we also add the horizontal border from the bottom
            if (levelOffset != rim - 1) {
                result += matrix[rim - 1][i];
            }
        }
        for (int i = levelOffset + 1; i < rim - 1; i++) {
            result += matrix[i][levelOffset] + matrix[i][rim - 1]; // left and right border
        }
        return result;


    }

    private static int sumSquare(int[][] matrix, int levelOffset, int rim) {
        int result = 0;
        for (int i = levelOffset; i < rim; i++) {
            for (int j = levelOffset; j < rim; j++) {
                result += matrix[i][j];
            }
        }
        return result;
    }

    private static boolean isMatrix(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            if (matrix[i] == null || matrix[i].length != n) {
                return false;
            }
        }
        return true;
    }
}
