package com.hyd.sob;

import java.util.Date;

import static org.apache.commons.lang3.time.FastDateFormat.*;

public class FastDateFormatTest {

    public static void main(String[] args) {
        Date now = new Date();
        System.out.println(getDateTimeInstance(FULL, FULL).format(now));
        System.out.println(getDateTimeInstance(FULL, LONG).format(now));
        System.out.println(getDateTimeInstance(FULL, MEDIUM).format(now));
        System.out.println(getDateTimeInstance(FULL, SHORT).format(now));
        System.out.println("-----------------------------------------");
        System.out.println(getDateTimeInstance(LONG, FULL).format(now));
        System.out.println(getDateTimeInstance(LONG, LONG).format(now));
        System.out.println(getDateTimeInstance(LONG, MEDIUM).format(now));
        System.out.println(getDateTimeInstance(LONG, SHORT).format(now));
        System.out.println("-----------------------------------------");
        System.out.println(getDateTimeInstance(MEDIUM, FULL).format(now));
        System.out.println(getDateTimeInstance(MEDIUM, LONG).format(now));
        System.out.println(getDateTimeInstance(MEDIUM, MEDIUM).format(now));  // bingo
        System.out.println(getDateTimeInstance(MEDIUM, SHORT).format(now));
        System.out.println("-----------------------------------------");
        System.out.println(getDateTimeInstance(SHORT, FULL).format(now));
        System.out.println(getDateTimeInstance(SHORT, LONG).format(now));
        System.out.println(getDateTimeInstance(SHORT, MEDIUM).format(now));
        System.out.println(getDateTimeInstance(SHORT, SHORT).format(now));
        System.out.println("-----------------------------------------");
    }
}
