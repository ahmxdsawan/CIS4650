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
      
      // Process command line arguments
      for (int i = 0; i < argv.length; i++) {
        switch (argv[i]) {
          case "-a":
            showTree = true;
            break;
          case "-s":
            showSymbolTable = true;
            break;
          default:
            filename = argv[i];
            break;
        }
      }
      
      if (filename == null) {
        System.out.println("Usage: java -classpath /usr/share/java/cup.jar:. Main [-a] [-s] filename.cm");
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
      
      if (result != null) {
        if (showTree) {
          System.out.println("The abstract syntax tree is:");
          AbsynVisitor visitor = new ShowTreeVisitor();
          result.accept(visitor, 0); 
        }
        if (showSymbolTable) {
          System.out.println("The Symbol Table is:");
          AbsynVisitor visitor = new SemanticAnalyzer();
          result.accept(visitor, 0); 
        }
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
