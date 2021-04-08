package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.utils.DisplayUtils;

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
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void printDeviceInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("device ip: ").append(bean.getDevice_ip()).append("\n");
        builder.append("device port: ").append(bean.getDevice_port()).append("\n");
        builder.append("device type: ").append(bean.getDevice_type()).append("\n");
        builder.append("device auto: ").append(bean.isAuto()).append("\n");

        System.out.println(builder.toString());
    }
}