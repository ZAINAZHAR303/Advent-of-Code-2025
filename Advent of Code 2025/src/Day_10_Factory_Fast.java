import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class Day_10_Factory_Fast {
    public static void main(String[] args) throws IOException {
        long totalPresses = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                totalPresses += solveMachine(line);
            }
        }

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

        // Parse target joltage requirements
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

        // Parse buttons
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
        
        // Dijkstra with aggressive pruning
        PriorityQueue<State> pq = new PriorityQueue<>();
        HashMap<String, Integer> dist = new HashMap<>();
        
        State start = new State(new int[m], 0);
        String startKey = stateKey(start.counters);
        pq.offer(start);
        dist.put(startKey, 0);
        
        while (!pq.isEmpty()) {
            State curr = pq.poll();
            String currKey = stateKey(curr.counters);
            
            // Skip if we've found better path
            if (dist.containsKey(currKey) && dist.get(currKey) < curr.presses) {
                continue;
            }
            
            // Check if target reached
            if (Arrays.equals(curr.counters, target)) {
                return curr.presses;
            }
            
            // Try each button
            for (int[] button : buttons) {
                int[] next = curr.counters.clone();
                boolean valid = true;
                boolean useful = false;
                
                for (int idx : button) {
                    if (next[idx] < target[idx]) {
                        useful = true;
                    }
                    next[idx]++;
                    if (next[idx] > target[idx]) {
                        valid = false;
                        break;
                    }
                }
                
                if (!valid || !useful) continue;
                
                String nextKey = stateKey(next);
                int nextPresses = curr.presses + 1;
                
                if (!dist.containsKey(nextKey) || dist.get(nextKey) > nextPresses) {
                    dist.put(nextKey, nextPresses);
                    pq.offer(new State(next, nextPresses));
                }
            }
        }
        
        return Long.MAX_VALUE;
    }
    
    // Compact string key for state
    private static String stateKey(int[] counters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < counters.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(counters[i]);
        }
        return sb.toString();
    }
    
    static class State implements Comparable<State> {
        int[] counters;
        int presses;
        
        State(int[] counters, int presses) {
            this.counters = counters;
            this.presses = presses;
        }
        
        @Override
        public int compareTo(State other) {
            return Integer.compare(this.presses, other.presses);
        }
    }
}
