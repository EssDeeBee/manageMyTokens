package com.ser.service.list;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckingList {

    @Test
    public void findAndAddAndDelete() {

        List<String> testList = Collections.synchronizedList(new ArrayList<>());

        testList.add("Test!");

        String test = "Test!";
        System.out.println("Contains? : " + testList.contains(test));

        System.out.println(testList);

        testList.remove(test);

        System.out.println("Contains? : " + testList.contains(test));

        System.out.println(testList);

    }
}
