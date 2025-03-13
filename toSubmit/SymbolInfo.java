/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: SymbolInfo.java
*/

import java.util.List;
import absyn.TypeExp;

public class SymbolInfo {
    public String name;
    public int type;       // Use constants from TypeExp (e.g., TypeExp.INT, TypeExp.BOOL, TypeExp.VOID)
    public boolean isArray;
    public List<SymbolInfo> parameters; // For functions
    
    // Constructor for variables
    public SymbolInfo(String name, int type, boolean isArray) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.parameters = null;
    }
    
    // Constructor for functions with parameters
    public SymbolInfo(String name, int type, List<SymbolInfo> parameters) {
        this.name = name;
        this.type = type;
        this.isArray = false;
        this.parameters = parameters;
    }
    
    public String toString() {
        String typeStr = "";
        switch (type) {
            case TypeExp.INT:
                typeStr = "int";
                break;
            case TypeExp.BOOL:
                typeStr = "bool";
                break;
            case TypeExp.VOID:
                typeStr = "void";
                break;
            default:
                typeStr = "unknown";
        }
        if (isArray) {
            typeStr += "[]";
        }
        return name + " : " + typeStr;
    }
}