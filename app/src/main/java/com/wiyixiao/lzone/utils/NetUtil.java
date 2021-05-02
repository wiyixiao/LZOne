package com.wiyixiao.lzone.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Author:Think
 * Time:2021/5/2 19:19
 * Description:This is NetUtil
 */
public class NetUtil {

    public  static  boolean isIpString(String arg0){
        boolean is=true;
        try {
            InetAddress ia=InetAddress.getByName("arg0");
        } catch (UnknownHostException e) {
            is=false;
        }
        return is;
    }

    public static boolean isIp(String str) {
        Pattern pattern = Pattern
                .compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]"
                        + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
        return pattern.matcher(str).matches();
    }

}
