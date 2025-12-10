import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day_3_Lobby {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        long totalJoltagePart1 = 0;
        long totalJoltagePart2 = 0;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            line = line.trim();
            if (line.isEmpty()) continue;

            int n = line.length();

            // Part 1: choose exactly 2 digits in order for maximum 2-digit value
            int best2 = 0;
            for (int i = 0; i < n; i++) {
                int d1 = line.charAt(i) - '0';
                for (int j = i + 1; j < n; j++) {
                    int d2 = line.charAt(j) - '0';
                    int value = d1 * 10 + d2;
                    if (value > best2) {
                        best2 = value;
                    }
                }
            }
            totalJoltagePart1 += best2;

            // Part 2: choose exactly 12 digits in order for maximum 12-digit value
            // Greedy: scan from left to right, at each step take the largest digit
            // that still allows us to reach length 12 with remaining digits.
            int needed = 12;
            int index = 0;
            StringBuilder chosen = new StringBuilder(12);

            while (needed > 0 && index < n) {
                int remainingPositions = n - index;
                // We must be able to pick 'needed' digits from remainingPositions
                int windowEnd = n - needed; // inclusive end index we can search up to
                int bestDigit = -1;
                int bestPos = -1;

                for (int pos = index; pos <= windowEnd; pos++) {
                    int digit = line.charAt(pos) - '0';
                    if (digit > bestDigit) {
                        bestDigit = digit;
                        bestPos = pos;
                    }
                }

                chosen.append((char) ('0' + bestDigit));
                index = bestPos + 1;
                needed--;
            }

            long best12 = Long.parseLong(chosen.toString());
            totalJoltagePart2 += best12;
        }

        System.out.println(totalJoltagePart1);
        System.out.println(totalJoltagePart2);
    }
}
