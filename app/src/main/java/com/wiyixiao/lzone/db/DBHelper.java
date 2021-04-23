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

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DBHelper", "DataBase create");

        //创建连接设备数据表
        db.execSQL("CREATE TABLE IF NOT EXISTS device_table(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ip VARCHAR, port INTEGER, type INTEGER, auto INTEGER)");

        //创建设备对应的按键数据表
        db.execSQL("CREATE TABLE IF NOT EXISTS key_table(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ip VARCHAR, title TEXT, click_txt TEXT, lclick_txt TEXT, release_txt TEXT, type INTEGER, time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
