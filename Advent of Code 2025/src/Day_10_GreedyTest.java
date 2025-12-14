import java.io.*;
import java.util.*;

public class Day_10_GreedyTest {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        String line = br.readLine();
        br.close();
        
        int braceOpen = line.indexOf('{');
        int braceClose = line.indexOf('}', braceOpen + 1);
        
        String targetPart = line.substring(braceOpen + 1, braceClose).trim();
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
        
        System.out.println("Target: " + Arrays.toString(target));
        
        // Run greedy
        int[] current = new int[m];
        int presses = 0;
        
        while (!Arrays.equals(current, target)) {
            int bestBtn = -1;
            double bestScore = -1;
            
            for (int i = 0; i < buttons.size(); i++) {
                int[] btn = buttons.get(i);
                int deficitSum = 0;
                int helpCount = 0;
                boolean wouldExceed = false;
                
                for (int idx : btn) {
                    int deficit = target[idx] - current[idx];
                    if (deficit > 0) {
                        deficitSum += deficit;
                        helpCount++;
                    } else if (deficit == 0) {
                        wouldExceed = true;
                        break;
                    }
                }
                
                if (wouldExceed || helpCount == 0) continue;
                
                double score = (double) deficitSum / btn.length;
                
                if (score > bestScore) {
                    bestScore = score;
                    bestBtn = i;
                }
            }
            
            if (bestBtn == -1) {
                System.out.println("FAILED at press " + presses + " current: " + Arrays.toString(current));
                System.exit(1);
            }
            
            for (int idx : buttons.get(bestBtn)) {
                current[idx]++;
            }
            presses++;
            
            if (presses % 1000 == 0) {
                System.out.println("Press " + presses + ": " + Arrays.toString(current));
            }
            
            if (presses > 10000) {
                System.out.println("Taking too long, stopping");
                break;
            }
        }
        
        if (Arrays.equals(current, target)) {
            System.out.println("\nSUCCESS! Total presses: " + presses);
        }
    }
}
