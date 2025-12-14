import java.io.*;
import java.util.*;

public class Day_10_Part2_Optimized {
    
    static class Machine {
        List<int[]> buttons;
        int[] target;
        
        Machine(List<int[]> buttons, int[] target) {
            this.buttons = buttons;
            this.target = target;
        }
    }
    
    public static void main(String[] args) {
        try {
            List<Machine> machines = parseInput("input.txt");
            long totalPresses = 0;
            
            for (int i = 0; i < machines.size(); i++) {
                Machine machine = machines.get(i);
                System.out.println("Processing machine " + (i + 1) + "...");
                
                long minPresses = solveMachine(machine);
                
                if (minPresses == Long.MAX_VALUE) {
                    System.out.println("No solution found for machine " + (i + 1));
                    writeOutput("output.txt", "No solution");
                    return;
                }
                
                System.out.println("Machine " + (i + 1) + ": " + minPresses + " presses");
                totalPresses += minPresses;
            }
            
            writeOutput("output.txt", String.valueOf(totalPresses));
            System.out.println("Total presses: " + totalPresses);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static long solveMachine(Machine machine) {
        int m = machine.target.length;
        int n = machine.buttons.size();
        
        // Use BFS with optimized state management
        Map<String, Integer> visited = new HashMap<>();
        PriorityQueue<State> pq = new PriorityQueue<>((a, b) -> {
            int distDiff = a.heuristic - b.heuristic;
            if (distDiff != 0) return distDiff;
            return a.presses - b.presses;
        });
        
        int[] start = new int[m];
        State startState = new State(start, 0, machine.target);
        pq.offer(startState);
        visited.put(Arrays.toString(start), 0);
        
        while (!pq.isEmpty()) {
            State current = pq.poll();
            
            // Check if reached target
            if (Arrays.equals(current.counters, machine.target)) {
                return current.presses;
            }
            
            // Skip if we've found a better path to this state
            String key = Arrays.toString(current.counters);
            if (visited.containsKey(key) && visited.get(key) < current.presses) {
                continue;
            }
            
            // Try pressing each button
            for (int[] button : machine.buttons) {
                int[] newCounters = current.counters.clone();
                boolean valid = true;
                
                for (int idx : button) {
                    newCounters[idx]++;
                    if (newCounters[idx] > machine.target[idx]) {
                        valid = false;
                        break;
                    }
                }
                
                if (valid) {
                    String newKey = Arrays.toString(newCounters);
                    int newPresses = current.presses + 1;
                    
                    if (!visited.containsKey(newKey) || visited.get(newKey) > newPresses) {
                        visited.put(newKey, newPresses);
                        pq.offer(new State(newCounters, newPresses, machine.target));
                    }
                }
            }
            
            // Limit memory usage
            if (visited.size() > 500000) {
                System.out.println("State space too large, may not find optimal solution");
                break;
            }
        }
        
        return Long.MAX_VALUE;
    }
    
    static class State {
        int[] counters;
        int presses;
        int heuristic;
        
        State(int[] counters, int presses, int[] target) {
            this.counters = counters;
            this.presses = presses;
            this.heuristic = presses + manhattanDist(counters, target);
        }
    }
    
    static int manhattanDist(int[] state, int[] target) {
        int dist = 0;
        for (int i = 0; i < state.length; i++) {
            dist += Math.abs(state[i] - target[i]);
        }
        return dist;
    }
    
    static List<Machine> parseInput(String filename) throws IOException {
        List<Machine> machines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            // Parse target {3,5,4,7}
            int braceStart = line.indexOf('{');
            int braceEnd = line.indexOf('}');
            String targetStr = line.substring(braceStart + 1, braceEnd);
            String[] targetParts = targetStr.split(",");
            int[] target = new int[targetParts.length];
            for (int i = 0; i < targetParts.length; i++) {
                target[i] = Integer.parseInt(targetParts[i].trim());
            }
            
            // Parse buttons
            List<int[]> buttons = new ArrayList<>();
            int pos = 0;
            while ((pos = line.indexOf('(', pos)) != -1) {
                int endPos = line.indexOf(')', pos);
                String buttonStr = line.substring(pos + 1, endPos);
                String[] buttonParts = buttonStr.split(",");
                int[] button = new int[buttonParts.length];
                for (int i = 0; i < buttonParts.length; i++) {
                    button[i] = Integer.parseInt(buttonParts[i].trim());
                }
                buttons.add(button);
                pos = endPos + 1;
            }
            
            machines.add(new Machine(buttons, target));
        }
        
        br.close();
        return machines;
    }
    
    static void writeOutput(String filename, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(content);
        bw.newLine();
        bw.close();
    }
}
