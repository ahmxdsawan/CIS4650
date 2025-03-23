/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: Main.java
  To Build: 
  After the Scanner.java, cm.flex, and cm.cup have been processed, do:
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.cm

  where gcd.cm is an test input file for the cm language.
*/
   
import java.io.*;
import absyn.*;
   
class Main {
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      boolean showTree = false;
      boolean showSymbolTable = false;
      String filename = null;
      boolean genCode = false;
      
      // Process command line arguments
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-a")) {
          showTree = true;
        } else if (argv[i].equals("-s")) {
          showSymbolTable = true;
        } else if (argv[i].equals("-c")) {
          genCode = true;
        } else {
          filename = argv[i];
        }
      }

      if (filename == null) {
        System.out.println("Usage: java -classpath /usr/share/java/cup.jar:. Main [-a] [-s] [-c] filename.cm");
        System.exit(1);
      }
      
      parser p = new parser(new Lexer(new FileReader(filename)));
      Absyn result = (Absyn)(p.parse().value);   

      // Check parser validity
        if (!parser.valid) {
            System.err.println("Parsing completed with errors.");
        }
        else {
            System.out.println("Parsing completed successfully.");
        }
      
      if (showTree && result != null) {
         System.out.println("The abstract syntax tree is:");
         AbsynVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0, false); 
      }

      if (result != null) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer(showSymbolTable);
        result.accept(analyzer, 0, false);
        if (showSymbolTable) {
          analyzer.printGlobalScope();
        }
      }

      // If -c option is provided, perform code generation
      if (genCode && result != null) {
         CodeGenerator cg = new CodeGenerator("output.tm");
         cg.generate(result);
         System.out.println("TM assembly code generated in output.tm");
      }
      
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}