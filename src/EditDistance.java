public class EditDistance {
    public int levenshtein(String str1, String str2) {
        int m = str1.length() + 1;
        int n = str2.length() + 1;
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        int[][] matrix = new int[m][n];
        int cost = 0;
        for (int i = 0; i < m; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j < n; j++) {
            matrix[0][j] = j;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[m - 1][n - 1];
    }
}
