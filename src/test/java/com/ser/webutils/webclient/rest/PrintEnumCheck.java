package com.ser.webutils.webclient.rest;

import org.junit.Test;

public class PrintEnumCheck {

    @Test
    //init

    //act
    public void printEnumCheck() {
        String company = "yandex".toLowerCase();
        //    EnumCheck.GOOGLE.name = "My_target";
        System.out.println("enumcheck: " + EnumCheck.GOOGLE.name());
        System.out.println("company: " + company);
        System.out.println("equals?: " + company.equals(EnumCheck.GOOGLE.name()));

        switch (company) {
            case "google":
                System.out.println("company is google: " + company);
                break;
            case "yandex":
                System.out.println("company is yandex: " + company);
                break;
            default:
                System.out.println("nothing :(");

        }


    }

    //assert
}
