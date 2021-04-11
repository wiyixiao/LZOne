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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.wiyixiao.lzone.adapter.KeysAdapter;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.utils.DisplayUtils;
import com.wiyixiao.lzone.views.KeyPadView;
import com.wiyixiao.lzone.views.SettingView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.msg_lv)
    ListView msgLv;
    @BindView(R.id.keypad_view)
    KeyPadView keyPadView;

    private Context mContext;
    private MyApplication myApplication;
    private Unbinder unbinder;

    //设置
    private SettingView settingView;
    private DeviceInfoBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mContext = this.getApplicationContext();
        myApplication = (MyApplication)mContext;
        unbinder = ButterKnife.bind(this);

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

        //设置页面
        settingView = SettingView.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
        settingView.destoryView();
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
            case R.id.item_key_set:
                DisplayUtils.showMsg(mContext, "进入按键配置模式");
                break;
            case R.id.item_set:
                //显示设置弹窗
                settingView.showDialog();
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