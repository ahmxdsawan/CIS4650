/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: SymbolInfo.java
*/

import java.util.List;
import absyn.TypeExp;

public class SymbolInfo {
    public String name;
    public int type;
    public boolean isArray;
    public List<SymbolInfo> parameters;
    
    public SymbolInfo(String name, int type, boolean isArray) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.parameters = null;
    }
    
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
        if (parameters != null) {
            StringBuilder paramsStr = new StringBuilder();
            paramsStr.append("(");
            for (int i = 0; i < parameters.size(); i++) {
                SymbolInfo param = parameters.get(i);
                paramsStr.append(param.name).append(" : ");
                switch(param.type) {
                    case TypeExp.INT: paramsStr.append("int"); break;
                    case TypeExp.BOOL: paramsStr.append("bool"); break;
                    case TypeExp.VOID: paramsStr.append("void"); break;
                    default: paramsStr.append("unknown");
                }
                if (i < parameters.size() - 1) {
                    paramsStr.append(", ");
                }
            }
            paramsStr.append(")");
            return "Function " + name + " : " + typeStr + " " + paramsStr.toString();
        } else {
            return name + " : " + typeStr;
        }
    }
}