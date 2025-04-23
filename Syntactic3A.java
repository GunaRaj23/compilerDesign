package ADT;

public class Syntactic3A {
   private String filein; //The full file path to input file
   private SymbolTable symbolList; //Symbol table storing ident/const
   private Lexical lex; //Lexical analyzer
   private Lexical.token token; //Next Token retrieved
   private boolean traceon; //Controls tracing mode
   private int level = 0; //Controls indent for trace mode
   private boolean anyErrors; //Set TRUE if an error happens
   private final int symbolSize = 250;

   public Syntactic3A(String filename, boolean traceOn) {
       filein = filename;
       traceon = traceOn;
       symbolList = new SymbolTable(symbolSize);
       lex = new Lexical(filein, symbolList, true);
       lex.setPrintToken(traceOn);
       anyErrors = false;
   }

   //The interface to the syntax analyzer, initiates parsing
   
   public void parse() {
       int recur=0;
       token = lex.GetNextToken();
       recur = Program();
   }

   private int Program() {

       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Program",true);
       if (token.code == lex.codeFor("PROGR")) {
           token = lex.GetNextToken();
           recur = ProgIdentifier();
           if (token.code == lex.codeFor("SEMIC")) {
               token = lex.GetNextToken();
               recur = Block();
               if (token.code == lex.codeFor("_DOT")) {
                   if(!anyErrors) {
                   	System.out.println("Success.");
                   }else {
                   	System.out.println("Compilation Failed.");
                   }
               } else {
                   error("_DOT", token.lexeme);
               }
           } else {
               error("SEMIC", token.lexeme);
           }
       } else {
           error("PROGR", token.lexeme);
       }
       trace("Program",false);
       return recur;
   }
   //Non-Terminal ProgIddentifier
   private int ProgIdentifier() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       
       if (token.code == lex.codeFor("IDENT")) {
           symbolList.SetPurpose(symbolList.LookupSymbol(token.lexeme), SymbolPurpose.PROGRAM);
           token = lex.GetNextToken();
       }
       return recur;
   }
   //Non-Terminal Block
   private int Block() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Block", true);
       while (token.code == lex.codeFor("_VAR")) {
           recur = VariableDecSec();
       }
       if (token.code == lex.codeFor("BEGIN")) {
           recur = BlockBody();
       }
       trace("Block", false);
       return recur;
   }

   //Non-Terminal for Variable Declaration Section(VariableDecSec).
   
   private int VariableDecSec() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Variabledecsec", true);
       if (token.code == lex.codeFor("_VAR")) {
           token = lex.GetNextToken();
           
           while (token.code == lex.codeFor("IDENT")) {
               recur = VariableDeclaration();
           }
       } else {
           error("VAR", token.lexeme);
       }
       trace("variabledecsec", false);
       return recur;
   }


   //Non-Terminal for Variable Declaration
   private int VariableDeclaration() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       if (token.code == lex.codeFor("IDENT")) {
           token = lex.GetNextToken();
           while (token.code == lex.codeFor("COMMA")) {
               token = lex.GetNextToken();
               if (token.code == lex.codeFor("IDENT")) {
                   token = lex.GetNextToken();
               } else {
                   error("IDENT", token.lexeme);
               }
           }
           if (token.code == lex.codeFor("COLON")) {
               token = lex.GetNextToken();
               recur = SimpleType();
               if (token.code == lex.codeFor("SEMIC")) {
                   token = lex.GetNextToken();                        
               } else {
                   error("SEMIC", token.lexeme);
               }
           }else {
               error("COLON", token.lexeme);
           }
       } else {
           error("IDENTIFIER", token.lexeme);
       }
       return recur;
   }
   
   
   //Non-Terminal for Block-Body
   private int BlockBody() {
//       int recur =  0;
//       if (anyErrors) {
//           return -1;
//       }
//       trace("Blockbody", true);
//       if (token.code == lex.codeFor("BEGIN")) {
//           token = lex.GetNextToken();
//           recur = Statement();
//           while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
//               token = lex.GetNextToken();
//               recur = Statement();
//           }
//           if (token.code == lex.codeFor("END")) {
//               token = lex.GetNextToken();
//           } else {
//               error("END", token.lexeme);
//           }
//       } else {
//           error("BEGIN", token.lexeme);
//       }
//       trace("Blockbody", false);
//       return recur;         
        
        int recur = 0;
        if (anyErrors) return -1;
        trace("Blockbody", true);

        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();

            while (!lex.EOF() && token.code != lex.codeFor("END")) {
                // Resynchronize before attempting a new statement
                while (!lex.EOF() &&
                       token.code != lex.codeFor("IDENT") &&
                       token.code != lex.codeFor("BEGIN") &&
                       token.code != lex.codeFor("_IF_") &&
                       token.code != lex.codeFor("WHILE") &&
                       token.code != lex.codeFor("REPEA") &&
                       token.code != lex.codeFor("_FOR") &&
                       token.code != lex.codeFor("WRITE") &&
                       token.code != lex.codeFor("WRITELN") &&
                       token.code != lex.codeFor("READL") &&
                       token.code != lex.codeFor("READLN") &&
                       token.code != lex.codeFor("END")) {
                    error("Statement start", token.lexeme);
                    token = lex.GetNextToken();
                }

                if (token.code == lex.codeFor("END")) break;

                recur = Statement();

                if (token.code == lex.codeFor("SEMIC")) {
                    token = lex.GetNextToken();
                } else if (token.code != lex.codeFor("END")) {
                    error("SEMIC or END", token.lexeme);
                    while (!lex.EOF() &&
                           token.code != lex.codeFor("SEMIC") &&
                           token.code != lex.codeFor("END")) {
                        token = lex.GetNextToken();
                    }
                    if (token.code == lex.codeFor("SEMIC")) {
                        token = lex.GetNextToken();
                    }
                }
            }

            if (token.code == lex.codeFor("END")) {
                token = lex.GetNextToken();
            } else {
                error("END", token.lexeme);
            }
        } else {
            error("BEGIN", token.lexeme);
        }

        trace("Blockbody", false);
        return recur;
   }
   //Non-Terminal for Statement
   private int Statement() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Statement", true);

       
       if (token.code == lex.codeFor("IDENT")) {
           trace("handleAssignment", true);
           trace("Variable", true);
           token = lex.GetNextToken();
           trace("Variable", false);
           if (token.code == lex.codeFor("_AS_")) {
               recur = handleAssignment();
               recur = SimpleExpression();
           } else {
               error("_AS_", token.lexeme);
           }
           trace("handleAssignment", false);
           trace("Statement", false);
           return recur;
       }
       
       if (token.code == lex.codeFor("BEGIN")) {
           trace("Block", true);
           recur = BlockBody();
           trace("Block", false);
           trace("Statement", false);
           return recur;
       }
       
       if (token.code == lex.codeFor("_IF_")) {
           trace("handleIf", true);
           token = lex.GetNextToken();
           recur = RelExpression();
           if (token.code == lex.codeFor("THEN")) {
               token = lex.GetNextToken();
               recur = Statement();
               if (token.code == lex.codeFor("ELSE")) {
                   token = lex.GetNextToken();
                   recur = Statement();
               }
           } else {
               error("THEN", token.lexeme);
           }
           trace("handleIf", false);
           trace("Statement", false);
           return recur;
       }
       
       if (token.code == lex.codeFor("WHILE")) {
           trace("handleWhile", true);
           token = lex.GetNextToken();
           recur = RelExpression();
           if (token.code == lex.codeFor("_DO_")) {
               token = lex.GetNextToken();
               recur = Statement();
           } else {
               error("_DO_", token.lexeme);
           }
           trace("handleWhile", false);
           trace("Statement", false);
           return recur;
       }
       
       

       if (token.code == lex.codeFor("REPEA")) {
           trace("handleRepeat", true);
           token = lex.GetNextToken();
           recur = Statement();
           if (token.code == lex.codeFor("UNTIL")) {
               token = lex.GetNextToken();
               recur = RelExpression();
           } else {
               error("UNTIL", token.lexeme);
           }
           trace("handleRepeat", false);
           trace("Statement", false);
           return recur;
       }

       

       if (token.code == lex.codeFor("_FOR")) {
           trace("handleFor", true);
           token = lex.GetNextToken();
           if (token.code == lex.codeFor("IDENT")) {
               trace("Variable", true);
               token = lex.GetNextToken();
               trace("Variable", false);
               if (token.code == lex.codeFor("_AS_")) {
                   token = lex.GetNextToken();
                   recur = SimpleExpression();
                   if (token.code == lex.codeFor("_TO_")) {
                       token = lex.GetNextToken();
                       recur = SimpleExpression();
                       if (token.code == lex.codeFor("_DO_")) {
                           token = lex.GetNextToken();
                           recur = Statement();
                       } else {
                           error("DO", token.lexeme);
                       }
                   } else {
                       error("TO", token.lexeme);
                   }
               } else {
                   error("ASSIGN", token.lexeme);
               }
                   
           } else {
               error("IDENTIFIER", token.lexeme);
           }
           trace("handleFor", false);
           trace("Statement", false);
           return recur;
       }

       

       if(token.code == lex.codeFor("WRITE")){
           trace("handlePrintln", true);
           token =  lex.GetNextToken();
           if(token.code == lex.codeFor("PARAO")){
               token = lex.GetNextToken();
               if(token.code == lex.codeFor("NCFLO")||token.code==lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR") || token.code ==  lex.codeFor("MULTI")  || token.code == lex.codeFor("DIVID") || token.code == lex.codeFor("NCINT") || token.code == lex.codeFor("INTEG") || token.code == lex.codeFor("FLOAT")){
                   recur = SimpleExpression();
               } else if (token.code == lex.codeFor("IDENT") || token.code == lex.codeFor("STRCO")) {
                   trace("SimpleExpression", true);
                   trace("Term", true);
                   trace("Factor", true);
                   trace("Variable", true);
                   token=lex.GetNextToken();
                   trace("Variable", false);
                   trace("Factor", false);
                   trace("Term", false);
                   trace("SimpleExpression", false);
               }else{
                   error("Writeln",token.lexeme);
               }
           } if(token.code == lex.codeFor("PARAC")) {
               token = lex.GetNextToken();
               trace("handlePrintln", false);
               trace("Statement", false);
               return recur;
           }
           trace("handlePrintln", false);
           trace("Statement", false);
           return recur;
       }

       

       if(token.code == lex.codeFor("READL")){
           trace("handleReadln", true);
           token = lex.GetNextToken();
           if(token.code == lex.codeFor("PARAO")){
               token = lex.GetNextToken();
               if(token.code == lex.codeFor("IDENT")){
                   trace("Variable", true);
                   token = lex.GetNextToken();
                   trace("Variable", false);
                   if(token.code == lex.codeFor("PARAC")){
                       token = lex.GetNextToken();
                   }else{
                       error("Closed Paranthesis", token.lexeme);
                   }
               }else{
                   error("IDENTIFIER", token.lexeme);
               }
           }else { 
               error("Open Paranthesis", token.lexeme);
           }
           trace("handleReadln", false);
           trace("Statement", false);
           return recur;
       }
            
        error("Statement start", token.lexeme);

        while (!lex.EOF() && token.code != lex.codeFor("END") && token.code != lex.codeFor("SEMIC") && 
               token.code != lex.codeFor("_DOT") && token.code != lex.codeFor("IDENT") && 
               token.code != lex.codeFor("BEGIN") && token.code != lex.codeFor("_IF_") && 
               token.code != lex.codeFor("WHILE") && token.code != lex.codeFor("REPEA") && 
               token.code != lex.codeFor("_FOR") && token.code != lex.codeFor("WRITE") && 
               token.code != lex.codeFor("READL")) {
            token = lex.GetNextToken();
        }
       
       trace("Statement", false);
       return recur;
   }

   //Non-Terminal for SimpleExpression
   private int SimpleExpression() {
   
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("SimpleExpression", true);
       
       if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
           recur = Sign();
       }
       if (token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("NCINT") || token.code==lex.codeFor("IDENT") || token.code==lex.codeFor("PARAO")) {
           recur = Term();
       }
       //HERE you made the mistake.... Check the CFG again and probably write a while loop to parse everything 
       while ((token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) && (!lex.EOF())
               && (!anyErrors)) {
           token=lex.GetNextToken();
           recur = Term();
       } 
       trace("SimpleExpression", false);
       return recur;
   }
   //Non-Terminal for handleAssignment
   private int handleAssignment() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       // have ident already in order to get to here, handle as Variable
       // recur = Variable(); // Variable moves ahead, next token ready
       if (token.code == lex.codeFor("_AS_")) {
           token = lex.GetNextToken();
       } else {
           error(lex.reserveFor("_AS_"), token.lexeme);
       }
       return recur;
   }

   //Non-Terminal for RelExpression

   private int RelExpression() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Relexpression", true);
       
       if(token.code == lex.codeFor("NCFLO")||token.code==lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR") || token.code ==  lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID") || token.code == lex.codeFor("NCINT") || token.code == lex.codeFor("INTEG") || token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("IDENT")){
           recur = SimpleExpression();
           if(token.code == lex.codeFor("EQUAL") || token.code == lex.codeFor("LESST") || token.code == lex.codeFor("GREAT") || token.code == lex.codeFor("LESSE") || token.code == lex.codeFor("GREAE")){
               trace("Relop", true);
               token = lex.GetNextToken();
               trace("Relop", false);
               recur = SimpleExpression();
               }else{
                   error("Relational Operator", token.lexeme);
               }
       }else {
           error("Something in SimpleExpression", token.lexeme);
       }
       trace("Relexpression", false);
       return recur;
   }

   //Non-Terminal for Variable
   private int Variable() {
       int recur = 0;
       if(anyErrors){
           return -1;
       }
       trace("Variable", true);
       if(token.code == lex.codeFor("IDENT")){
           if (symbolList.LookupSymbol(token.lexeme) == -1) {
                error("Declared identifier", token.lexeme);
            }
           token = lex.GetNextToken();
       } else {
           error("IDENT", token.lexeme);
       }
       trace("Variable", false);
       return recur;
   }

   private int Sign() {
       int recur = 0;
       if(anyErrors){
           return -1;
       }
       if(token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")|| token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")){
           token = lex.GetNextToken();
       } else {
           error("ADDIT or SUBTR", token.lexeme);
       }
       return recur;
   }
   
   
   // New SimpleType non-terminal
   private int SimpleType() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Simpletype", true);
       if (token.code == lex.codeFor("INTEG") || token.code == lex.codeFor("FLOAT")
               || token.code == lex.codeFor("STRIN")) {
           token = lex.GetNextToken();
       } else {
           error("Expected INTEGER, FLOAT, or STRING", token.lexeme);
       }
       trace("Simpletype", false);
       return recur;
   }
   
   
   private int Term() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Term", true);
       if (token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("NCINT") || token.code==lex.codeFor("IDENT") || token.code==lex.codeFor("PARAO")) {
           recur = Factor();
           while ((token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")) && (!lex.EOF()) && (!anyErrors)) {
               token = lex.GetNextToken();
               recur = Factor();
           }
       } else{
           error("Term",token.lexeme);
       }
       trace("Term", false);
       return recur;
   }

   private int Factor() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("Factor", true);
       if (token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("NCINT")) {
           trace("UnsignedConstant", true);
           trace("UnsignedNumber", true);
           token = lex.GetNextToken();
           trace("UnsignedNumber", false);
           trace("UnsignedConstant", false);
       } else if (token.code == lex.codeFor("IDENT")) {
           trace("Variable", true);
           token = lex.GetNextToken();
           trace("Variable", false);
       } else if (token.code == lex.codeFor("PARAO")) {
           token = lex.GetNextToken();
           recur = SimpleExpression();
           if (token.code == lex.codeFor("PARAC")) {
               token = lex.GetNextToken();
           } else {
               error("PARAC", token.lexeme);
           }
       } else {
           error("FLOAT OR INTEGER", token.lexeme);
       }
       trace("Factor", false);
       return recur;
   }
   
   // New UnsignedConstant non-terminal
   private int UnsignedConstant() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("UnsignedConstant", true);
       if (token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("NCINT")) {
           recur = UnsignedNumber();
       } else {
           error("FLOAT OR INTEGER", token.lexeme);
       }
       trace("UnsignedConstant", false);
       return recur;
   }
   
   // New UnsignedNumber non-terminal
   private int UnsignedNumber() {
       int recur = 0;
       if (anyErrors) {
           return -1;
       }
       trace("UnsignedNumber", true);
       if (token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("NCINT")) {
           token = lex.GetNextToken();
       } else {
           error("FLOAT OR INTEGER", token.lexeme);
       }
       trace("UnsignedNumber", false);
       return recur;
   }

   /**
    * *************************************************
    */
   /* UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
   // error provides a simple way to print an error statement to standard output
   // and avoid redundancy
   private void error(String wanted, String got) {
       anyErrors = true;
       System.out.println("ERROR: Expected " + wanted + " but found " + got);
   }

   // trace simply RETURNs if traceon is false; otherwise, it prints an
   // ENTERING or EXITING message using the proc string
   private void trace(String proc, boolean enter) {
       String tabs = "";
       if (!traceon) {
           return;
       }
       if (enter) {
           tabs = repeatChar(" ", level);
           System.out.print(tabs);
           System.out.println("--> Entering " + proc);
           level++;
       } else {
           if (level > 0) {
               level--;
           }
           tabs = repeatChar(" ", level);
           System.out.print(tabs);
           System.out.println("<-- Exiting " + proc);
       }
   }

   // repeatChar returns a string containing x repetitions of string s;
   // nice for making a varying indent format
   private String repeatChar(String s, int x) {
       int i;
       String result = "";
       for (i = 1; i <= x; i++) {
           result = result + s;
       }
       return result;
   }
}
