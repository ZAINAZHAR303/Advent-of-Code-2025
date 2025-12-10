import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day_4_Printing_Department {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            line = line.trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            writeOutput("0");
            return;
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            grid[r] = lines.get(r).toCharArray();
        }

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        int removedTotal = 0;

        while (true) {
            List<int[]> toRemove = new ArrayList<>();

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c] != '@') continue;

                    int neighbors = 0;
                    for (int k = 0; k < 8; k++) {
                        int nr = r + dr[k];
                        int nc = c + dc[k];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        if (grid[nr][nc] == '@') {
                            neighbors++;
                        }
                    }

                    if (neighbors < 4) {
                        toRemove.add(new int[]{r, c});
                    }
                }
            }

            if (toRemove.isEmpty()) {
                break; // no more rolls can be removed
            }

            for (int[] pos : toRemove) {
                int r = pos[0];
                int c = pos[1];
                if (grid[r][c] == '@') { // still present
                    grid[r][c] = '.';
                    removedTotal++;
                }
            }
        }

        writeOutput(String.valueOf(removedTotal));
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
