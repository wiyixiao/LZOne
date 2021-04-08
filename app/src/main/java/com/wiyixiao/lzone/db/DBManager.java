package com.wiyixiao.lzone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wiyixiao.lzone.MyApplication;

public class DBManager {

    private MyApplication myApplication;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        myApplication = (MyApplication)context.getApplicationContext();
        this.dbHelper = new DBHelper(context, myApplication.dbName, null, myApplication.dbVersion);
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


}
