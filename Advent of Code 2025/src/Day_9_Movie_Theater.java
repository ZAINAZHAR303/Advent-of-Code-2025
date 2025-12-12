import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Day_9_Movie_Theater {
    public static void main(String[] args) throws IOException {
        List<long[]> reds = new ArrayList<>();

        // Read red tile coordinates from input.txt
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 2) continue; // ignore malformed lines

                long x = Long.parseLong(parts[0].trim());
                long y = Long.parseLong(parts[1].trim());
                reds.add(new long[]{x, y});
            }
        }

        if (reds.isEmpty()) {
            writeOutput("0");
            return;
        }

        int n = reds.size();

        // Determine bounds of the grid
        long minX = Long.MAX_VALUE, maxX = Long.MIN_VALUE;
        long minY = Long.MAX_VALUE, maxY = Long.MIN_VALUE;
        for (long[] p : reds) {
            if (p[0] < minX) minX = p[0];
            if (p[0] > maxX) maxX = p[0];
            if (p[1] < minY) minY = p[1];
            if (p[1] > maxY) maxY = p[1];
        }

        int width = (int) (maxX - minX + 1);
        int height = (int) (maxY - minY + 1);

        boolean[][] boundary = new boolean[height][width];

        // Mark polygon edges (including red tiles) as boundary
        for (int i = 0; i < n; i++) {
            long[] a = reds.get(i);
            long[] b = reds.get((i + 1) % n);

            int ax = (int) (a[0] - minX);
            int ay = (int) (a[1] - minY);
            int bx = (int) (b[0] - minX);
            int by = (int) (b[1] - minY);

            if (ax == bx) {
                // Vertical segment
                int y0 = Math.min(ay, by);
                int y1 = Math.max(ay, by);
                for (int y = y0; y <= y1; y++) {
                    boundary[y][ax] = true;
                }
            } else if (ay == by) {
                // Horizontal segment
                int x0 = Math.min(ax, bx);
                int x1 = Math.max(ax, bx);
                for (int x = x0; x <= x1; x++) {
                    boundary[ay][x] = true;
                }
            }
        }

        // Flood fill from outside to find cells that are not inside the loop
        int extH = height + 2;
        int extW = width + 2;
        boolean[][] outside = new boolean[extH][extW];

        Deque<int[]> dq = new ArrayDeque<>();
        dq.add(new int[]{0, 0});
        outside[0][0] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!dq.isEmpty()) {
            int[] cur = dq.poll();
            int r = cur[0];
            int c = cur[1];

            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k];
                int nc = c + dc[k];
                if (nr < 0 || nr >= extH || nc < 0 || nc >= extW) continue;
                if (outside[nr][nc]) continue;

                // Map to original grid
                int gy = nr - 1;
                int gx = nc - 1;
                boolean blocked = false;
                if (gy >= 0 && gy < height && gx >= 0 && gx < width) {
                    if (boundary[gy][gx]) blocked = true;
                }

                if (blocked) continue;

                outside[nr][nc] = true;
                dq.add(new int[]{nr, nc});
            }
        }

        // Cells that are boundary or inside the loop are allowed (red or green)
        boolean[][] allowed = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (boundary[y][x]) {
                    allowed[y][x] = true;
                } else {
                    // Check if this cell is inside: not reachable from outside
                    int ey = y + 1;
                    int ex = x + 1;
                    if (!outside[ey][ex]) {
                        allowed[y][x] = true;
                    }
                }
            }
        }

        // Build prefix sum of illegal cells (!allowed)
        int[][] pref = new int[height + 1][width + 1];
        for (int y = 0; y < height; y++) {
            int rowSum = 0;
            for (int x = 0; x < width; x++) {
                if (!allowed[y][x]) rowSum++;
                pref[y + 1][x + 1] = pref[y][x + 1] + rowSum;
            }
        }

        long maxArea = 0L;

        // Try all pairs of red tiles as opposite corners
        for (int i = 0; i < n; i++) {
            long[] a = reds.get(i);
            int ax = (int) (a[0] - minX);
            int ay = (int) (a[1] - minY);
            for (int j = i + 1; j < n; j++) {
                long[] b = reds.get(j);
                int bx = (int) (b[0] - minX);
                int by = (int) (b[1] - minY);

                int x0 = Math.min(ax, bx);
                int x1 = Math.max(ax, bx);
                int y0 = Math.min(ay, by);
                int y1 = Math.max(ay, by);

                int illegal = pref[y1 + 1][x1 + 1]
                        - pref[y0][x1 + 1]
                        - pref[y1 + 1][x0]
                        + pref[y0][x0];

                if (illegal == 0) {
                    long dx = x1 - x0 + 1L;
                    long dy = y1 - y0 + 1L;
                    long area = dx * dy;
                    if (area > maxArea) {
                        maxArea = area;
                    }
                }
            }
        }

        writeOutput(String.valueOf(maxArea));
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
