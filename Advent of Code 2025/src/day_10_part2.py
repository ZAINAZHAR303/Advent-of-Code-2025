import re
import heapq
from collections import defaultdict

def parse_input(filename):
    machines = []
    with open(filename, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            
            # Parse target {3,5,4,7}
            target_match = re.search(r'\{([^}]+)\}', line)
            target = list(map(int, target_match.group(1).split(',')))
            
            # Parse buttons (0,2,3)
            buttons = []
            for match in re.finditer(r'\(([^)]+)\)', line):
                button = list(map(int, match.group(1).split(',')))
                buttons.append(button)
            
            machines.append((buttons, target))
    
    return machines

def manhattan_dist(state, target):
    return sum(abs(s - t) for s, t in zip(state, target))

def solve_machine(buttons, target):
    m = len(target)
    
    # Use level-by-level BFS with aggressive pruning
    current = {tuple([0] * m)}
    target_tuple = tuple(target)
    
    for presses in range(1000):
        if target_tuple in current:
            return presses
        
        if not current:
            break
        
        next_states = set()
        
        for state in current:
            for button in buttons:
                new_state = list(state)
                valid = True
                
                for idx in button:
                    new_state[idx] += 1
                    if new_state[idx] > target[idx]:
                        valid = False
                        break
                
                if valid:
                    next_states.add(tuple(new_state))
        
        # Aggressive pruning - keep only closest states
        if len(next_states) > 100000:
            scored = [(manhattan_dist(state, target), state) for state in next_states]
            scored.sort()
            next_states = set(state for _, state in scored[:50000])
        
        current = next_states
        
        if presses % 10 == 0 and presses > 0:
            print(f"  Press {presses}: {len(current)} states")
    
    return None

def main():
    machines = parse_input('input.txt')
    total_presses = 0
    
    for i, (buttons, target) in enumerate(machines, 1):
        print(f"Processing machine {i}...")
        
        min_presses = solve_machine(buttons, target)
        
        if min_presses is None:
            print(f"No solution found for machine {i}")
            with open('output.txt', 'w') as f:
                f.write("No solution\n")
            return
        
        print(f"Machine {i}: {min_presses} presses")
        total_presses += min_presses
    
    with open('output.txt', 'w') as f:
        f.write(f"{total_presses}\n")
    
    print(f"Total presses: {total_presses}")

if __name__ == '__main__':
    main()
