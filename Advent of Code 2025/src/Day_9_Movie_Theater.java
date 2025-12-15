import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        long maxArea = 0L;

        // Try all pairs of red tiles as opposite corners
        for (int i = 0; i < n; i++) {
            long[] a = reds.get(i);
            for (int j = i + 1; j < n; j++) {
                long[] b = reds.get(j);

                long x0 = Math.min(a[0], b[0]);
                long x1 = Math.max(a[0], b[0]);
                long y0 = Math.min(a[1], b[1]);
                long y1 = Math.max(a[1], b[1]);

                // Skip degenerate rectangles (zero area)
                if (x0 == x1 || y0 == y1) continue;

                if (rectangleInsidePolygon(x0, y0, x1, y1, reds)) {
                    long area = (x1 - x0 + 1L) * (y1 - y0 + 1L);
                    if (area > maxArea) {
                        maxArea = area;
                    }
                }
            }
        }

        writeOutput(String.valueOf(maxArea));
    }

    // Check if an axis-aligned rectangle [x0,x1] x [y0,y1] is fully inside
    // the polygon formed by reds (connected in given order with axis-aligned edges).
    private static boolean rectangleInsidePolygon(long x0, long y0, long x1, long y1, List<long[]> reds) {
        // Sample point strictly inside the rectangle
        double cx = (x0 + x1) * 0.5;
        double cy = (y0 + y1) * 0.5;

        if (!pointInOrOnPolygon(cx, cy, reds)) {
            return false;
        }

        // Ensure no polygon edge crosses the interior of the rectangle
        int n = reds.size();
        for (int i = 0; i < n; i++) {
            long[] p1 = reds.get(i);
            long[] p2 = reds.get((i + 1) % n);

            long xA = p1[0], yA = p1[1];
            long xB = p2[0], yB = p2[1];

            if (xA == xB) {
                // Vertical edge at x = xA between yA and yB
                long x = xA;
                long yMin = Math.min(yA, yB);
                long yMax = Math.max(yA, yB);

                if (x > x0 && x < x1) {
                    // Overlap with rectangle's vertical span in (y0, y1)
                    long yy0 = Math.max(yMin, y0 + 1);
                    long yy1 = Math.min(yMax, y1 - 1);
                    if (yy0 <= yy1) {
                        return false; // edge crosses rectangle interior
                    }
                }
            } else if (yA == yB) {
                // Horizontal edge at y = yA between xA and xB
                long y = yA;
                long xMin = Math.min(xA, xB);
                long xMax = Math.max(xA, xB);

                if (y > y0 && y < y1) {
                    long xx0 = Math.max(xMin, x0 + 1);
                    long xx1 = Math.min(xMax, x1 - 1);
                    if (xx0 <= xx1) {
                        return false; // edge crosses rectangle interior
                    }
                }
            }
        }

        return true;
    }

    // Standard point-in-polygon (ray casting) treating boundary as inside.
    private static boolean pointInOrOnPolygon(double x, double y, List<long[]> reds) {
        int n = reds.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = reds.get(i)[0];
            double yi = reds.get(i)[1];
            double xj = reds.get(j)[0];
            double yj = reds.get(j)[1];

            // Check if point is exactly on a polygon edge
            if (onSegment(xj, yj, xi, yi, x, y)) {
                return true;
            }

            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi + 0.0) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }

    private static boolean onSegment(double x1, double y1, double x2, double y2, double px, double py) {
        // Collinearity check for axis-aligned edges
        if (x1 == x2) {
            // vertical segment
            if (px != x1) return false;
            return py >= Math.min(y1, y2) && py <= Math.max(y1, y2);
        } else if (y1 == y2) {
            // horizontal segment
            if (py != y1) return false;
            return px >= Math.min(x1, x2) && px <= Math.max(x1, x2);
        }
        return false;
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
