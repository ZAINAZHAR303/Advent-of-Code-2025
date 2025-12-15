def parse_input(filename):
    """Parse the input file and build a directed graph"""
    graph = {}
    with open(filename, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or ':' not in line:
                continue
            
            # Parse "device: output1 output2 ..."
            parts = line.split(': ')
            if len(parts) != 2:
                continue
            
            device = parts[0]
            outputs = parts[1].split()
            graph[device] = outputs
    
    return graph

from collections import deque

def count_paths_iterative(graph, start, end, required_nodes):
    """Count paths using iterative DFS with a stack"""
    # Stack contains: (current_node, path_set, visited_required_set)
    stack = [(start, frozenset([start]), frozenset([start]) & required_nodes)]
    count = 0
    iterations = 0
    
    while stack:
        current, path, req_visited = stack.pop()
        
        iterations += 1
        if iterations % 100000 == 0:
            print(f"  Processed {iterations} paths, found {count} valid paths so far...")
        
        # Reached destination
        if current == end:
            if req_visited == required_nodes:
                count += 1
            continue
        
        # Get neighbors
        if current not in graph:
            continue
        
        # Add neighbors to stack
        for neighbor in graph[current]:
            if neighbor not in path:  # Avoid cycles
                new_path = path | {neighbor}
                new_req = req_visited | ({neighbor} & required_nodes)
                stack.append((neighbor, new_path, new_req))
    
    return count

def main():
    graph = parse_input('input.txt')
    
    print(f"Graph has {len(graph)} nodes")
    print("Finding all paths from 'svr' to 'out' that visit both 'dac' and 'fft'...")
    required_nodes = frozenset({'dac', 'fft'})
    num_paths = count_paths_iterative(graph, 'svr', 'out', required_nodes)
    
    with open('output.txt', 'w') as f:
        f.write(f"{num_paths}\n")
    
    print(f"Number of paths from 'svr' to 'out' that visit both 'dac' and 'fft': {num_paths}")

if __name__ == '__main__':
    main()
