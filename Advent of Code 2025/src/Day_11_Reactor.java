import java.io.*;
import java.util.*;

public class Day_11_Reactor {
    
    static class PathState {
        String current;
        Set<String> path;
        Set<String> requiredVisited;
        
        PathState(String current, Set<String> path, Set<String> requiredVisited) {
            this.current = current;
            this.path = path;
            this.requiredVisited = requiredVisited;
        }
    }
    
    public static void main(String[] args) {
        try {
            Map<String, List<String>> graph = parseInput("input.txt");
            
            System.out.println("Graph has " + graph.size() + " nodes");
            System.out.println("Finding all paths from 'svr' to 'out' that visit both 'dac' and 'fft'...");
            
            Set<String> requiredNodes = new HashSet<>(Arrays.asList("dac", "fft"));
            long numPaths = countPaths(graph, "svr", "out", requiredNodes);
            
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write(numPaths + "\n");
            writer.close();
            
            System.out.println("Number of paths from 'svr' to 'out' that visit both 'dac' and 'fft': " + numPaths);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static long countPaths(Map<String, List<String>> graph, String start, String end, Set<String> requiredNodes) {
        Stack<PathState> stack = new Stack<>();
        
        Set<String> initialPath = new HashSet<>();
        initialPath.add(start);
        
        Set<String> initialRequired = new HashSet<>();
        if (requiredNodes.contains(start)) {
            initialRequired.add(start);
        }
        
        stack.push(new PathState(start, initialPath, initialRequired));
        
        long count = 0;
        long iterations = 0;
        
        while (!stack.isEmpty()) {
            PathState state = stack.pop();
            
            iterations++;
            if (iterations % 100000 == 0) {
                System.out.println("  Processed " + iterations + " paths, found " + count + " valid paths so far...");
            }
            
            // Reached destination
            if (state.current.equals(end)) {
                if (state.requiredVisited.equals(requiredNodes)) {
                    count++;
                }
                continue;
            }
            
            // Get neighbors
            List<String> neighbors = graph.get(state.current);
            if (neighbors == null) {
                continue;
            }
            
            // Add neighbors to stack
            for (String neighbor : neighbors) {
                if (!state.path.contains(neighbor)) {  // Avoid cycles
                    Set<String> newPath = new HashSet<>(state.path);
                    newPath.add(neighbor);
                    
                    Set<String> newRequired = new HashSet<>(state.requiredVisited);
                    if (requiredNodes.contains(neighbor)) {
                        newRequired.add(neighbor);
                    }
                    
                    stack.push(new PathState(neighbor, newPath, newRequired));
                }
            }
        }
        
        return count;
    }
    
    static Map<String, List<String>> parseInput(String filename) throws IOException {
        Map<String, List<String>> graph = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || !line.contains(": ")) {
                continue;
            }
            
            String[] parts = line.split(": ");
            if (parts.length != 2) {
                continue;
            }
            
            String device = parts[0];
            String[] outputs = parts[1].split(" ");
            graph.put(device, Arrays.asList(outputs));
        }
        
        reader.close();
        return graph;
    }
}
