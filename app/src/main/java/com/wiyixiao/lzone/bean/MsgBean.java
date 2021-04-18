package com.wiyixiao.lzone.bean;

/**
 * Author:Think
 * Time:2021/4/18 22:19
 * Description:This is MsgBean
 */
public class MsgBean {

    public String getMsg_txt() {
        return msg_txt;
    }

    public void setMsg_txt(String msg_txt) {
        this.msg_txt = msg_txt;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    /**
     * @Desc: 接收 或 发送 消息数据
     */
    private String msg_txt;

    /**
     * @Desc: 消息类型
     * 消息类型:
     * 0: 发送
     * 1: 接收
     * 2: APP消息
     */
    private int msg_type;

}
