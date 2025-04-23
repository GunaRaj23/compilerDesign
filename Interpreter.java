package ADT;

import java.io.*;
import java.util.*;

public class Interpreter {
    private ReserveTable reserveTable;
    public static final int MAX_VALUE = 16;

    public Interpreter() {
        this.reserveTable = new ReserveTable(MAX_VALUE);
        InitializeReserveTable(reserveTable);
    }

    public static void InitializeReserveTable(ReserveTable optable) {
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("PRINT", 6);
        optable.Add("READ", 7);
        optable.Add("JUMP", 8);
        optable.Add("JZ", 9);
        optable.Add("JP", 10);
        optable.Add("JN", 11);
        optable.Add("JNZ", 12);
        optable.Add("JNP", 13);
        optable.Add("JNN", 14);
        optable.Add("JINDR", 15);
    }

    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        InitSTF(stable);
        InitQTF(qtable);
        return true;
    }

    public static void InitSTF(SymbolTable st) {
        st.AddSymbol("n", SymbolPurpose.VARIABLE, 10);
        st.AddSymbol("i", SymbolPurpose.VARIABLE, 0);
        st.AddSymbol("product", SymbolPurpose.VARIABLE, 0);
        st.AddSymbol("1", SymbolPurpose.CONSTANT, 1);
        st.AddSymbol("$temp", SymbolPurpose.VARIABLE, 0);
    }

    public void InitQTF(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); 
        qt.AddQuad(5, 3, 0, 1); 
        qt.AddQuad(3, 1, 0, 4); 
        qt.AddQuad(10, 4, 0, 7); 
        qt.AddQuad(2, 2, 1, 2); 
        qt.AddQuad(4, 1, 3, 1); 
        qt.AddQuad(8, 0, 0, 2); 
        qt.AddQuad(6, 2, 0, 0); 
        qt.AddQuad(0, 0, 0, 0); 
    }
    
    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
        InitSTS(stable);
        InitQTS(qtable);
        return true;
    }
    
    public static void InitSTS(SymbolTable st) {
        st.AddSymbol("n", SymbolPurpose.VARIABLE, 10);
        st.AddSymbol("i", SymbolPurpose.VARIABLE, 0);
        st.AddSymbol("sum", SymbolPurpose.VARIABLE, 0);
        st.AddSymbol("1", SymbolPurpose.CONSTANT, 1);
        st.AddSymbol("$temp", SymbolPurpose.VARIABLE, 0);
    }
 
    public static void InitQTS(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); 
        qt.AddQuad(5, 3, 0, 1); 
        qt.AddQuad(3, 1, 0, 4); 
        qt.AddQuad(10, 4, 0, 7);
        qt.AddQuad(4, 2, 1, 2);
        qt.AddQuad(4, 1, 3, 1); 
        qt.AddQuad(8, 0, 0, 2);
        qt.AddQuad(6, 2, 0, 0);
        qt.AddQuad(0, 0, 0, 0); 
    }

    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            int PC = 0; 

            while (PC < Q.NextQuad()) {
                int[] quad = Q.GetQuad(PC);
                int opcode = quad[0];
                int op1 = quad[1];
                int op2 = quad[2];
                int op3 = quad[3];

                
                if (TraceOn) {
                    String traceString = makeTraceString(PC, opcode, op1, op2, op3, S);
                    System.out.println(traceString);
                    writer.write(traceString);
                    writer.newLine();
                }

                
                switch (opcode) {
                    case STOP_OPCODE: 
                        System.out.println("Execution terminated by program stop.");
                        return;
                    case DIV_OPCODE: 
                        S.SetValue(op3, S.GetInteger(op1) / S.GetInteger(op2));
                        break;
                    case MUL_OPCODE: 
                        S.SetValue(op3, S.GetInteger(op1) * S.GetInteger(op2));
                        break;
                    case SUB_OPCODE: 
                        S.SetValue(op3, S.GetInteger(op1) - S.GetInteger(op2));
                        break;
                    case ADD_OPCODE: 
                        S.SetValue(op3, S.GetInteger(op1) + S.GetInteger(op2));
                        break;
                    case MOV_OPCODE: 
                        S.SetValue(op3, S.GetInteger(op1));
                        break;
                    case PRINT_OPCODE: 
                        writer.write(S.GetName(op1));
                        writer.newLine();
                        System.out.println(S.GetInteger(op1));
                        break;
                    case READ_OPCODE: 
                        System.out.print("Enter value for " + S.GetName(op3) + ": ");
                        Scanner scanner = new Scanner(System.in);
                        int readValue = scanner.nextInt();
                        S.SetValue(op3, readValue);
                        break;
                    case JMP_OPCODE: 
                        PC = op3;
                        continue;
                    case JZ_OPCODE:
                        if (S.GetInteger(op1) == 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JP_OPCODE: 
                        if (S.GetInteger(op1) > 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JN_OPCODE: 
                        if (S.GetInteger(op1) < 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JNZ_OPCODE:
                        if (S.GetInteger(op1) != 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JNP_OPCODE: 
                        if (S.GetInteger(op1) >= 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JNN_OPCODE: 
                        if (S.GetInteger(op1) <= 0) {
                            PC = op3;
                            continue;
                        } else {
                            break;
                        }
                        
                    case JINDR_OPCODE: 
                        break;
                    default:
                        System.err.println("Invalid opcode: " + opcode);
                        break;
                }

                PC++; 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String makeTraceString(int pc, int opcode, int op1, int op2, int op3, SymbolTable S) {
        String mnemonic = getMnemonic(opcode);
        String operand1 = S.GetName(op1);
        String operand2 = S.GetName(op2);
        String operand3 = (opcode >= 8 && opcode <= 14) ? Integer.toString(op3) : S.GetName(op3);
        return String.format("PC = %04d: %s %d <%s>, %d <%s>, %s", pc, mnemonic, opcode, operand1, op1, operand2, operand3);
    }
    
    private String getMnemonic(int opcode) {
        switch (opcode) {
            case 0: return "STOP";
            case 1: return "DIV";
            case 2: return "MUL";
            case 3: return "SUB";
            case 4: return "ADD";
            case 5: return "MOV";
            case 6: return "PRINT";
            case 7: return "READ";
            case 8: return "JMP";
            case 9: return "JZ";
            case 10: return "JP";
            case 11: return "JN";
            case 12: return "JNZ";
            case 13: return "JNP";
            case 14: return "JNN";
            case 15: return "JINDR";
            default: return "UNKNOWN";
        }
    }
    public static final int STOP_OPCODE = 0;
    public static final int DIV_OPCODE = 1;
    public static final int MUL_OPCODE = 2;
    public static final int SUB_OPCODE = 3;
    public static final int ADD_OPCODE = 4;
    public static final int MOV_OPCODE = 5;
    public static final int PRINT_OPCODE = 6;
    public static final int READ_OPCODE = 7;
    public static final int JMP_OPCODE = 8;
    public static final int JZ_OPCODE = 9;
    public static final int JP_OPCODE = 10;
    public static final int JN_OPCODE = 11;
    public static final int JNZ_OPCODE = 12;
    public static final int JNP_OPCODE = 13;
    public static final int JNN_OPCODE = 14;
    public static final int JINDR_OPCODE = 15;
}