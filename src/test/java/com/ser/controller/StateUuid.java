package com.ser.controller;

import org.junit.Test;

import java.util.UUID;

public class StateUuid {

    @Test
    public void createState() {
        String uuid = UUID.randomUUID().toString();

        System.out.println(uuid);
    }

}
