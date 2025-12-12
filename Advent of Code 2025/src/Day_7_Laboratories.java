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

        long splitCount = simulateSplits(grid, rows, cols, startRow, startCol);
        writeOutput(String.valueOf(splitCount));
    }

    private static long simulateSplits(char[][] grid, int rows, int cols, int startRow, int startCol) {
        boolean[] current = new boolean[cols];
        boolean[] next = new boolean[cols];

        // Beam starts just below S
        if (startRow + 1 < rows) {
            current[startCol] = true;
        }

        long splits = 0L;

        for (int r = startRow + 1; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                next[c] = false;
            }

            for (int c = 0; c < cols; c++) {
                if (!current[c]) continue;

                char cell = grid[r][c];
                if (cell == '^') {
                    // Split: original beam stops, two new beams left and right
                    splits++;
                    if (r + 1 < rows) {
                        if (c - 1 >= 0) next[c - 1] = true;
                        if (c + 1 < cols) next[c + 1] = true;
                    }
                } else {
                    // Empty or S: beam continues straight down
                    if (r + 1 < rows) {
                        next[c] = true;
                    }
                }
            }

            boolean any = false;
            for (int c = 0; c < cols; c++) {
                current[c] = next[c];
                if (current[c]) any = true;
            }

            if (!any) break; // no more beams active
        }

        return splits;
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
