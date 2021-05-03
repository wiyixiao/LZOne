package com.wiyixiao.lzone.interfaces;

import com.wiyixiao.lzone.bean.KeyInfoBean;

public interface IKeyPadListener {

    /**
     * 按键触发回调
     * @param bean 按键参数
     * @param mode 触发模式
     */
    void touch_callback(KeyInfoBean bean, int mode);

}
