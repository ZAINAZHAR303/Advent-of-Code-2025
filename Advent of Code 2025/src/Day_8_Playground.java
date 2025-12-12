import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day_8_Playground {
    private static class Edge {
        int a;
        int b;
        long dist2;

        Edge(int a, int b, long dist2) {
            this.a = a;
            this.b = b;
            this.dist2 = dist2;
        }
    }

    public static void main(String[] args) throws IOException {
        List<long[]> points = new ArrayList<>();

        // Read junction box positions from input.txt
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 3) continue; // ignore malformed lines
                long x = Long.parseLong(parts[0].trim());
                long y = Long.parseLong(parts[1].trim());
                long z = Long.parseLong(parts[2].trim());
                points.add(new long[]{x, y, z});
            }
        }

        if (points.isEmpty()) {
            writeOutput("0");
            return;
        }

        int n = points.size();

        // Build all pair distances
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            long[] pi = points.get(i);
            for (int j = i + 1; j < n; j++) {
                long[] pj = points.get(j);
                long dx = pi[0] - pj[0];
                long dy = pi[1] - pj[1];
                long dz = pi[2] - pj[2];
                long dist2 = dx * dx + dy * dy + dz * dz;
                edges.add(new Edge(i, j, dist2));
            }
        }

        // Sort edges by distance squared (ascending)
        edges.sort((e1, e2) -> Long.compare(e1.dist2, e2.dist2));

        // Disjoint Set Union (Union-Find) to track circuits
        int[] parent = new int[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        // Connect the 1000 closest pairs (or fewer if not enough pairs)
        int maxConnections = 1000;
        int connections = Math.min(maxConnections, edges.size());
        for (int i = 0; i < connections; i++) {
            Edge e = edges.get(i);
            union(parent, rank, e.a, e.b);
        }

        // Compute sizes of all circuits
        int[] compSize = new int[n];
        for (int i = 0; i < n; i++) {
            int root = find(parent, i);
            compSize[root]++;
        }

        List<Integer> sizes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (compSize[i] > 0) {
                sizes.add(compSize[i]);
            }
        }

        // Sort sizes descending and multiply three largest
        Collections.sort(sizes, Collections.reverseOrder());
        long result = 1L;
        int take = Math.min(3, sizes.size());
        for (int i = 0; i < take; i++) {
            result *= sizes.get(i);
        }

        writeOutput(String.valueOf(result));
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private static void union(int[] parent, int[] rank, int a, int b) {
        int ra = find(parent, a);
        int rb = find(parent, b);
        if (ra == rb) return;
        if (rank[ra] < rank[rb]) {
            parent[ra] = rb;
        } else if (rank[rb] < rank[ra]) {
            parent[rb] = ra;
        } else {
            parent[rb] = ra;
            rank[ra]++;
        }
    }

    private static void writeOutput(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
            bw.write(text);
            bw.newLine();
        }
    }
}
