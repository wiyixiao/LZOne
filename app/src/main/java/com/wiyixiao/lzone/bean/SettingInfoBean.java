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

    /**
     * @Desc: 是否显示发送数据
     * true | false
     */
    private boolean show_send;

    /**
     * @Desc: 接收数据类型
     * Str | Hex
     * 0 | 1
     */
    private int rev_type;

    /**
     * @Desc: 是否显示发送时间
     * true | false
     */
    private boolean show_time;

    /**
     * @Desc: 结尾符
     */
    private String end_symbol_str;

}
