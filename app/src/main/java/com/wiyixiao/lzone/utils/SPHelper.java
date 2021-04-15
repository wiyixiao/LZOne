package com.wiyixiao.lzone.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * SharedPreferences帮助类
 */
public class SPHelper {

    private static final int VALUE_TYPE_STRING = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_LONG = 2;
    private static final int VALUE_TYPE_FLOAT = 3;
    private static final int VALUE_TYPE_BOOLEAN = 4;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static SPHelper spHelper;

    public static class SpItem<T>{
        private String mKey;
        private T mValue;
        private int mValueType;

        public SpItem(String key, T value) {
            this.mKey = key;
            this.mValue = value;
            this.mValueType = initValueType(value);
        }

        private int initValueType(T t) {
            int typ = -1;
            if (t instanceof String) {
                typ = VALUE_TYPE_STRING;
            } else if (t instanceof Integer) {
                typ = VALUE_TYPE_INT;
            } else if (t instanceof Long) {
                typ = VALUE_TYPE_LONG;
            } else if (t instanceof Float) {
                typ = VALUE_TYPE_FLOAT;
            } else if (t instanceof Boolean) {
                typ = VALUE_TYPE_BOOLEAN;
            }
            return typ;
        }
    }

    private SPHelper(Context context, String name){
        sp = context.getSharedPreferences(name, MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SPHelper getInstance(Context context, String name){

        if(spHelper == null){
            spHelper = new SPHelper(context, name);
        }

        return spHelper;
    }

    /**
     * @写入值
     * @param spItem
     */
    public void put(SpItem spItem)
    {

        if (spItem == null || editor == null) {
            return;
        }
        switch (spItem.mValueType) {
            case VALUE_TYPE_STRING:
                editor.putString(spItem.mKey, (String) spItem.mValue);
                break;
            case VALUE_TYPE_INT:
                editor.putInt(spItem.mKey, (Integer) spItem.mValue);
                break;
            case VALUE_TYPE_LONG:
                editor.putLong(spItem.mKey, (Long) spItem.mValue);
                break;
            case VALUE_TYPE_FLOAT:
                editor.putFloat(spItem.mKey, (Float) spItem.mValue);
                break;
            case VALUE_TYPE_BOOLEAN:
                editor.putBoolean(spItem.mKey, (Boolean) spItem.mValue);
                break;
            default:
                break;
        }
        editor.commit();
    }

    /**
     * @写入值
     * @param items
     * @param overwrite //是否覆盖
     */
    public void put(List<SpItem> items, boolean overwrite){

        for (SpItem item: items
             ) {
            if(contains(item.mKey)){
                //如果值存在，确定是否覆盖写入
                if(overwrite){
                    put(item);
                }else{
                    continue;
                }
            }else{
                //如果值不存在，写入
                put(item);
            }
        }
    }

    /**
     * @获取值
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject)
    {

        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * @移除值
     * @param key
     */
    public void remove(String key)
    {
        if(contains(key)){
            editor.remove(key);
            editor.commit();
        }
    }

    /**
     * @清除数据
     */
    public void clear()
    {
        editor.clear();
        editor.commit();
    }

    /**
     * @查询某个key是否存在
     * @param key
     * @return
     */
    public boolean contains(String key)
    {
        return sp.contains(key);
    }

    /**
     * @返回所有键值对
     * @return
     */
    public Map<String, ?> getAll()
    {
        return sp.getAll();
    }

}