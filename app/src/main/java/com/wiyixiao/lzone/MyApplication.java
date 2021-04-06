package com.wiyixiao.lzone;

import com.wiyixiao.lzone.base.ApplaicationBase;
import com.wiyixiao.lzone.data.Vars;

public class MyApplication extends ApplaicationBase {

    public String getTAG() {
        return TAG;
    }

    /**
     * @Desc: 当前页面ID
     */
    public int intentType = Vars.IntentType.CONTROL;

    /**
     * @Desc: 数据库名称&版本
     */
    public String dbName = "lzone.db";
    public int dbVersion = 1;
}
