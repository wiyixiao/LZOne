package com.wiyixiao.lzone.interfaces;

public interface IClientListener {

    /**
     * @Desc: 连接成功
     */
    void connSuccess();

    /**
     * @Desc: 连接失败
     */
    void connFailed();

    /**
     * @Desc: 无连接
     */
    void connNo();

    /**
     * @Desc: 连接断开
     */
    void disConn();

}
