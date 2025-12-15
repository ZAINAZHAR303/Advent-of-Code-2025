import java.io.*;
import java.util.*;

public class Day_10_Debug {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        String line = br.readLine(); // First machine only
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
        System.out.println("Target sum: " + Arrays.stream(target).sum());
        System.out.println("Buttons: " + buttons.size());
        for (int i = 0; i < buttons.size(); i++) {
            System.out.println("  Button " + i + ": " + Arrays.toString(buttons.get(i)));
        }
        
        // Try greedy
        int[] current = new int[m];
        int presses = 0;
        
        for (int iter = 0; iter < 20; iter++) {
            if (Arrays.equals(current, target)) {
                System.out.println("\nReached target in " + presses + " presses!");
                break;
            }
            
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
                System.out.println("\nNo valid button found!");
                System.out.println("Current: " + Arrays.toString(current));
                break;
            }
            
            for (int idx : buttons.get(bestBtn)) {
                current[idx]++;
            }
            presses++;
            
            System.out.println("Press " + presses + ": Button " + bestBtn + " -> " + Arrays.toString(current));
        }
    }
}
