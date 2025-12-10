import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class AdventOfCodeDay2Part2 {
    public static long findInvalidSumPart2(String input) {
        long totalSum = 0;

        for (String rangeStr : input.split(",")) {
            rangeStr = rangeStr.trim();
            if (rangeStr.isEmpty()) continue;

            String[] bounds = rangeStr.split("-");
            long start = Long.parseLong(bounds[0]);
            long end = Long.parseLong(boundscd[1]);

            // Collect unique invalid IDs for this range to avoid double-counting
            Set<Long> invalidIds = new HashSet<>();

            for (int L = 1; L <= 18; L++) {
                for (int K = 1; K <= L; K++) {
                    if (L % K != 0) continue;
                    int repeats = L / K;

                    if (repeats < 2) continue; // Must repeat at least twice

                    long multiplier = 0;
                    long pow = 1;
                    long base = power10(K);
                    for (int i = 0; i < repeats; i++) {
                        multiplier += pow;
                        pow *= base;
                    }

                    long minX = power10(K - 1);
                    long maxX = power10(K) - 1;

                    long minXInRange = ceilDiv(start, multiplier);
                    long maxXInRange = floorDiv(end, multiplier);

                    long actualMinX = Math.max(minX, minXInRange);
                    long actualMaxX = Math.min(maxX, maxXInRange);

                    if (actualMinX <= actualMaxX) {
                        for (long x = actualMinX; x <= actualMaxX; x++) {
                            long id = x * multiplier;
                            if (id >= start && id <= end) {
                                invalidIds.add(id);
                            }
                        }
                    }
                }
            }

            for (long id : invalidIds) {
                totalSum += id;
            }
        }
        return totalSum;
    }

    // Integer ceil division: smallest integer >= a / b
    private static long ceilDiv(long a, long b) {
        if (b < 0) {
            a = -a;
            b = -b;
        }
        if (a >= 0) {
            return (a + b - 1) / b;
        } else {
            // For negative a, Java's / already truncs toward zero, which is >= true quotient
            return a / b;
        }
    }

    // Integer floor division: largest integer <= a / b
    private static long floorDiv(long a, long b) {
        if (b < 0) {
            a = -a;
            b = -b;
        }
        if (a >= 0) {
            return a / b;
        } else {
            // Adjust for truncation toward zero when a is negative
            return (a - b + 1) / b;
        }
    }

    // Exact integer power of 10 (avoids floating point precision issues)
    private static long power10(int exp) {
        if (exp <= 0) return 1;
        long result = 1;
        for (int i = 0; i < exp; i++) {
            result *= 10;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine(); // read full comma-separated ranges from stdin
        long result = findInvalidSumPart2(input);
        System.out.println(result);
    }
}