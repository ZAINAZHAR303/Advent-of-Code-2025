import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day_5_Cafeteria {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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

        int freshCount = 0;
        for (long id : ids) {
            if (isFresh(id, ranges)) {
                freshCount++;
            }
        }

        System.out.println(freshCount);
    }

    private static boolean isFresh(long id, List<long[]> ranges) {
        for (long[] r : ranges) {
            if (id >= r[0] && id <= r[1]) {
                return true;
            }
        }
        return false;
    }
}
