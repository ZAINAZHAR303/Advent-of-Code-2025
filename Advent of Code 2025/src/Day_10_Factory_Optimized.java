import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day_10_Factory_Optimized {
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

        System.out.println("Total: " + totalPresses);
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
        int maxDepth = Arrays.stream(target).sum() + 50;
        
        // BFS with aggressive state limiting
        HashMap<String, Integer> current = new HashMap<>();
        current.put(stateKey(new int[m]), 0);
        
        String targetKey = stateKey(target);
        
        for (int depth = 0; depth < maxDepth; depth++) {
            if (current.containsKey(targetKey)) {
                return current.get(targetKey);
            }
            
            HashMap<String, Integer> next = new HashMap<>();
            
            for (String key : current.keySet()) {
                int[] state = parseState(key, m);
                int presses = current.get(key);
                
                for (int[] button : buttons) {
                    int[] newState = state.clone();
                    boolean valid = true;
                    boolean helpful = false;
                    
                    for (int idx : button) {
                        if (newState[idx] < target[idx]) {
                            helpful = true;
                        }
                        newState[idx]++;
                        if (newState[idx] > target[idx]) {
                            valid = false;
                            break;
                        }
                    }
                    
                    if (!valid || !helpful) continue;
                    
                    String newKey = stateKey(newState);
                    int newPresses = presses + 1;
                    
                    if (!next.containsKey(newKey) || next.get(newKey) > newPresses) {
                        next.put(newKey, newPresses);
                    }
                }
            }
            
            // Limit state space - keep only closest to target
            if (next.size() > 100000) {
                next = pruneStates(next, target, m, 50000);
            }
            
            current = next;
            if (current.isEmpty()) break;
        }
        
        return Long.MAX_VALUE;
    }
    
    private static HashMap<String, Integer> pruneStates(HashMap<String, Integer> states, 
                                                         int[] target, int m, int keep) {
        List<String> keys = new ArrayList<>(states.keySet());
        keys.sort((a, b) -> {
            int[] stateA = parseState(a, m);
            int[] stateB = parseState(b, m);
            int distA = distance(stateA, target);
            int distB = distance(stateB, target);
            if (distA != distB) return Integer.compare(distA, distB);
            return Integer.compare(states.get(a), states.get(b));
        });
        
        HashMap<String, Integer> pruned = new HashMap<>();
        for (int i = 0; i < Math.min(keep, keys.size()); i++) {
            String key = keys.get(i);
            pruned.put(key, states.get(key));
        }
        return pruned;
    }
    
    private static int distance(int[] state, int[] target) {
        int dist = 0;
        for (int i = 0; i < state.length; i++) {
            dist += Math.abs(target[i] - state[i]);
        }
        return dist;
    }
    
    private static String stateKey(int[] counters) {
        return Arrays.toString(counters);
    }
    
    private static int[] parseState(String key, int m) {
        String[] parts = key.substring(1, key.length() - 1).split(", ");
        int[] state = new int[m];
        for (int i = 0; i < m && i < parts.length; i++) {
            state[i] = Integer.parseInt(parts[i]);
        }
        return state;
    }
}
