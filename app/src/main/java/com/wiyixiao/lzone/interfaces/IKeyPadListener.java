package com.wiyixiao.lzone.interfaces;

import com.wiyixiao.lzone.bean.KeyInfoBean;

public interface IKeyPadListener {

    /**
     * @Desc: 短按发送数据
     */
    void short_press(String data);

    /**
     * @Desc: 长按发送数据
     */
    void long_press(String data);

    /**
     * @Desc: 释放发送数据
     */
    void release_press(String data);

}
