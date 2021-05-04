package com.wiyixiao.lzone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Author:Think
 * Time:2021/4/6 16:24
 * Description:This is DBHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "DataBase create");

        initDataBase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库版本号大于当前版本，执行此处
        Log.e(TAG, "onUpgrade method");

        //v1.0 -> V2.0
        if(oldVersion == 1){
            Log.e(TAG, "Database update v1.0 -> v2.0");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try{
            super.onDowngrade(db, oldVersion, newVersion);

        }catch (Exception e){
            Log.e(TAG, "onDowngrade failed, init database");
            String sql_drop_device_table = "drop table if exists t_device";
            String sql_drop_key_table = "drop table if exists t_key";
            db.execSQL(sql_drop_device_table);
            db.execSQL(sql_drop_key_table);

            initDataBase(db);
        }
    }

    private void initDataBase(SQLiteDatabase db){
        /**
         * 创建连接设备数据表
         * @ip          设备IP地址
         * @port        设备端口
         * @type        设备类型（TCP || UDP）
         * @auto        命令行定时发送（YES || NO）
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS t_device(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ip VARCHAR, port INTEGER, type INTEGER, auto INTEGER)");

        /**
         * 创建设备对应的按键数据表
         * @ip          按键对应设备IP号
         * @name        按键名
         * @click       按下发送数据
         * @lclick      长按发送数据
         * @release     释放发送数据
         * @type        发送数据类型（ASCII || HEX）
         * @time        长按发送间隔（ms）
         * @idx         按键对应索引
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS t_key(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ip VARCHAR, name TEXT, " +
                "click_txt TEXT, lclick_txt TEXT, release_txt TEXT," +
                " type INTEGER, time INTEGER, idx INTEGER)");
    }
}
