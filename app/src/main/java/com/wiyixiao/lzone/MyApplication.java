package com.wiyixiao.lzone;

import android.content.res.Configuration;

import com.wiyixiao.lzone.base.ApplaicationBase;
import com.wiyixiao.lzone.bean.SettingInfoBean;
import com.wiyixiao.lzone.data.CfgDataManager;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.db.DBManager;

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
    public final String dbName = "lzone.db";
    public final int dbVersion = 1;
    public DBManager dbManager;

    /**
     * @Desc: 键盘默认行数
     */
    public final int keyPadRow = 4;

    /**
     * @Desc: 键盘默认列数
     */
    public final int keyPadCol = 4;

    /**
     * 没有自定义设置时的默认按键数量
     */
    public final int keyDdefaultCount = 20;

    /**
     * @Desc: 设置界面数据全局变量
     */
    public SettingInfoBean settingData;

    /**
     * @Desc: 配置文件管理
     */
    public CfgDataManager cfg;

    @Override
    public void onCreate() {
        super.onCreate();

    }


}
