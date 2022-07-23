package org.example;


import java.io.IOException;

public class Exec {
    static {
        System.out.println("hello");
    }
    public static void main(String[] args) throws IOException {
       Runtime runtime =   Runtime.getRuntime();
       runtime.exec("sleep\n10000000");
    }
}
