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

    //SPITEM_KEY
    private static final String SK_APP_AUTHOR       = "author";
    private static final String SK_RUN_FIRST        = "run_first";
    private static final String SK_CONNECT_TYPE     = "connect_type";
    private static final String SK_DISPLAY_SEND     = "display_send";
    private static final String SK_DISPLAY_TIME     = "display_time";       //send
    private static final String SK_DISPLAY_REV_TIME = "display_rev_time";   //rev
    private static final String SK_AUTO_SEND        = "auto_send";
    private static final String SK_DISPLAY_TYPE     = "display_type";
    private static final String SK_SEND_TYPE        = "send_type";
    private static final String SK_STOP_CHAR_TYPE   = "stop_char_type";
    private static final String SK_STOP_CHAR_VAL    = "stop_char_val";

    //SPITEM_VAL
    public String sv_app_author         = "wiyixiao";
    public boolean sv_run_first         = true;
    public int sv_connect_type          = 0;
    public boolean sv_display_send      = false;
    public boolean sv_display_time      = false;
    public boolean sv_display_rev_time  = false;
    public boolean sv_auto_send         = false;
    public int sv_display_type          = 0;
    public int sv_send_type             = 0;
    public int sv_stop_char_type        = 0;
    public String sv_stop_char_val      = "0D 0A";


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
        //xxxx
        sv_app_author = (String)spHelper.get(SK_APP_AUTHOR, VALUE_DEFAULT_STRING);
        //首次安装
        sv_run_first = (boolean) spHelper.get(SK_RUN_FIRST, VALUE_DEFAULT_BOOLEAN);
        //连接类型
        sv_connect_type = (int)spHelper.get(SK_CONNECT_TYPE, VALUE_DEFAULT_INT);
        //显示发送数据
        sv_display_send = (boolean)spHelper.get(SK_DISPLAY_SEND, VALUE_DEFAULT_BOOLEAN);
        //显示发送时间
        sv_display_time = (boolean)spHelper.get(SK_DISPLAY_TIME, VALUE_DEFAULT_BOOLEAN);
        //显示接收时间
        sv_display_rev_time = (boolean)spHelper.get(SK_DISPLAY_REV_TIME, VALUE_DEFAULT_BOOLEAN);
        //定时发送
        sv_auto_send = (boolean)spHelper.get(SK_AUTO_SEND, VALUE_DEFAULT_BOOLEAN);
        //接收显示类型
        sv_display_type = (int)spHelper.get(SK_DISPLAY_TYPE, VALUE_DEFAULT_INT);
        //命令行发送类型
        sv_send_type = (int)spHelper.get(SK_SEND_TYPE, VALUE_DEFAULT_INT);
        //终止符类型
        sv_stop_char_type = (int)spHelper.get(SK_STOP_CHAR_TYPE, VALUE_DEFAULT_INT);
        //终止符十六进制字符串
        sv_stop_char_val = (String)spHelper.get(SK_STOP_CHAR_VAL, VALUE_DEFAULT_STRING);

    }

    public void cfgWrite(){
        if(items.size() > 0){
            items.clear();

            //设置数据
            addData();

            //提交
            spHelper.put(items, true);

            //重新读取
            cfgRead();
        }
    }

    public void printfCfg(){
        StringBuilder builder = new StringBuilder();

        builder.append("**********Lzone Config**********").append("\n");
        builder.append(SK_APP_AUTHOR).append(": ").append(sv_app_author).append("\n");
        builder.append(SK_RUN_FIRST).append(": ").append(sv_run_first).append("\n");
        builder.append(SK_CONNECT_TYPE).append(": ").append(sv_connect_type).append("\n");
        builder.append(SK_DISPLAY_SEND).append(": ").append(sv_display_send).append("\n");
        builder.append(SK_DISPLAY_TIME).append(": ").append(sv_display_time).append("\n");
        builder.append(SK_DISPLAY_REV_TIME).append(": ").append(sv_display_rev_time).append("\n");
        builder.append(SK_AUTO_SEND).append(": ").append(sv_auto_send).append("\n");
        builder.append(SK_DISPLAY_TYPE).append(": ").append(sv_display_type).append("\n");
        builder.append(SK_SEND_TYPE).append(": ").append(sv_send_type).append("\n");
        builder.append(SK_STOP_CHAR_TYPE).append(": ").append(sv_stop_char_type).append("\n");
        builder.append(SK_STOP_CHAR_VAL).append(": ").append(sv_stop_char_val).append("\n");
        builder.append("**********Lzone Config**********").append("\n");

        System.out.println(builder.toString());
    }

    private void cfgInit(boolean overwrite){
        addData();

        spHelper.put(items, overwrite);
    }

    private void addData(){
        items.add(new SPHelper.SpItem(SK_APP_AUTHOR, sv_app_author));
        items.add(new SPHelper.SpItem(SK_RUN_FIRST, sv_run_first));
        items.add(new SPHelper.SpItem(SK_CONNECT_TYPE, sv_connect_type));
        items.add(new SPHelper.SpItem(SK_DISPLAY_SEND, sv_display_send));
        items.add(new SPHelper.SpItem(SK_DISPLAY_TIME, sv_display_time));
        items.add(new SPHelper.SpItem(SK_DISPLAY_REV_TIME, sv_display_rev_time));
        items.add(new SPHelper.SpItem(SK_AUTO_SEND, sv_auto_send));
        items.add(new SPHelper.SpItem(SK_DISPLAY_TYPE, sv_display_type));
        items.add(new SPHelper.SpItem(SK_SEND_TYPE, sv_send_type));
        items.add(new SPHelper.SpItem(SK_STOP_CHAR_TYPE, sv_stop_char_type));
        items.add(new SPHelper.SpItem(SK_STOP_CHAR_VAL, sv_stop_char_val));
    }

}
