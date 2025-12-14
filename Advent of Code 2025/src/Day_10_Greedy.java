import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day_10_Greedy {
    public static void main(String[] args) throws IOException {
        long totalPresses = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                lineNum++;
                long result = solveMachine(line);
                System.out.println("Machine " + lineNum + ": " + result + " presses");
                totalPresses += result;
            }
        }

        System.out.println("\nTotal: " + totalPresses);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(Long.toString(totalPresses));
            bw.newLine();
        }
    }

    private static long solveMachine(String line) {
        int braceOpen = line.indexOf('{');
        int braceClose = line.indexOf('}', braceOpen + 1);
        if (braceOpen == -1 || braceClose == -1) {
            return 0L;
        }

        String targetPart = line.substring(braceOpen + 1, braceClose).trim();
        if (targetPart.isEmpty()) {
            return 0L;
        }

        String[] targetTokens = targetPart.split(",");
        int m = targetTokens.length;
        int[] target = new int[m];
        for (int i = 0; i < m; i++) {
            target[i] = Integer.parseInt(targetTokens[i].trim());
        }

        String buttonsPart = line.substring(0, braceOpen);
        List<int[]> buttons = new ArrayList<>();
        int pos = 0;
        while (true) {
            int open = buttonsPart.indexOf('(', pos);
            if (open == -1) break;
            int close = buttonsPart.indexOf(')', open + 1);
            if (close == -1) break;
            String inside = buttonsPart.substring(open + 1, close).trim();
            if (!inside.isEmpty()) {
                String[] idx = inside.split(",");
                List<Integer> indices = new ArrayList<>();
                for (String s : idx) {
                    s = s.trim();
                    if (s.isEmpty()) continue;
                    int k = Integer.parseInt(s);
                    if (k >= 0 && k < m) {
                        indices.add(k);
                    }
                }
                if (!indices.isEmpty()) {
                    int[] arr = new int[indices.size()];
                    for (int i = 0; i < indices.size(); i++) {
                        arr[i] = indices.get(i);
                    }
                    buttons.add(arr);
                }
            }
            pos = close + 1;
        }

        boolean allZero = true;
        for (int v : target) {
            if (v != 0) {
                allZero = false;
                break;
            }
        }
        if (allZero) return 0L;
        if (buttons.isEmpty()) return Long.MAX_VALUE;

        return searchMinPresses(buttons, target);
    }

    private static long searchMinPresses(List<int[]> buttons, int[] target) {
        int m = target.length;
        int[] current = new int[m];
        int totalPresses = 0;
        
        // Greedy: Always press the most efficient button
        while (!Arrays.equals(current, target)) {
            int bestButton = -1;
            double bestScore = -1;
            
            for (int i = 0; i < buttons.size(); i++) {
                int[] btn = buttons.get(i);
                
                // Calculate how helpful this button is
                int helpfulCount = 0;
                int totalDeficit = 0;
                boolean wouldOvershoot = false;
                
                for (int idx : btn) {
                    int deficit = target[idx] - current[idx];
                    if (deficit > 0) {
                        helpfulCount++;
                        totalDeficit += deficit;
                    } else if (deficit == 0) {
                        wouldOvershoot = true;
                        break;
                    }
                }
                
                if (wouldOvershoot || helpfulCount == 0) continue;
                
                // Score: total deficit covered / button size (prefer efficient buttons)
                double score = (double) totalDeficit / btn.length;
                
                if (score > bestScore) {
                    bestScore = score;
                    bestButton = i;
                }
            }
            
            if (bestButton == -1) {
                // No valid button - shouldn't happen with valid input
                return Long.MAX_VALUE;
            }
            
            // Press the best button
            for (int idx : buttons.get(bestButton)) {
                current[idx]++;
            }
            totalPresses++;
            
            if (totalPresses > 1000000) {
                return Long.MAX_VALUE; // Safety limit
            }
        }
        
        return totalPresses;
    }
}
