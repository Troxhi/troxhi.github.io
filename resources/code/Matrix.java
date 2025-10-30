public class Matrix {

    // Time complexity: m * n * 4 = O(m * n)
    public static int countAssimilated(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int sum = 0;
                for (int e = -1; e <= 1; e++) {
                    for (int d = -1; d <= 1; d++) {
                        if (e == 0 && d == 0) {
                            continue;
                        }
                        int x = i + e;
                        int y = j + d;
                        sum += safelyAccessMatrix(matrix, x, y);
                    }
                }

                if (sum % matrix[i][j] == 0) {
                    count++;
                }
            }
        }

        return count;
    }

    private static int safelyAccessMatrix(int[][] matrix, int i, int j) {
        int m = matrix.length;
        int n = matrix[0].length;
        if (i < 0 || i >= m || j < 0 || j >= n) {
            return 0;
        }
        return matrix[i][j];
    }

    public static void main(String[] args) {
        System.out.println("countAssimilated([[10, 10, 10], [10, 10, 10], [10, 10, 10]]): " +
                countAssimilated(new int[][]{{10, 10, 10}, {10, 10, 10}, {10, 10, 10}}));
        System.out.println("countAssimilated([[5, 10, 3], [6, 9, 6], [3, 3, 15]]): " +
                countAssimilated(new int[][]{{5, 10, 3}, {6, 9, 6}, {3, 3, 15}}));
        System.out.println("countAssimilated([[4, 7, 13], [-2, -12, 32], [20, 15, -8], [17, 3, 1111]]): " +
                countAssimilated(new int[][]{{4, 7, 13}, {-2, -12, 32}, {20, 15, -8}, {17, 3, 1111}}));
        System.out.println("countAssimilated([[1, 2, 3, 4, 5, 6], " +
                "[7, 8, 9, 10, 11, 12], " +
                "[13, 14, 15, 16, 17, 18], " +
                "[19, 20, 21, 22, 23, 24], " +
                "[25, 26, 27, 28, 29, 30]]): " +
                countAssimilated(new int[][]{{1, 2, 3, 4, 5, 6},
                        {7, 8, 9, 10, 11, 12},
                        {13, 14, 15, 16, 17, 18},
                        {19, 20, 21, 22, 23, 24},
                        {25, 26, 27, 28, 29, 30}}));
    }
}
