import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Day_10_Compact {
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
                System.out.println("Machine " + lineNum + ": " + result);
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
        
        // Layer-by-layer BFS with pruning
        Set<String> current = new HashSet<>();
        current.add(encodeState(new int[m]));
        String targetEncoded = encodeState(target);
        
        int maxSteps = 500; // Reasonable upper bound
        
        for (int steps = 0; steps < maxSteps; steps++) {
            if (current.contains(targetEncoded)) {
                return steps;
            }
            
            Set<String> next = new HashSet<>();
            
            for (String encoded : current) {
                int[] state = decodeState(encoded, m);
                
                for (int[] button : buttons) {
                    int[] newState = state.clone();
                    boolean valid = true;
                    boolean useful = false;
                    
                    for (int idx : button) {
                        if (newState[idx] < target[idx]) {
                            useful = true;
                        }
                        newState[idx]++;
                        if (newState[idx] > target[idx]) {
                            valid = false;
                            break;
                        }
                    }
                    
                    if (valid && useful) {
                        next.add(encodeState(newState));
                    }
                }
            }
            
            // Prune if getting too large
            if (next.size() > 200000) {
                next = pruneToClosest(next, target, m, 100000);
            }
            
            current = next;
            if (current.isEmpty()) break;
            
            if (steps % 10 == 0 && steps > 0) {
                System.out.println("  Step " + steps + ": " + current.size() + " states");
            }
        }
        
        return Long.MAX_VALUE;
    }
    
    private static Set<String> pruneToClosest(Set<String> states, int[] target, int m, int keep) {
        List<String> list = new ArrayList<>(states);
        list.sort((a, b) -> {
            int[] stateA = decodeState(a, m);
            int[] stateB = decodeState(b, m);
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
    
    private static String encodeState(int[] state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(state[i]);
        }
        return sb.toString();
    }
    
    private static int[] decodeState(String encoded, int m) {
        String[] parts = encoded.split(",");
        int[] state = new int[m];
        for (int i = 0; i < m && i < parts.length; i++) {
            state[i] = Integer.parseInt(parts[i]);
        }
        return state;
    }
}
