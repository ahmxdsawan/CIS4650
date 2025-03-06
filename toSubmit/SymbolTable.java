import java.util.*;

// not sure why this import has to be here
// but vscode was being weird without it
import java.util.Stack; 

public class SymbolTable {
    private HashMap<String, ArrayList<Entry>> table;
    private Stack<Integer> scopeStack;
    private int currentScope;
    private int nextScope;
    private boolean showSymbolTable;

    public SymbolTable() {
        table = new HashMap<>();
        scopeStack = new Stack<>();
        currentScope = 0;
        nextScope = 1;
        enterScope(); // Global scope
    }

    public void setShowSymbolTable(boolean show) {
        this.showSymbolTable = show;
    }

    public void enterScope() {
        scopeStack.push(currentScope);
        currentScope = nextScope++;
    }

    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            currentScope = scopeStack.pop();
        }
    }

    public boolean insert(Entry symbol) {
        String name = symbol.name;
        
        // Check if the symbol exists in the current scope
        ArrayList<Entry> symbols = table.get(name);
        if (symbols != null) {
            for (Entry s : symbols) {
                if (s.scope == currentScope) {
                    return false; // Entry already exists in current scope
                }
            }
        } else {
            symbols = new ArrayList<>();
            table.put(name, symbols);
        }
        
        symbol.scope = currentScope;
        symbols.add(symbol);
        return true;
    }

    public Entry lookup(String name) {
        ArrayList<Entry> symbols = table.get(name);
        if (symbols != null) {
            // Start from the most recently added symbol (innermost matching scope)
            for (int i = symbols.size() - 1; i >= 0; i--) {
                Entry s = symbols.get(i);
                int scope = s.scope;
                
                // Check if this scope is accessible from current scope
                for (int j = scopeStack.size() - 1; j >= 0; j--) {
                    if (scopeStack.get(j) == scope) {
                        return s;
                    }
                }
                
                // Check current scope
                if (currentScope == scope) {
                    return s;
                }
            }
        }
        return null; // Entry not found
    }

    public Entry lookupCurrentScope(String name) {
        ArrayList<Entry> symbols = table.get(name);
        if (symbols != null) {
            for (Entry s : symbols) {
                if (s.scope == currentScope) {
                    return s;
                }
            }
        }
        return null;
    }

    public void printCurrentScope() {
        if (!showSymbolTable) return;
        
        // Collect all symbols in the current scope
        HashMap<String, Entry> scopeSymbols = new HashMap<>();
        for (Map.Entry<String, ArrayList<Entry>> entry : table.entrySet()) {
            for (Entry s : entry.getValue()) {
                if (s.scope == currentScope) {
                    scopeSymbols.put(s.name, s);
                }
            }
        }
        
        // Print all symbols alphabetically
        ArrayList<String> names = new ArrayList<>(scopeSymbols.keySet());
        Collections.sort(names);
        
        for (String name : names) {
            Entry s = scopeSymbols.get(name);
            System.out.println(s);
        }
    }
}