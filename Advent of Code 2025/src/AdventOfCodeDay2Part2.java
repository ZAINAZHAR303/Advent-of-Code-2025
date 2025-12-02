public class AdventOfCodeDay2Part2 {
    public static long findInvalidSumPart2(String input) {
        long totalSum = 0;

        for (String rangeStr : input.split(",")) {
            if (rangeStr.isEmpty()) continue;
            String[] bounds = rangeStr.split("-");
            long start = Long.parseLong(bounds[0]);
            long end = Long.parseLong(bounds[1]);

            // For each possible total length L
            for (int L = 1; L <= 18; L++) {
                // For each possible repeat length K that divides L
                for (int K = 1; K <= L; K++) {
                    if (L % K != 0) continue;

                    long multiplier = 0;
                    long powK = 1;
                    for (int i = 0; i < L/K; i++) {
                        multiplier += powK;
                        powK *= (long) Math.pow(10, K);
                    }

                    // X must have exactly K digits (no leading zeros)
                    long minX = (long) Math.pow(10, K-1);
                    long maxX = (long) Math.pow(10, K) - 1;

                    // Find X range within [start, end]
                    long minXInRange = (long) Math.ceil((double) start / multiplier);
                    long maxXInRange = (long) Math.floor((double) end / multiplier);

                    long actualMinX = Math.max(minX, minXInRange);
                    long actualMaxX = Math.min(maxX, maxXInRange);

                    if (actualMinX <= actualMaxX) {
                        long count = actualMaxX - actualMinX + 1;
                        long firstID = actualMinX * multiplier;
                        long lastID = actualMaxX * multiplier;
                        totalSum += count * (firstID + lastID) / 2;
                    }
                }
            }
        }
        return totalSum;
    }

    public static void main(String[] args) {
        String input = "9191896883-9191940271,457499-518693,4952-6512,960-1219,882220-1039699,2694-3465,3818-4790,166124487-166225167,759713819-759869448,4821434-4881387,7271-9983,1182154-1266413,810784-881078,802-958,1288-1491,45169-59445,25035-29864,379542-433637,287-398,75872077-75913335,653953-689335,168872-217692,91-113,475-590,592-770,310876-346156,2214325-2229214,85977-112721,51466993-51620441,8838997-8982991,534003-610353,32397-42770,17-27,68666227-68701396,1826294188-1826476065,1649-2195,141065204-141208529,7437352-7611438,10216-13989,33-44,1-16,49-74,60646-73921,701379-808878"; // your full input
        System.out.println("Part 2 Sum: " + findInvalidSumPart2(input));
    }
}
