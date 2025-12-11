import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day_6_Trash_Compactor {
    public static void main(String[] args) throws IOException {
        List<String> lines = new ArrayList<>();

        // Read worksheet from input.txt
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
        for (int r = 0; r < rows; r++) {
            String s = lines.get(r);
            for (int c = 0; c < cols; c++) {
                grid[r][c] = (c < s.length()) ? s.charAt(c) : ' ';
            }
        }

        // Identify separator columns: full column of spaces
        boolean[] isSeparator = new boolean[cols];
        for (int c = 0; c < cols; c++) {
            boolean sep = true;
            for (int r = 0; r < rows; r++) {
                if (grid[r][c] != ' ') {
                    sep = false;
                    break;
                }
            }
            isSeparator[c] = sep;
        }

        long grandTotal = 0L;
        int col = 0;
        while (col < cols) {
            // Skip separator columns
            while (col < cols && isSeparator[col]) {
                col++;
            }
            if (col >= cols) break;

            int startCol = col;
            while (col < cols && !isSeparator[col]) {
                col++;
            }
            int endCol = col - 1;

            // For this problem block [startCol, endCol], find operator in bottom row
            int opRow = rows - 1;
            char op = ' ';
            for (int c = startCol; c <= endCol; c++) {
                char ch = grid[opRow][c];
                if (ch == '+' || ch == '*') {
                    op = ch;
                    break;
                }
            }
            if (op != '+' && op != '*') {
                // No valid operator found; skip this block
                continue;
            }

            // Part 2: numbers are given per column, most significant digit at top
            List<Long> numbers = new ArrayList<>();
            for (int c = startCol; c <= endCol; c++) {
                StringBuilder sb = new StringBuilder();
                for (int r = 0; r < opRow; r++) {
                    char ch = grid[r][c];
                    if (ch >= '0' && ch <= '9') {
                        sb.append(ch);
                    }
                }
                if (sb.length() > 0) {
                    numbers.add(Long.parseLong(sb.toString()));
                }
            }

            if (!numbers.isEmpty()) {
                long value;
                if (op == '+') {
                    value = 0L;
                    for (long n : numbers) value += n;
                } else { // '*'
                    value = 1L;
                    for (long n : numbers) value *= n;
                }
                grandTotal += value;
            }
        }

        writeOutput(String.valueOf(grandTotal));
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
