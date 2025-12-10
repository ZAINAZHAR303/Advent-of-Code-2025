import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day_3_Lobby {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        long totalJoltage = 0;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            line = line.trim();
            if (line.isEmpty()) continue;

            int best = 0;
            int n = line.length();

            // Look at every pair of distinct positions i < j
            for (int i = 0; i < n; i++) {
                int d1 = line.charAt(i) - '0';
                for (int j = i + 1; j < n; j++) {
                    int d2 = line.charAt(j) - '0';
                    int value = d1 * 10 + d2;
                    if (value > best) {
                        best = value;
                    }
                }
            }

            totalJoltage += best;
        }

        System.out.println(totalJoltage);
    }
}
