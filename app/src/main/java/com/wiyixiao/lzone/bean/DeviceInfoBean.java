package com.wiyixiao.lzone.bean;

public class DeviceInfoBean {

    private String device_ip;
    private String device_port;
    private int device_type;
    private boolean auto;   //是否定时发送

    private int imageId;

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

}
