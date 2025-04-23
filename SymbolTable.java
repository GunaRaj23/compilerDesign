/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ADT;
import java.io.*;
import java.util.*;
/**
 *
 * @author gunar
 */

public class SymbolTable {
    private int maxSize;
    private List<SymbolEntry> table;

    public SymbolTable(int maxSize) {
        this.maxSize = maxSize;
        this.table = new ArrayList<>(maxSize);
    }

    public int AddSymbol(String symbol, SymbolPurpose purpose, int value) {
        int index = LookupSymbol(symbol);
        if (index != -1) return index;
        
        if (table.size() >= maxSize) return -1;
        
        SymbolEntry entry = new SymbolEntry(symbol, purpose, DataType.INTEGERTYPE);
        entry.integerValue = value;
        table.add(entry);
        return table.size() - 1;
    }

    public int AddSymbol(String symbol, SymbolPurpose purpose, double value) {
        int index = LookupSymbol(symbol);
        if (index != -1) return index;
        
        if (table.size() >= maxSize) return -1;
        
        SymbolEntry entry = new SymbolEntry(symbol, purpose, DataType.FLOATTYPE);
        entry.floatValue = value;
        table.add(entry);
        return table.size() - 1;
    }

    public int AddSymbol(String symbol, SymbolPurpose purpose, String value) {
        int index = LookupSymbol(symbol);
        if (index != -1) return index;
        
        if (table.size() >= maxSize) return -1;
        
        SymbolEntry entry = new SymbolEntry(symbol, purpose, DataType.STRINGTYPE);
        entry.stringValue = value;
        table.add(entry);
        return table.size() - 1;
    }

    public int LookupSymbol(String symbol) {
        for (int i = 0; i < table.size(); i++) {
            if (table.get(i).name.equalsIgnoreCase(symbol)) {
                return i;
            } else {
                continue;
            }
        }
        return -1;
    }

    public String GetName(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).name;
        } else {
            return "";
        }
    }

    public SymbolPurpose GetPurpose(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).purpose;
        } else {
            return null;
        }
    }

    public void SetPurpose(int index, SymbolPurpose purpose) {
        if (index >= 0 && index < table.size()) {
            table.get(index).purpose = purpose;
        }
    }

    public DataType GetDataType(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).dataType;
        } else {
            return null;
        } 
    }

    public boolean isInteger(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).dataType == DataType.INTEGERTYPE;
        } else {
            return false;
        } 
    }

    public boolean isFloat(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).dataType == DataType.FLOATTYPE;
        }
        return false;
    }

    public boolean isString(int index) {
        if (index >= 0 && index < table.size()) {
            return table.get(index).dataType == DataType.STRINGTYPE;
        }
        return false;
    }

    public String GetString(int index) {
        if (!isValidIndex(index)) return "";
        SymbolEntry entry = table.get(index);
        
        switch (entry.dataType) {
            case STRINGTYPE:
                return entry.stringValue;
            case INTEGERTYPE:
                return String.valueOf(entry.integerValue);
            case FLOATTYPE:
                return String.valueOf(entry.floatValue);
            default:
                return "";
        }
    }

    public int GetInteger(int index) {
        if (isValidIndex(index) && isInteger(index)) {
            return table.get(index).integerValue;
        }
        return 0;
    }

    public double GetFloat(int index) {
        if (isValidIndex(index) && isFloat(index)) {
            return table.get(index).floatValue;
        }
        return 0.0;
    }

    public void SetValue(int index, int value) {
        if (isValidIndex(index)) {
            SymbolEntry entry = table.get(index);
            entry.dataType = DataType.INTEGERTYPE;
            entry.integerValue = value;
        }
    }

    public void SetValue(int index, double value) {
        if (isValidIndex(index)) {
            SymbolEntry entry = table.get(index);
            entry.dataType = DataType.FLOATTYPE;
            entry.floatValue = value;
        }
    }

    public void SetValue(int index, String value) {
        if (isValidIndex(index)) {
            SymbolEntry entry = table.get(index);
            entry.dataType = DataType.STRINGTYPE;
            entry.stringValue = value;
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < table.size();
    }

    public void PrintSymbolTable(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.printf("%-6s %-20s %-10s %-10s %s%n", 
                         "Index", "Name", "Purpose", "Type", "Value");
            
            for (int i = 0; i < table.size(); i++) {
                SymbolEntry entry = table.get(i);
                String value = entry.getValue();
                writer.printf("%-6d %-20s %-10s %-10s %s%n",
                            i, entry.name, entry.purpose, entry.dataType, value);
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static class SymbolEntry {
        private String name;
        private SymbolPurpose purpose;
        private DataType dataType;
        private int integerValue;
        private double floatValue;
        private String stringValue;

        public SymbolEntry(String name, SymbolPurpose purpose, DataType dataType) {
            this.name = name;
            this.purpose = purpose;
            this.dataType = dataType;
        }

        public String getValue() {
            switch (dataType) {
                case INTEGERTYPE:
                    return String.valueOf(integerValue);
                case FLOATTYPE:
                    return String.valueOf(floatValue);
                case STRINGTYPE:
                    return stringValue;
                default:
                    return "";
            }
        }
    }
}             
