import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day_10_Factory {
    public static void main(String[] args) throws IOException {
        long totalPresses = 0;
        int machineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                machineCount++;
                long result = solveMachine(line);
                if (result == Long.MAX_VALUE) {
                    System.err.println("Machine " + machineCount + ": Failed to solve");
                    totalPresses = Long.MAX_VALUE;
                    break;
                }
                totalPresses += result;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(Long.toString(totalPresses));
            bw.newLine();
        }
    }

    // Part 2: solve for joltage counters using BFS in counter space.
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
        int m = targetTokens.length; // number of counters
        int[] target = new int[m];
        for (int i = 0; i < m; i++) {
            target[i] = Integer.parseInt(targetTokens[i].trim());
        }

        // Parse buttons from the portion before the '{'
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

        // If target is all zeros, no presses needed.
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
        
        // Level-by-level BFS with pruning
        Set<String> current = new HashSet<>();
        current.add(Arrays.toString(new int[m]));
        String targetKey = Arrays.toString(target);
        
        int maxPresses = 1000;
        
        for (int presses = 0; presses < maxPresses; presses++) {
            if (current.contains(targetKey)) {
                return presses;
            }
            
            Set<String> next = new HashSet<>();
            
            for (String stateKey : current) {
                int[] state = parseState(stateKey, m);
                
                for (int[] button : buttons) {
                    int[] newState = state.clone();
                    boolean valid = true;
                    
                    for (int idx : button) {
                        newState[idx]++;
                        if (newState[idx] > target[idx]) {
                            valid = false;
                            break;
                        }
                    }
                    
                    if (valid) {
                        next.add(Arrays.toString(newState));
                    }
                }
            }
            
            // Prune if too large
            if (next.size() > 200000) {
                next = pruneStates(next, target, m, 100000);
            }
            
            current = next;
            if (current.isEmpty()) break;
        }
        
        return Long.MAX_VALUE;
    }
    
    private static Set<String> pruneStates(Set<String> states, int[] target, int m, int keep) {
        List<String> list = new ArrayList<>(states);
        list.sort((a, b) -> {
            int[] stateA = parseState(a, m);
            int[] stateB = parseState(b, m);
            return Integer.compare(manhattanDist(stateA, target), manhattanDist(stateB, target));
        });
        return new HashSet<>(list.subList(0, Math.min(keep, list.size())));
    }
    
    private static int manhattanDist(int[] state, int[] target) {
        int dist = 0;
        for (int i = 0; i < state.length; i++) {
            dist += Math.abs(target[i] - state[i]);
        }
        return dist;
    }
    
    private static int[] parseState(String key, int m) {
        String inner = key.substring(1, key.length() - 1);
        if (inner.isEmpty()) return new int[m];
        String[] parts = inner.split(", ");
        int[] state = new int[m];
        for (int i = 0; i < m && i < parts.length; i++) {
            state[i] = Integer.parseInt(parts[i]);
        }
        return state;
    }
}
