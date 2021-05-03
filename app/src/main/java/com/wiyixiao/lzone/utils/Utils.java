package com.wiyixiao.lzone.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Author:Think
 * Time:2021/4/18 16:54
 * Description:This is Utils
 */
public class Utils {

    public static long getCurrTimeMillis(){
        return System.currentTimeMillis();
    }

    public static String getSysTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        return formatter.format(getCurrTimeMillis());
    }

    public static String getLocalLanguage(){
        return Locale.getDefault().getLanguage().toUpperCase();
    }

}
