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

        long maxArea = 0L;
        int n = reds.size();

        for (int i = 0; i < n; i++) {
            long[] a = reds.get(i);
            for (int j = i + 1; j < n; j++) {
                long[] b = reds.get(j);
                long dx = Math.abs(a[0] - b[0]);
                long dy = Math.abs(a[1] - b[1]);

                long area = (dx + 1) * (dy + 1); // inclusive tile count
                if (area > maxArea) {
                    maxArea = area;
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
