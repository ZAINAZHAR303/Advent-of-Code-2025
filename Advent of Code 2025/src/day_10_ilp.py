import re
import numpy as np
from scipy.optimize import linprog, milp, LinearConstraint, Bounds
from scipy.sparse import csr_matrix

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

def solve_machine_ilp(buttons, target):
    """Solve using Integer Linear Programming"""
    m = len(target)  # number of counters
    n = len(buttons)  # number of buttons
    
    # Build coefficient matrix A where A[i][j] = 1 if button j affects counter i
    A = np.zeros((m, n), dtype=int)
    for j, button in enumerate(buttons):
        for i in button:
            A[i][j] = 1
    
    # We want to minimize sum of button presses (objective: minimize sum(x))
    c = np.ones(n)
    
    # Constraint: A * x = target (each counter must reach its target value)
    # where x[j] is the number of times button j is pressed
    
    try:
        # Use integer linear programming
        # Constraints: A @ x == target, x >= 0, x integer
        integrality = np.ones(n)  # All variables are integers
        
        result = milp(
            c=c,
            integrality=integrality,
            bounds=Bounds(lb=np.zeros(n), ub=np.full(n, max(target) * 2)),
            constraints=LinearConstraint(A, lb=target, ub=target),
            options={'disp': False}
        )
        
        if result.success:
            return int(round(result.fun))
        else:
            return None
    except Exception as e:
        print(f"  ILP solver error: {e}")
        return None

def main():
    try:
        machines = parse_input('input.txt')
        total_presses = 0
        
        for i, (buttons, target) in enumerate(machines, 1):
            print(f"Processing machine {i}...")
            
            min_presses = solve_machine_ilp(buttons, target)
            
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
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()
