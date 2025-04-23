/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ADT;

import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author gunar
 */
public class ReserveTable {
    
    //declaring the type of names, codesForRespectiveNames and sizeOfTheReserveTable.
    private String[] names;
    private int[] codesForRespectiveNames;
    private int sizeOfTheReserveTable;
                  
                  
    //Initializing ReserveTable.
    public ReserveTable(int needed_size) {
        codesForRespectiveNames=new int[needed_size];
        names=new String[needed_size];
        sizeOfTheReserveTable=0;
    } 
                  
                  
    //this method adds the new row to thetable with new values and names and finally returns the index of that row.
    public int Add(String name, int code) {
        codesForRespectiveNames[sizeOfTheReserveTable]=code;
        names[sizeOfTheReserveTable]=name;
        sizeOfTheReserveTable++;
        return sizeOfTheReserveTable-1;
    } 

    //this method returns the code associated to the name, if the name is not present in the table it returns -1.
    public int LookupName(String name) {
        for(int i =0;i<sizeOfTheReserveTable;i++) {
            if(names[i].compareToIgnoreCase(name)==0) {
                return codesForRespectiveNames[i];
            } else {
                continue;
            }
        }
        return -1;//for non existing name.
    } 


    //this method return the name associated to the code, if the code is not present in the table it returns "".
    public String LookupCode(int code) {
        for(int i=0;i<sizeOfTheReserveTable;i++) {
            if(codesForRespectiveNames[i]==code) {
                return names[i];
            } else {
                continue;
            }
        }
        return ""; //empty string for non existing code.
    } 

    //prints the generated table to the text file.
    public void PrintReserveTable(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
          writer.write("Index\tName\tCode");
          writer.newLine();
          
          int i = 0;
          while (i < sizeOfTheReserveTable) {
            String code = pad(pad(Integer.toString(codesForRespectiveNames[i]),3,true),8,false);
            String name = pad(pad(names[i],3,true),8,false);
            writer.write(String.format("%d\t%s\t%s%n", i + 1, name, code));
            i++;
          }
          
        } catch (IOException e) {
            e.printStackTrace();
        }	        		 
    } 


    //used to put spaces within the table while writing to the file.
    public String pad(String input, int length, boolean lefteElement) {
        StringBuilder generator = new StringBuilder(input);

        while (generator.length() < length) {
            if (lefteElement) {
                generator.insert(0, " ");
            } else {
                generator.append(" ");
            }
        }

        return generator.toString();
    }
}
