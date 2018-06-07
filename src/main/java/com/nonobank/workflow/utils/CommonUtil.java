package com.nonobank.workflow.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String Time2String(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    }


}
