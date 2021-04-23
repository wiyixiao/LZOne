package com.wiyixiao.lzone.data;

import android.graphics.Color;

/**
 * Author:Think
 * Time:2021/2/3 10:01
 * Description:This is Vars
 */
public class Vars {

    public interface IntentType{
        int CONTROL = 0;
        int ABOUT = 1;
    }

    public interface  RevDataType{
        int ASCII = 0;
        int HEX = 1;
    }

    public interface MsgType{
        int REV = 0;
        int SEND = 1;
        int INFO = 2;

        int COLOR[] = {
                Color.RED,
                Color.BLACK,
                Color.BLUE
        };
    }

    public interface StopCharType{
        int RN      = 0;
        int N       = 1;
        int CUSTOM  = 2;
    }

    public interface StopCharVal{
        String RN = "0D0A";
        String N  = "0A";
    }

}
