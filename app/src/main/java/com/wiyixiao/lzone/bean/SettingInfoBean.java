package com.wiyixiao.lzone.bean;

/**
 * Author:Think
 * Time:2021/4/9 10:04
 * Description:This is SettingInfoBean
 */
public class SettingInfoBean {

    public boolean isShow_send() {
        return show_send;
    }

    public void setShow_send(boolean show_send) {
        this.show_send = show_send;
    }

    public boolean isShow_time() {
        return show_time;
    }

    public void setShow_time(boolean show_time) {
        this.show_time = show_time;
    }

    public String getEnd_symbol_str() {
        return end_symbol_str;
    }

    public void setEnd_symbol_str(String end_symbol_str) {
        this.end_symbol_str = end_symbol_str;
    }

    public int getRev_type() {
        return rev_type;
    }

    public void setRev_type(int rev_type) {
        this.rev_type = rev_type;
    }

    public int getConn_type() {
        return conn_type;
    }

    public void setConn_type(int conn_type) {
        this.conn_type = conn_type;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isShow_time_rev() {
        return show_time_rev;
    }

    public void setShow_time_rev(boolean show_time_rev) {
        this.show_time_rev = show_time_rev;
    }

    /**
     * @Desc: 连接类型，与设备信息相关
     */
    private int conn_type;

    /**
     * @Desc: 是否定时发送，与设备信息相关
     */
    private boolean auto;

    /**
     * @Desc: 是否显示发送数据
     * true | false
     */
    private boolean show_send;

    /**
     * @Desc: 接收数据类型
     * ASCII | Hex
     * 0 | 1
     */
    private int rev_type;

    /**
     * @Desc: 是否显示发送时间
     * true | false
     */
    private boolean show_time;

    /**
     * @Desc: 是否显示接收时间
     * true | false
     */
    private boolean show_time_rev;

    /**
     * @Desc: 结尾符
     */
    private String end_symbol_str;

}
