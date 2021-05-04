package com.wiyixiao.lzone.bean;

import android.content.ContentValues;

public class DeviceInfoBean {

    //对应数据库列标题
    public static final String _IP = "ip";
    public static final String _PORT = "port";
    public static final String _TYPE = "type";
    public static final String _AUTO = "auto";

    private String device_ip;
    private String device_port;
    private int device_type;
    private boolean auto;   //是否定时发送

    private int imageId;

    public DeviceInfoBean() {
    }

    public DeviceInfoBean(String device_ip, String device_port, int device_type, boolean auto, int imageId) {
        this.device_ip = device_ip;
        this.device_port = device_port;
        this.device_type = device_type;
        this.auto = auto;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getDevice_port() {
        return device_port;
    }

    public void setDevice_port(String device_port) {
        this.device_port = device_port;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_IP, device_ip);
        contentValues.put(_PORT, device_port);
        contentValues.put(_TYPE, device_type);
        contentValues.put(_AUTO, auto);
        return contentValues;
    }

}
