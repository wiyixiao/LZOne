package com.wiyixiao.lzone.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wiyixiao.lzone.activity.MyApplication;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.KeyInfoBean;

import java.util.ArrayList;
import java.util.List;

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
        db.close();
    }

    public String getDbInfo(){
        return (this.dbHelper.getDatabaseName() + " " + this.db.getVersion());
    }

    public int getTableDataCount(String tname){
        int number = 0;
        Cursor c = null;
        try{
            c = db.rawQuery(String.format("select * from %s", tname), null);
            number = c.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(c != null){c.close();}
        }
        return number;
    }

    public boolean searchData(String tname, String ip){

        try (Cursor cursor = db.rawQuery("select * from " + tname + " where ip=?", new String[]{ip})) {
            if (cursor.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean searchKey(String ip, String name){
        try(Cursor cursor = db.query(DBTable.TableName.key,
                null,
                "ip=? and name=?",
                new String[]{ip, name},
                null,
                null,
                null)){

            if(cursor.moveToNext()){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*****************************************Device*****************************************/
    public List<DeviceInfoBean> getDevices(){
        List<DeviceInfoBean> list = new ArrayList<>();

        int index = 0;
        try (Cursor cursor = db.query(DBTable.TableName.device,
                null,
                null,
                null,
                null,
                null,
                null)) {
            //读取全部数据
            while (cursor.moveToNext()) {
                DeviceInfoBean bean = new DeviceInfoBean();
                index = cursor.getColumnIndex(DeviceInfoBean._IP);
                bean.setDevice_ip(cursor.getString(index));
                index = cursor.getColumnIndex(DeviceInfoBean._PORT);
                bean.setDevice_port(cursor.getString(index));
                index = cursor.getColumnIndex(DeviceInfoBean._TYPE);
                bean.setDevice_type(cursor.getInt(index));
                index = cursor.getColumnIndex(DeviceInfoBean._AUTO);
                bean.setAuto(cursor.getInt(index) == 1);

                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public long insertDevice(DeviceInfoBean bean){
        return db.insert(DBTable.TableName.device, null, bean.toContentValues());
    }

    public int removeDevice(String ip){
        int i = db.delete(DBTable.TableName.device, "ip=?", new String[]{ip});
        return i;
    }

    public int updateDevice(DeviceInfoBean bean){
        int i = db.update(DBTable.TableName.device,
                bean.toContentValues(), "ip=?",
                new String[]{bean.getDevice_ip()});
        return i;
    }

    /*****************************************Key*****************************************/
    /**
     * @Desc: 获取对应IP的全部键位
     * @Author: Aries.hu
     * @Date: 2021/5/4 13:56
     */
    public List<KeyInfoBean> getKeys(String ip){
        List<KeyInfoBean> list= new ArrayList<>();
        int index = 0;

        try (Cursor cursor = db.query(DBTable.TableName.key,
                null,
                "ip=?",
                new String[]{ip},
                null,
                null,
                null)) {

            while (cursor.moveToNext()) {
                KeyInfoBean bean = new KeyInfoBean();
                index = cursor.getColumnIndex(KeyInfoBean._IP);
                bean.setIp(cursor.getString(index));
                index = cursor.getColumnIndex(KeyInfoBean._NAME);
                bean.setName(cursor.getString(index));
                index = cursor.getColumnIndex(KeyInfoBean._CLICK);
                bean.setTxt_click(cursor.getString(index));
                index = cursor.getColumnIndex(KeyInfoBean._LCLICK);
                bean.setTxt_lclick(cursor.getString(index));
                index = cursor.getColumnIndex(KeyInfoBean._RELEASE);
                bean.setTxt_release(cursor.getString(index));
                index = cursor.getColumnIndex(KeyInfoBean._TYPE);
                bean.setType(cursor.getInt(index));
                index = cursor.getColumnIndex(KeyInfoBean._TIME);
                bean.setTime(String.valueOf(cursor.getInt(index)));
                index = cursor.getColumnIndex(KeyInfoBean._IDX);
                bean.setIndex(cursor.getInt(index));

                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public long insertKey(KeyInfoBean bean){
        return db.insert(DBTable.TableName.key, null, bean.toContentValues());
    }

    public int removeKey(KeyInfoBean bean){
        return db.delete(DBTable.TableName.key,
                "ip=? and name=?",
                new String[]{bean.getIp(), bean.getName()});
    }

    public int updateKey(KeyInfoBean bean, String oldname){
        return db.update(DBTable.TableName.key,
                bean.toContentValues(),
                "ip=? and name=?",
                new String[]{bean.getIp(), oldname});
    }

    public int clearKeysByIp(String ip){
        return db.delete(DBTable.TableName.key,
                "ip=?",
                new String[]{ip});
    }


    public void clearTable(String tname){

        if(getTableDataCount(tname) <= 0){
            return;
        }

        final String sql_delete_table = String.format("delete from %s", tname);
        final String sql_reset_id     = String.format("update sqlite_sequence SET seq = 0 where name = '%s'", tname);

        db.execSQL(sql_delete_table);
        db.execSQL(sql_reset_id);
    }

    public void initDbTable(){
        clearTable(DBTable.TableName.device);
        clearTable(DBTable.TableName.key);
    }

}
