/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ADT;

/**
 *
 * @author gunar
 */
public class Main {
    public static void main(String[] args) {
       String filePath = args[0];
        boolean traceon = true;
        System.out.println("Guna Raj Nedunuru, 1018, CS4100/5100, SPRING 2025");
        System.out.println("INPUT FILE TO PROCESS IS: "+filePath);
    
        Syntactic3A parser = new Syntactic3A(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}
