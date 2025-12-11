import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day_5_Cafeteria {
    public static void main(String[] args) throws IOException {
        // Read from input.txt in the current working directory
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        List<long[]> ranges = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        String line;
        boolean readingRanges = true;

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Blank line switches from ranges to IDs
            if (line.isEmpty()) {
                readingRanges = false;
                continue;
            }

            if (readingRanges) {
                String[] parts = line.split("-");
                if (parts.length != 2) continue; // ignore malformed
                long start = Long.parseLong(parts[0]);
                long end = Long.parseLong(parts[1]);
                if (start > end) {
                    long tmp = start;
                    start = end;
                    end = tmp;
                }
                ranges.add(new long[]{start, end});
            } else {
                ids.add(Long.parseLong(line));
            }
        }

        // Part 1: how many available IDs are fresh?
        int freshCount = 0;
        for (long id : ids) {
            if (isFresh(id, ranges)) {
                freshCount++;
            }
        }

        // Part 2: how many IDs are fresh according to the ranges alone?
        long totalFreshIds = countFreshFromRanges(ranges);

        writeOutput(freshCount, totalFreshIds);
    }

    private static boolean isFresh(long id, List<long[]> ranges) {
        for (long[] r : ranges) {
            if (id >= r[0] && id <= r[1]) {
                return true;
            }
        }
        return false;
    }

    // Count how many integer IDs are covered by the union of all ranges
    private static long countFreshFromRanges(List<long[]> ranges) {
        if (ranges.isEmpty()) return 0L;

        // Sort ranges by start
        ranges.sort(Comparator.comparingLong(a -> a[0]));

        long total = 0L;
        long currentStart = ranges.get(0)[0];
        long currentEnd = ranges.get(0)[1];

        for (int i = 1; i < ranges.size(); i++) {
            long[] r = ranges.get(i);
            long start = r[0];
            long end = r[1];

            if (start <= currentEnd + 1) {
                // Overlapping or adjacent: extend current merged range
                if (end > currentEnd) {
                    currentEnd = end;
                }
            } else {
                // Disjoint: close current range and start a new one
                total += (currentEnd - currentStart + 1);
                currentStart = start;
                currentEnd = end;
            }
        }

        // Add last merged range
        total += (currentEnd - currentStart + 1);

        return total;
    }

    private static void writeOutput(int part1, long part2) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(String.valueOf(part1));
            bw.newLine();
            bw.write(String.valueOf(part2));
            bw.newLine();
        }
    }
}
