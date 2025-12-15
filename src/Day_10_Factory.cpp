#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <unordered_set>
#include <unordered_map>
#include <queue>
#include <algorithm>
#include <climits>

using namespace std;

struct Machine {
    vector<vector<int>> buttons;
    vector<int> target;
};

// Hash function for vector<int>
struct VectorHash {
    size_t operator()(const vector<int>& v) const {
        size_t hash = 0;
        for (int i : v) {
            hash ^= std::hash<int>()(i) + 0x9e3779b9 + (hash << 6) + (hash >> 2);
        }
        return hash;
    }
};

int manhattanDist(const vector<int>& state, const vector<int>& target) {
    int dist = 0;
    for (size_t i = 0; i < state.size(); i++) {
        dist += abs(state[i] - target[i]);
    }
    return dist;
}

vector<vector<int>> pruneStates(const unordered_set<vector<int>, VectorHash>& states, 
                                 const vector<int>& target, int keepCount) {
    vector<pair<int, vector<int>>> scored;
    scored.reserve(states.size());
    
    for (const auto& state : states) {
        scored.push_back({manhattanDist(state, target), state});
    }
    sort(scored.begin(), scored.end());
    
    vector<vector<int>> result;
    result.reserve(keepCount);
    for (int i = 0; i < min(keepCount, (int)scored.size()); i++) {
        result.push_back(scored[i].second);
    }
    return result;
}

long long searchMinPresses(const vector<vector<int>>& buttons, const vector<int>& target) {
    int m = target.size();
    int maxPresses = 1000;
    int pruneThreshold = 200000;
    int keepCount = 100000;
    
    unordered_set<vector<int>, VectorHash> current;
    vector<int> start(m, 0);
    current.insert(start);
    
    for (int presses = 0; presses < maxPresses; presses++) {
        // Check if we reached target
        if (current.count(target) > 0) {
            return presses;
        }
        
        unordered_set<vector<int>, VectorHash> next;
        next.reserve(current.size() * buttons.size());
        
        for (const auto& state : current) {
            // Try pressing each button
            for (const auto& button : buttons) {
                vector<int> newState = state;
                bool valid = true;
                
                for (int idx : button) {
                    newState[idx]++;
                    if (newState[idx] > target[idx]) {
                        valid = false;
                        break;
                    }
                }
                
                if (valid) {
                    next.insert(newState);
                }
            }
        }
        
        // Prune if too many states
        if (next.size() > pruneThreshold) {
            auto pruned = pruneStates(next, target, keepCount);
            next.clear();
            next.reserve(pruned.size());
            for (const auto& state : pruned) {
                next.insert(state);
            }
        }
        
        current = move(next);
        
        if (current.empty()) {
            break;
        }
    }
    
    return LLONG_MAX;
}

Machine parseMachine(const string& line) {
    Machine machine;
    
    // Find joltage requirements {3,5,4,7}
    size_t braceStart = line.find('{');
    size_t braceEnd = line.find('}');
    string targetStr = line.substr(braceStart + 1, braceEnd - braceStart - 1);
    
    stringstream ss(targetStr);
    string num;
    while (getline(ss, num, ',')) {
        machine.target.push_back(stoi(num));
    }
    
    // Find buttons (0,2,3) ...
    size_t pos = 0;
    while ((pos = line.find('(', pos)) != string::npos) {
        size_t endPos = line.find(')', pos);
        string buttonStr = line.substr(pos + 1, endPos - pos - 1);
        
        vector<int> button;
        stringstream buttonSS(buttonStr);
        string idx;
        while (getline(buttonSS, idx, ',')) {
            button.push_back(stoi(idx));
        }
        machine.buttons.push_back(button);
        
        pos = endPos + 1;
    }
    
    return machine;
}

int main() {
    ifstream input("input.txt");
    ofstream output("output.txt");
    
    if (!input.is_open()) {
        cerr << "Could not open input.txt" << endl;
        return 1;
    }
    
    long long totalPresses = 0;
    string line;
    int machineNum = 0;
    
    while (getline(input, line)) {
        if (line.empty()) continue;
        
        machineNum++;
        Machine machine = parseMachine(line);
        
        cout << "Processing machine " << machineNum << "..." << endl;
        
        long long minPresses = searchMinPresses(machine.buttons, machine.target);
        
        if (minPresses == LLONG_MAX) {
            cout << "No solution found for machine " << machineNum << endl;
            output << "No solution" << endl;
            input.close();
            output.close();
            return 1;
        }
        
        cout << "Machine " << machineNum << ": " << minPresses << " presses" << endl;
        totalPresses += minPresses;
    }
    
    output << totalPresses << endl;
    cout << "Total presses: " << totalPresses << endl;
    
    input.close();
    output.close();
    
    return 0;
}
