package com.ser.service.date;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimeStampDate {

    @Test
    public void getNow() {

        Timestamp localDate = Timestamp.valueOf(LocalDateTime.now());
        Timestamp nowDate = Timestamp.valueOf(LocalDateTime.now());


        System.out.println(nowDate.after(localDate));


        System.out.println(localDate + "   -    " + nowDate);

    }
}
