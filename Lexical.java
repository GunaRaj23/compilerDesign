/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ADT;

import java.io.*;

/**
 *
 * @author gunar
 */
public class Lexical {
     private File file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String line;
    private int linePos;
    private SymbolTable saveSymbols;
    private boolean EOF;
    private boolean echo;
    private boolean printToken;
    private int lineCount;
    private boolean needLine;
    private final int sizeReserveTable = 50;
    private ReserveTable reserveWords = new ReserveTable(sizeReserveTable);
    private ReserveTable mnemonics = new ReserveTable(sizeReserveTable);
    private char currCh;

    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;
        echo = echoOn;
        lineCount = 0;
        line = "";
        needLine = true;
        printToken = false;
        linePos = -1;
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        try {
            file = new File(filename);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }

    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }

    public boolean EOF() {
        return EOF;
    }

    public void setPrintToken(boolean on) {
        printToken = on;
    }

    private void initReserveWords(ReserveTable reserveWords) {
        // Initialize reserve words table
        // Example:
        // reserveWords.Add("BEGIN", 11);
        reserveWords.Add("GOTO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4);
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7);
        reserveWords.Add("OF", 8);
        reserveWords.Add("WRITELN", 9);
        reserveWords.Add("READLN", 10);
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("WHILE", 14);
        reserveWords.Add("PROGRAM", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("CASE", 19);
        reserveWords.Add("FLOAT", 20);
        reserveWords.Add("REAL", 21);
        reserveWords.Add("STRING", 22);
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
        reserveWords.Add("IDENTIFIER",50);
    	reserveWords.Add("NCINT", 51);
    	reserveWords.Add("NCFLO", 52);
    	reserveWords.Add("STRCO", 53);
    }

    private void initMnemonics(ReserveTable mnemonics) {
        // Initialize token mnemonics table
    	String[] reserves = {"GOTO", "INTEGER", "_TO_", "_DO_", "_IF_", "THEN", "ELSE", "_FOR", "_OF_", "WRITELN", "READLN", "BEGIN", "END", "_VAR", "WHILE", "PROGRAM", "LABEL", "REPEAT", "UNTIL", "CASE", "FLOAT", "REAL", "STRING"};
        for (int i = 0; i < reserves.length; i++) {
            reserves[i] = reserves[i].substring(0, Math.min(reserves[i].length(), 5));
            mnemonics.Add(reserves[i],i);
        }
        mnemonics.Add("DIVID", 30);
        mnemonics.Add("MULTI", 31);
        mnemonics.Add("ADDIT", 32);
        mnemonics.Add("SUBTR", 33);
        mnemonics.Add("PARAO", 34);
        mnemonics.Add("PARAC", 35);
        mnemonics.Add("SEMIC", 36);
        mnemonics.Add("_AS_", 37);
        mnemonics.Add("GREAT", 38);
        mnemonics.Add("LESST", 39);
        mnemonics.Add("GREAE", 40);
        mnemonics.Add("LESSE", 41);
        mnemonics.Add("EQUAL", 42);
        mnemonics.Add("NEQUA", 43);
        mnemonics.Add("COMMA", 44);
        mnemonics.Add("BRACO", 45);
        mnemonics.Add("BRACC", 46);
        mnemonics.Add("COLON", 47);
        mnemonics.Add("_DOT", 48);
        mnemonics.Add("IDENT", 50);
        mnemonics.Add("NCINT", 51);
    	mnemonics.Add("NCFLO", 52);
    	mnemonics.Add("STRCO", 53);
    }

    private void consoleShowError(String message) {
        System.out.println("**** ERROR FOUND: " + message);
    }

    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }

    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }

    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' ';
        } else {
            if ((linePos + 1) < line.length()) {
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    private void GetNextLine() {
        try {
            line = bufferedReader.readLine();
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {
            EOF = true;
        }
        linePos = -1;
        needLine = false;
    }

    public char GetNextChar() {
        char result;
        if (needLine) {
            GetNextLine();
        }
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            if ((linePos < line.length() - 1)) {
                linePos++;
                result = line.charAt(linePos);
            } else {
                result = '\n';
                needLine = true;
            }
        }
        return result;
    }

    public char skipComment(char curr) {
        // Implement skipping comments
    	final char commentStart_1 = '{';
    	final char commentEnd_1 = '}';
    	final char commentStart_2 = '(';
    	final char commentPairChar = '*';
    	final char commentEnd_2 = ')';
    	if (curr == commentStart_1) { // Check for the { style comment
            curr = GetNextChar();
            while ((curr != commentEnd_1) && (!EOF)) {
                if (curr == '\n') {
                    lineCount++;
//                    if (echo) {
//                        System.out.println(String.format("%04d", lineCount) + " " + line);
//                    }
                }
                curr = GetNextChar();
            }
            if (EOF) {
                consoleShowError("Unterminated comment found.");
            } else {
                curr = GetNextChar(); 
            }
        } else if (curr == commentStart_2 && PeekNextChar() == commentPairChar) { // Check for the (* style comment
            curr = GetNextChar(); // skip the second character of the comment start delimiter
            curr = GetNextChar(); // move to the first character inside the comment
            while (!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2)) && !EOF) {
                if (curr == '\n') {
                    lineCount++;
                    if (echo) {
                        System.out.println(String.format("%04d", lineCount) + " " + line);
                    }
                }
                curr = GetNextChar();
            }
            if (EOF) {
                consoleShowError("Unterminated comment found.");
            } else {
                curr = GetNextChar();
                curr = GetNextChar(); 
            }
        }
        return (curr);
    }

    public char skipWhiteSpace() {
        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }


    private boolean isStringStart(char ch) {
        return ch == '"';
    }

    private token getIdentifier() {
        StringBuilder identifier = new StringBuilder();
        while ((isLetter(currCh) || isDigit(currCh) || currCh == '_' || currCh == '$') && identifier.length() < 20) {
            identifier.append(currCh);
            currCh = GetNextChar();
        }
        // Check if the identifier exceeds 20 characters
        if (identifier.length() == 20 && currCh != '\n') {
            consoleShowError("Identifier exceeds 20 characters and will be truncated");
            while (isLetter(currCh) || isDigit(currCh) || currCh == '_' || currCh == '$') {
                currCh = GetNextChar();
            }
        }
        // Truncate the identifier if it's longer than 20 characters
        if (identifier.length() > 20) {
            identifier.setLength(20);
        }
        // Create token object
        token result = new token();
        result.lexeme = identifier.toString();

        // Check if the lexeme is a reserved word
        String lexemeUpper = result.lexeme.toUpperCase();
        int reserveCode = reserveWords.LookupName(lexemeUpper);
        if (reserveCode != -1) {
            // It's a reserved word; get the corresponding mnemonic
            result.code = reserveCode;
            result.mnemonic = mnemonics.LookupCode(reserveCode);
        } else {
            // It's not a reserved word, so assign the IDENTIFIER token code
            result.code = mnemonics.LookupName("IDENT");
            result.mnemonic = "IDENT";
            saveSymbols.AddSymbol(result.lexeme, SymbolPurpose.VARIABLE, 0);
        }

        return result;
    }

    private token getNumber() {
        // Implement getting number token
    	StringBuilder number = new StringBuilder();
        boolean isFloat = false;
        boolean hasExponent = false;
        // Read the integer part
        while (isDigit(currCh) && number.length() < 20) {
            number.append(currCh);
            currCh = GetNextChar();
        }
        // Check if there is a decimal point
        if (currCh == '.') {
            isFloat = true;
            number.append(currCh);
            currCh = GetNextChar();
            // Read the fractional part
            while (isDigit(currCh) && number.length() < 20) {
                number.append(currCh);
                currCh = GetNextChar();
            }
        }
        // Check for exponential notation
        if (currCh == 'E' && isFloat) {
            hasExponent = true;
            number.append(currCh);
            currCh = GetNextChar();
            // Check for optional sign
            if (currCh == '+' || currCh == '-') {
                number.append(currCh);
                currCh = GetNextChar();
            }
            
            if (!isDigit(currCh)) {
                  consoleShowError("Invalid scientific notation detected: " + number + " (Ignoring)");
                    while (!isWhitespace(currCh) && currCh != '\n' && !EOF) {
                        currCh = GetNextChar(); // Skip the rest of the term
                    }
                    return GetNextToken();
            } else {
                hasExponent = true;
                while (isDigit(currCh) && number.length() < 20) {
                    number.append(currCh);
                    currCh = GetNextChar();
                }
            }
        
        }
        // Create token object
        token result = new token();
        result.lexeme = number.toString();
        // Assign token code based on whether it's a float or an integer
        result.code = isFloat ? mnemonics.LookupName("NCFLO") : mnemonics.LookupName("NCINT");
        result.mnemonic = isFloat ? "NCFLO" : "NCINT";
        if (result.mnemonic == "NCFLO" || hasExponent) {
            saveSymbols.AddSymbol(result.lexeme, SymbolPurpose.CONSTANT, Double.parseDouble(result.lexeme));
        } else {
            if (result.lexeme.length() > 6) {
                consoleShowError("Integer length > 6, truncated: " + result.lexeme);
                result.lexeme = result.lexeme.substring(0, 6);
            }
            saveSymbols.AddSymbol(result.lexeme, SymbolPurpose.CONSTANT, Integer.parseInt(result.lexeme));
        }
//        saveSymbols.AddSymbol(result.lexeme, SymbolPurpose.VARIABLE, result.lexeme);
        return result;
    }

    private token getString() {
//        // Implement getting string token
//        return dummyGet();
    	StringBuilder stringLiteral = new StringBuilder();
        // Skip the starting double-quote
        currCh = GetNextChar();
        // Read characters until the terminating double-quote or the end of line
        while (currCh != '"' && currCh != '\n' && !EOF) {
            stringLiteral.append(currCh);
            currCh = GetNextChar();
        }
        // Check if the string ended prematurely
        if (currCh != '"') {
            consoleShowError("Unterminated string found.");
            while (!isWhitespace(currCh) && currCh != '\n' && !EOF) {
            currCh = GetNextChar();
            }
        return GetNextToken();
        }
        // Create token object
        token result = new token();
        result.lexeme = stringLiteral.toString();
        result.code = reserveWords.LookupName("STRCO");
        result.mnemonic = "STRCO";
        saveSymbols.AddSymbol(result.lexeme, SymbolPurpose.VARIABLE, result.lexeme);
        return result;
    }

    private token getOtherToken() {
    	char ch = currCh;
        token result = new token();
        switch (ch) {
            case '+':
                result.lexeme = "+";
                result.code = mnemonics.LookupName("ADDIT");
                result.mnemonic = "ADDIT";
                break;
            case '-':
                result.lexeme = "-";
                result.code = mnemonics.LookupName("SUBTR");
                result.mnemonic = "SUBTR";
                break;
            case '(':
                result.lexeme = "(";
                result.code = mnemonics.LookupName("PARAO");
                result.mnemonic = "PARAO";
                break;
            case ')':
                result.lexeme = ")";
                result.code = mnemonics.LookupName("PARAC");
                result.mnemonic = "PARAC";
                break;
            case ';':
                result.lexeme = ";";
                result.code = mnemonics.LookupName("SEMIC");
                result.mnemonic = "SEMIC";
                break;
            case ':':
                if (PeekNextChar() == '=') {
                    result.lexeme = ":=";
                    result.code = reserveWords.LookupName(":=");
                    result.mnemonic = ":=";
                    currCh = GetNextChar(); // Move past '='
                } else {
                    result.lexeme = ":";
                    result.code    = mnemonics.LookupName("COLON");
                    result.mnemonic = "COLON";
                }
                break;
            case '>':
                if (PeekNextChar() == '=') {
                    result.lexeme = ">=";
                    result.code = reserveWords.LookupName(">=");
                    result.mnemonic = mnemonics.LookupCode(result.code);
//                    result.mnemonic = ">=";
                    currCh = GetNextChar(); // Move past '='
                } else {
                    result.lexeme = ">";
                    result.code = mnemonics.LookupName("GREAT");
                    result.mnemonic = "GREAT";
                }
                break;
            case '<':
                if (PeekNextChar() == '=') {
                    result.lexeme = "<=";
                    result.code = reserveWords.LookupName("<=");
                    result.mnemonic = mnemonics.LookupCode(result.code);
//                    result.code = mnemonics.LookupName("<=");
//                    result.mnemonic = "<=";
                    currCh = GetNextChar(); // Move past '='
                } else if (PeekNextChar() == '>') {
                    result.lexeme = "<>";
                    result.code = mnemonics.LookupName("<>");
                    result.mnemonic = "<>";
                    currCh = GetNextChar(); // Move past '>'
                } else {
                    result.lexeme = "<";
                    result.code = mnemonics.LookupName("LESST");
                    result.mnemonic = "LESST";
                }
                break;
            case '=':
                result.lexeme = "=";
                result.code = mnemonics.LookupName("EQUAL");
                result.mnemonic = "EQUAL";
                break;
            case ',':
                result.lexeme = ",";
                result.code = mnemonics.LookupName("COMMA");
                result.mnemonic = "COMMA";
                break;
            case '[':
                result.lexeme = "[";
                result.code = mnemonics.LookupName("BRACO");
                result.mnemonic = "BRACO";
                break;
            case ']':
                result.lexeme = "]";
                result.code = mnemonics.LookupName("BRACC");
                result.mnemonic = "BRACC";
                break;
            case '.':
                result.lexeme = ".";
                result.code = mnemonics.LookupName("_DOT");
                result.mnemonic = "_DOT";
                break;
            case '*':
                result.lexeme = "*";
                result.code = mnemonics.LookupName("MULTI");
                result.mnemonic = "MULTI";
                break;
            case '/':
                result.lexeme = "/";
                result.code = mnemonics.LookupName("DIVID");
                result.mnemonic = "DIVID";
                break;
            default:
                // For any other character not defined elsewhere
                result.lexeme = String.valueOf(ch);
                result.code = mnemonics.LookupName("OTHER");
                result.mnemonic = "OTHER";
                break;
        }
        currCh = GetNextChar(); // Move to the next character
        return result;
    }

    public token GetNextToken() {
        token result = new token();
        currCh = skipWhiteSpace();
        if (isLetter(currCh)) {
            result = getIdentifier();
        } else if (isDigit(currCh)) {
            result = getNumber();
        } else if (isStringStart(currCh)) {
            result = getString();
        } else {
            result = getOtherToken();
        }
        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
        if (result != null) {
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;
    }

//    private token dummyGet() {
//        token result = new token();
//        result.lexeme = "" + currCh;
//        currCh = GetNextChar();
//        result.code = 0;
//        result.mnemonic = "DUMY";
//        return result;
//    }

    public class token {
        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }
}
