package com.wiyixiao.lzone.bean;

import android.content.ContentValues;

public class KeyInfoBean {

    public static final String _IP      = "ip";
    public static final String _NAME    = "name";
    public static final String _CLICK   = "click_txt";
    public static final String _LCLICK  = "lclick_txt";
    public static final String _RELEASE = "release_txt";
    public static final String _TYPE    = "type";
    public static final String _TIME    = "time";
    public static final String _IDX     = "idx";

    private String ip;
    private String name;
    private String txt_click;
    private String txt_lclick;
    private String txt_release;
    private String time;
    private int type;   //0: Ascii, 1: Hex
    private int index;

    //记录值，避免HEX模式下每次发送相同值都要重新获取一下
    private String lastClickHexCmd;
    private byte[] hexClickCmd;
    private String lastLClickHexCmd;
    private byte[] hexLClickCmd;
    private String lastReleaseHexCmd;
    private byte[] hexReleaseCmd;

    public String getLastClickHexCmd() {
        return lastClickHexCmd == null ? "" : lastClickHexCmd;
    }

    public void setLastClickHexCmd(String lastClickHexCmd) {
        this.lastClickHexCmd = lastClickHexCmd;
    }

    public byte[] getHexClickCmd() {
        return hexClickCmd;
    }

    public void setHexClickCmd(byte[] hexClickCmd) {
        this.hexClickCmd = hexClickCmd;
    }

    public String getLastLClickHexCmd() {
        return lastLClickHexCmd == null ? "" : lastLClickHexCmd;
    }

    public void setLastLClickHexCmd(String lastLClickHexCmd) {
        this.lastLClickHexCmd = lastLClickHexCmd;
    }

    public byte[] getHexLClickCmd() {
        return hexLClickCmd;
    }

    public void setHexLClickCmd(byte[] hexLClickCmd) {
        this.hexLClickCmd = hexLClickCmd;
    }

    public String getLastReleaseHexCmd() {
        return lastReleaseHexCmd == null ? "" : lastReleaseHexCmd;
    }

    public void setLastReleaseHexCmd(String lastReleaseHexCmd) {
        this.lastReleaseHexCmd = lastReleaseHexCmd;
    }

    public byte[] getHexReleaseCmd() {
        return hexReleaseCmd;
    }

    public void setHexReleaseCmd(byte[] hexReleaseCmd) {
        this.hexReleaseCmd = hexReleaseCmd;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getTxt_click() {
        return txt_click == null ? "" : txt_click;
    }

    public void setTxt_click(String txt_click) {
        this.txt_click = txt_click == null ? "" : txt_click;
    }

    public String getTxt_lclick() {
        return txt_lclick == null ? "" : txt_lclick;
    }

    public void setTxt_lclick(String txt_lclick) {
        this.txt_lclick = txt_lclick == null ? "" : txt_lclick;
    }

    public String getTxt_release() {
        return txt_release == null ? "" : txt_release;
    }

    public void setTxt_release(String txt_release) {
        this.txt_release = txt_release == null ? "" : txt_release;
    }

    public String getTime() {
        return (time == null || Integer.parseInt(time) < 50) ? "50" : time;
    }

    public void setTime(String time) {
        this.time = (time == null || "".equals(time) || Integer.parseInt(time) < 50) ? "50" : time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIp() {
        return ip == null ? "" : ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_IP, ip);
        contentValues.put(_NAME, name);
        contentValues.put(_CLICK, txt_click);
        contentValues.put(_LCLICK, txt_lclick);
        contentValues.put(_RELEASE, txt_release);
        contentValues.put(_TYPE, type);
        contentValues.put(_TIME, time);
        contentValues.put(_IDX, index);

        return contentValues;
    }
}
