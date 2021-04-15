package com.wiyixiao.lzone.data;

import android.content.Context;

import com.wiyixiao.lzone.utils.SPHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:Think
 * Time:2021/4/7 11:30
 * Description:This is CfgDataManager
 */
public class CfgDataManager {

    //读取默认值
    private static final String VALUE_DEFAULT_STRING    =   "";
    private static final int    VALUE_DEFAULT_INT       =   0;
    private static final long   VALUE_DEFAULT_LONG      =   0L;
    private static final float  VALUE_DEFAULT_FLOAT     =   0.0f;
    private static final boolean VALUE_DEFAULT_BOOLEAN  =   false;

    private static final String CONFIG_NAME = "lzone_configuration";

    private static final String SPITEM_KEY_COMPANY = "company";
    public String spitem_val_company = "xxxx";

    private SPHelper spHelper;
    private List<SPHelper.SpItem> items = new ArrayList<>();

    private CfgDataManager(Context context) {
        if(spHelper == null){
            spHelper = SPHelper.getInstance(context, CONFIG_NAME);
            //初始化配置，不覆盖写入
            cfgInit(false);
            //读取配置
            cfgRead();
        }
    }

    public static CfgDataManager getInstance(Context context){
        return new CfgDataManager(context);
    }

    public void cfgRead(){
        //读取配置
        spitem_val_company = (String)spHelper.get(SPITEM_KEY_COMPANY, VALUE_DEFAULT_STRING);

    }

    public void cfgWrite(){
        if(items.size() > 0){
            items.clear();

            items.add(new SPHelper.SpItem(SPITEM_KEY_COMPANY, spitem_val_company));

            //提交
            spHelper.put(items, true);

            //重新读取
            cfgRead();
        }
    }

    private void cfgInit(boolean overwrite){
        //读取本地存储设置
        items.add(new SPHelper.SpItem(SPITEM_KEY_COMPANY, spitem_val_company));

        spHelper.put(items, overwrite);
    }

}
