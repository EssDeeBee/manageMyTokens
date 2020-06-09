package com.ser.service;

import lombok.SneakyThrows;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

public class html {


    @Test
    @SneakyThrows
    public void getTextFromFile(){
        File file = new File("src/main/resources/show_page.html");
       try ( Scanner scanner = new Scanner(file);){
           StringBuilder stringBuilder = new StringBuilder();

           while (scanner.hasNext()){
               stringBuilder.append(scanner.nextLine());
           }
           System.out.println(stringBuilder.toString());
       }
    }
}
