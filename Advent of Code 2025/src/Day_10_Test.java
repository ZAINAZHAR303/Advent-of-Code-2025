import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Day_10_Test {
    public static void main(String[] args) throws IOException {
        long totalPresses = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("test_input.txt"))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                lineNum++;
                long result = solveMachine(line);
                System.out.println("Machine " + lineNum + ": " + result);
                totalPresses += result;
            }
        }

        System.out.println("\nTotal: " + totalPresses + " (expected 33)");
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

        System.out.println("  Target: " + Arrays.toString(target));
        System.out.println("  Buttons: " + buttons.size());
        for (int i = 0; i < buttons.size(); i++) {
            System.out.println("    " + i + ": " + Arrays.toString(buttons.get(i)));
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
        
        // Dijkstra with PriorityQueue
        PriorityQueue<State> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.presses, b.presses));
        HashSet<String> visited = new HashSet<>();
        
        State start = new State(new int[m], 0);
        pq.offer(start);
        
        while (!pq.isEmpty()) {
            State curr = pq.poll();
            String currKey = Arrays.toString(curr.counters);
            
            // Skip if already visited
            if (visited.contains(currKey)) {
                continue;
            }
            visited.add(currKey);
            
            // Check if we reached target
            if (Arrays.equals(curr.counters, target)) {
                return curr.presses;
            }
            
            // Try each button
            for (int[] button : buttons) {
                int[] next = curr.counters.clone();
                boolean valid = true;
                
                for (int idx : button) {
                    next[idx]++;
                    if (next[idx] > target[idx]) {
                        valid = false;
                        break;
                    }
                }
                
                if (!valid) continue;
                
                String nextKey = Arrays.toString(next);
                if (!visited.contains(nextKey)) {
                    pq.offer(new State(next, curr.presses + 1));
                }
            }
        }
        
        return Long.MAX_VALUE;
    }
    
    static class State {
        int[] counters;
        int presses;
        
        State(int[] counters, int presses) {
            this.counters = counters;
            this.presses = presses;
        }
    }
}
