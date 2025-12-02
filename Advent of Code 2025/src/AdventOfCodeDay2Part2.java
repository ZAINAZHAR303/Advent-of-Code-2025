public class AdventOfCodeDay2Part2 {
    public static long findInvalidSumPart2(String input) {
        long totalSum = 0;

        for (String rangeStr : input.split(",")) {
            if (rangeStr.isEmpty()) continue;
            String[] bounds = rangeStr.split("-");
            long start = Long.parseLong(bounds[0]);
            long end = Long.parseLong(bounds[1]);

            for (int L = 1; L <= 18; L++) {
                for (int K = 1; K <= L; K++) {
                    if (L % K != 0) continue;
                    int repeats = L / K;

                    if (repeats < 2) continue; // Must repeat at least twice

                    // Correct multiplier: 10^(L-K) + 10^(L-2K) + ... + 10^0
                    long multiplier = 0;
                    long pow = 1;
                    for (int i = 0; i < repeats; i++) {
                        multiplier += pow;
                        pow *= power10(K); // Exact integer power
                    }

                    // X has exactly K digits, no leading zeros
                    long minX = power10(K - 1);
                    long maxX = power10(K) - 1;

                    // X * multiplier must be in [start, end] (use integer math for bounds)
                    long minXInRange = ceilDiv(start, multiplier);
                    long maxXInRange = floorDiv(end, multiplier);

                    long actualMinX = Math.max(minX, minXInRange);
                    long actualMaxX = Math.min(maxX, maxXInRange);

                    // Verify first and last are actually in range
                    if (actualMinX <= actualMaxX) {
                        long firstID = actualMinX * multiplier;
                        long lastID = actualMaxX * multiplier;

                        if (firstID >= start && firstID <= end &&
                                lastID >= start && lastID <= end) {
                            long count = actualMaxX - actualMinX + 1;
                            totalSum += count * (firstID + lastID) / 2;
                        }
                    }
                }
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
        long result = 1;
        for (int i = 0; i < exp; i++) {
            result *= 10;
        }
        return result;
    }

    public static void main(String[] args) {
        String input = "9191896883-9191940271,457499-518693,4952-6512,960-1219,882220-1039699,2694-3465,3818-4790,166124487-166225167,759713819-759869448,4821434-4881387,7271-9983,1182154-1266413,810784-881078,802-958,1288-1491,45169-59445,25035-29864,379542-433637,287-398,75872077-75913335,653953-689335,168872-217692,91-113,475-590,592-770,310876-346156,2214325-2229214,85977-112721,51466993-51620441,8838997-8982991,534003-610353,32397-42770,17-27,68666227-68701396,1826294188-1826476065,1649-2195,141065204-141208529,7437352-7611438,10216-13989,33-44,1-16,49-74,60646-73921,701379-808878";

        System.out.println("Corrected Part 2 Sum: " + findInvalidSumPart2(input));
    }
}