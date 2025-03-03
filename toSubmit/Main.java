/*
  Created by: 
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
      String filename = null;
      
      // Process command line arguments
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-a")) {
          showTree = true;
        } else {
          filename = argv[i];
        }
      }
      
      if (filename == null) {
        System.out.println("Usage: java -classpath /usr/share/java/cup.jar:. Main [-a] filename.cm");
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
         result.accept(visitor, 0); 
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
