import java.util.ArrayList;

public class Entry {
    String name;
    int type;
    int scope;
    boolean isArray;
    int arraySize;
    boolean isFunction;
    ArrayList<Entry> parameters;
    int line;
    int column;
    
    // Constructor for variables
    public Entry(String name, int type, int line, int column, boolean isArray, int arraySize) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        this.isArray = isArray;
        this.arraySize = arraySize;
        this.isFunction = false;
        this.parameters = null;
    }
    
    // Constructor for functions
    public Entry(String name, int returnType, int line, int column, ArrayList<Entry> parameters) {
        this.name = name;
        this.type = returnType;
        this.line = line;
        this.column = column;
        this.isArray = false;
        this.arraySize = 0;
        this.isFunction = true;
        this.parameters = parameters;
    }
    
    @Override
    public String toString() {
        if (isFunction) {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" : ").append(SemanticAnalyzer.getTypeName(type)).append(" (function)");
            if (parameters != null && !parameters.isEmpty()) {
                sb.append("\n");
                // Add indentation for parameters
                for (int i = 0; i < scope + 1; i++) {
                    sb.append("  ");
                }
                sb.append("Parameters: [");
                for (int i = 0; i < parameters.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Entry param = parameters.get(i);
                    sb.append(param.name).append(" : ").append(SemanticAnalyzer.getTypeName(param.type));
                    if (param.isArray) sb.append("[]");
                }
                sb.append("]");
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" : ").append(SemanticAnalyzer.getTypeName(type));
            if (isArray) sb.append("[]");
            sb.append(" (").append(isArray ? "array" : "variable").append(")");
            return sb.toString();
        }
    }
}