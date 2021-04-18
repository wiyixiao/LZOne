package com.wiyixiao.lzone.bean;

public class KeyInfoBean {

    private String name;
    private String txt_click;
    private String txt_lclick;
    private String txt_release;
    private String time;
    private int type;   //0: Ascii, 1: Hex
    private int index;

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
}
