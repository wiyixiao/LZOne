package com.wiyixiao.lzone.bean;

public class KeyInfoBean {

    private String name;
    private String txt_click;
    private String txt_lclick;
    private String txt_release;
    private String time;
    private int type;   //0: Str, 1: Hex

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTxt_click() {
        return txt_click;
    }

    public void setTxt_click(String txt_click) {
        this.txt_click = txt_click;
    }

    public String getTxt_lclick() {
        return txt_lclick;
    }

    public void setTxt_lclick(String txt_lclick) {
        this.txt_lclick = txt_lclick;
    }

    public String getTxt_release() {
        return txt_release;
    }

    public void setTxt_release(String txt_release) {
        this.txt_release = txt_release;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
