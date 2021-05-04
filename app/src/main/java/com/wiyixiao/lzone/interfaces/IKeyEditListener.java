package com.wiyixiao.lzone.interfaces;

import com.wiyixiao.lzone.bean.KeyInfoBean;

public interface IKeyEditListener {

    void add(KeyInfoBean bean, String oldname);

    void remove(KeyInfoBean bean);

}
