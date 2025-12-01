import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day_1_Secret_Entrance {
    public static void main(String[] args) throws IOException {
        final int MOD = 100;
        int position = 50;      // starting position
        long zeroCount = 0;     // how many times we land on 0 (use long just in case)

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;

        // Read until EOF (no more lines)
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            char dir = line.charAt(0);                         // 'L' or 'R'
            int steps = Integer.parseInt(line.substring(1));   // the number after L/R

            if (dir != 'L' && dir != 'R') {
                // ignore or handle invalid direction if needed
                continue;
            }

            // part 2: simulate every single click
            for (int i = 0; i < steps; i++) {
                if (dir == 'L') {
                    position--;
                    if (position < 0) {
                        position += MOD;  // wrap from -1 to 99
                    }
                } else { // dir == 'R'
                    position++;
                    if (position >= MOD) {
                        position -= MOD;  // wrap from 100 to 0
                    }
                }

                if (position == 0) {
                    zeroCount++;
                }
            }
        }

        System.out.println(zeroCount);
    }
}
