// SymbolTable.java
import java.util.*;

public class SymbolTable {
    private Stack<HashMap<String, SymbolInfo>> scopes;

    public SymbolTable() {
        scopes = new Stack<>();
        enterScope(); // Create the global scope
    }

    public void enterScope() {
        scopes.push(new HashMap<String, SymbolInfo>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    // Inserts a symbol into the current scope.
    // Returns false if the symbol already exists in this scope.
    public boolean addSymbol(String name, SymbolInfo info) {
        HashMap<String, SymbolInfo> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // redeclaration error
        }
        currentScope.put(name, info);
        return true;
    }

    // Looks up the symbol starting from the innermost scope outward.
    public SymbolInfo lookup(String name) {
        ListIterator<HashMap<String, SymbolInfo>> it = scopes.listIterator(scopes.size());
        while (it.hasPrevious()) {
            HashMap<String, SymbolInfo> scope = it.previous();
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    // Get all symbols in the current scope
    public Collection<SymbolInfo> getCurrentScopeSymbols() {
        if (!scopes.isEmpty()) {
            return scopes.peek().values();
        }
        return new ArrayList<SymbolInfo>();
    }

    // Print the symbol table for all scopes.
    public void print() {
        int level = 0;
        for (HashMap<String, SymbolInfo> scope : scopes) {
            System.out.println("Scope Level " + level + ":");
            for (SymbolInfo info : scope.values()) {
                System.out.println("    " + info);
            }
            level++;
        }
    }
}