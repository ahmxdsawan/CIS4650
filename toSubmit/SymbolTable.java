/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: SymbolTable.java
*/

import java.util.*;

public class SymbolTable {
    private Stack<HashMap<String, SymbolInfo>> scopes;

    public SymbolTable() {
        scopes = new Stack<>();
        enterScope(); 
    }

    public void enterScope() {
        scopes.push(new HashMap<String, SymbolInfo>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    // Adds a symbol to the current scope. Returns true if the symbol was added successfully.
    public boolean addSymbol(String name, SymbolInfo info) {
        HashMap<String, SymbolInfo> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // redeclaration error
        }
        currentScope.put(name, info);
        return true;
    }

    // Looks up a symbol in the current scope and enclosing scopes.
    // Returns null if the symbol is not found.
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

    // Returns the symbols in the current scope
    public Collection<SymbolInfo> getCurrentScopeSymbols() {
        if (!scopes.isEmpty()) {
            return scopes.peek().values();
        }
        return new ArrayList<SymbolInfo>();
    }

    // Print the symbol table
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