import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

        int components = n;
        long result = 0L;

        // Connect closest pairs until all boxes are in a single circuit.
        for (Edge e : edges) {
            int ra = find(parent, e.a);
            int rb = find(parent, e.b);
            if (ra == rb) {
                // Already in the same circuit; connecting doesn't change anything.
                continue;
            }

            union(parent, rank, ra, rb);
            components--;

            if (components == 1) {
                long xa = points.get(e.a)[0];
                long xb = points.get(e.b)[0];
                result = xa * xb;
                break;
            }
        }

        writeOutput(String.valueOf(result));
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private static void union(int[] parent, int[] rank, int aRoot, int bRoot) {
        int ra = aRoot;
        int rb = bRoot;
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
