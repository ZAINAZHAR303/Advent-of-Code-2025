import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day_7_Laboratories {
    public static void main(String[] args) throws IOException {
        List<String> lines = new ArrayList<>();

        // Read manifold diagram from input.txt
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            writeOutput("0");
            return;
        }

        int rows = lines.size();
        int cols = 0;
        for (String s : lines) {
            if (s.length() > cols) cols = s.length();
        }

        char[][] grid = new char[rows][cols];
        int startRow = -1;
        int startCol = -1;

        for (int r = 0; r < rows; r++) {
            String s = lines.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = (c < s.length()) ? s.charAt(c) : '.';
                grid[r][c] = ch;
                if (ch == 'S') {
                    startRow = r;
                    startCol = c;
                }
            }
        }

        if (startRow == -1 || startCol == -1) {
            writeOutput("0");
            return;
        }

        long timelineCount = simulateTimelines(grid, rows, cols, startRow, startCol);
        writeOutput(String.valueOf(timelineCount));
    }

    // Part 2: count how many distinct timelines a single particle can take,
    // where each splitter branches the timeline into left and right paths.
    private static long simulateTimelines(char[][] grid, int rows, int cols, int startRow, int startCol) {
        long[][] dp = new long[rows][cols];
        long timelines = 0L;

        int startR = startRow + 1;
        if (startR >= rows) {
            // Particle immediately exits the manifold
            return 1L;
        }

        dp[startR][startCol] = 1L; // one particle entering just below S

        for (int r = startR; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                long ways = dp[r][c];
                if (ways == 0L) continue;

                char cell = grid[r][c];
                int nr = r + 1;

                if (cell == '^') {
                    // Split into left and right branches
                    int leftCol = c - 1;
                    int rightCol = c + 1;

                    // Left branch
                    if (nr >= rows || leftCol < 0) {
                        timelines += ways;
                    } else {
                        dp[nr][leftCol] += ways;
                    }

                    // Right branch
                    if (nr >= rows || rightCol >= cols) {
                        timelines += ways;
                    } else {
                        dp[nr][rightCol] += ways;
                    }
                } else {
                    // Continue straight down
                    int downCol = c;
                    if (nr >= rows || downCol < 0 || downCol >= cols) {
                        timelines += ways;
                    } else {
                        dp[nr][downCol] += ways;
                    }
                }
            }
        }

        return timelines;
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
