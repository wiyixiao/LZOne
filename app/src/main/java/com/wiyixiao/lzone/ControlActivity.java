package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.utils.DisplayUtils;

import java.lang.reflect.Method;
import java.util.Objects;

import butterknife.BindView;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.msg_lv)
    ListView msgLv;
    @BindView(R.id.key_layout)
    LinearLayout keyLayout;

    private Context mContext;
    private MyApplication myApplication;

    private DeviceInfoBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mContext = this.getApplicationContext();
        myApplication = (MyApplication)mContext;

        String device_json = getIntent().getStringExtra(Constants.DEVICE_INTENT_NAME);
        bean = new Gson().fromJson(device_json, DeviceInfoBean.class);

        if(BuildConfig.DEBUG){
            printDeviceInfo();
        }

        //设置页面标题
        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        //设置标题居中
        DisplayUtils.setCenterTitleActionBar(actionBar,
                this,
                String.format("%s:%s",bean.getDevice_ip(), bean.getDevice_port()),
                getResources().getDimensionPixelOffset(R.dimen.sp_22),
                Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.item_key:
                //增加按钮弹窗
                showKeyEditDialog(null);
                break;
            case R.id.item_set:
                //显示设置弹窗

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO 状态栏按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_control_layout, menu);
        return true;
    }

    private void printDeviceInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("device ip: ").append(bean.getDevice_ip()).append("\n");
        builder.append("device port: ").append(bean.getDevice_port()).append("\n");
        builder.append("device type: ").append(bean.getDevice_type()).append("\n");
        builder.append("device auto: ").append(bean.isAuto()).append("\n");

        System.out.println(builder.toString());
    }

    private void showKeyEditDialog(KeyInfoBean bean){
        //图层模板生成器句柄
        LayoutInflater factory = LayoutInflater.from(this);
        //用sname.xml模板生成视图模板
        final View dialogView = factory.inflate(R.layout.item_key_info, null);

        android.app.AlertDialog saveDialog =  new android.app.AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.NAL_key_cfg))
                //设置视图模板
                .setView(dialogView)
                //显示对话框
                .show();
    }
}