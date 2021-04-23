package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.MsgBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;
import com.wiyixiao.lzone.utils.DisplayUtils;
import com.wiyixiao.lzone.views.KeyPadView;
import com.wiyixiao.lzone.views.MsgView;
import com.wiyixiao.lzone.views.SettingView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.keypad_view)
    KeyPadView keyPadView;
    @BindView(R.id.msg_view)
    MsgView msgView;
    @BindView(R.id.clear_btn)
    ImageButton clearBtn;

    private Context mContext;
    private MyApplication myApplication;
    private Unbinder unbinder;

    //设置
    private SettingView settingView;
    private DeviceInfoBean deviceInfoBean;

    private IKeyPadListener mKeyPadListener = new IKeyPadListener() {

        @Override
        public void short_press(String data) {
            if(BuildConfig.DEBUG){
                System.out.println("***************************************");
                System.out.println(data);
            }

            msgView.add(data, Vars.MsgType.SEND);
        }

        @Override
        public void long_press(String data) {
            if(BuildConfig.DEBUG){
                System.out.println("***************************************");
                System.out.println(data);
            }

            msgView.add(data, Vars.MsgType.SEND);
        }

        @Override
        public void release_press(String data) {
            if(BuildConfig.DEBUG){
                System.out.println("***************************************");
                System.out.println(data);
            }

            msgView.add(data, Vars.MsgType.SEND);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mContext = this.getApplicationContext();
        myApplication = (MyApplication)mContext;
        unbinder = ButterKnife.bind(this);

        String device_json = getIntent().getStringExtra(Constants.DEVICE_INTENT_NAME);
        deviceInfoBean = new Gson().fromJson(device_json, DeviceInfoBean.class);

        if(BuildConfig.DEBUG){
            printDeviceInfo();
        }

        //设置页面标题
        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        //设置标题居中
        DisplayUtils.setCenterTitleActionBar(actionBar,
                this,
                String.format("%s:%s",deviceInfoBean.getDevice_ip(), deviceInfoBean.getDevice_port()),
                getResources().getDimensionPixelOffset(R.dimen.sp_22),
                Color.WHITE);

        //设置页面
        settingView = SettingView.getInstance(this);
        keyPadView.keySetListener(mContext, mKeyPadListener, deviceInfoBean.getDevice_ip());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        keyPadView.close();
        msgView.close();
        settingView.close();

        unbinder.unbind();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivity();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                exitActivity();
                break;
            case R.id.item_key_add:
                //增加按钮弹窗
                keyPadView.keyShowEditDialog();
                break;
            case R.id.item_key_clear:
                keyPadView.keyClear();
                break;
            case R.id.item_key_set:
                if(!keyPadView.isCfgMode()){
                    DisplayUtils.showMsg(mContext, "进入按键配置模式");
                    keyPadView.setCfgMode(true);
                }else{
                    DisplayUtils.showMsg(mContext, "退出按键配置模式");
                    keyPadView.setCfgMode(false);
                }
                break;
            case R.id.item_set:
                //显示设置弹窗
                settingView.showDialog(deviceInfoBean);
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

    @OnClick({R.id.clear_btn})
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.clear_btn:
                msgView.clear();
                break;
            default:
                break;
        }
    }

    private void printDeviceInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("device ip: ").append(deviceInfoBean.getDevice_ip()).append("\n");
        builder.append("device port: ").append(deviceInfoBean.getDevice_port()).append("\n");
        builder.append("device type: ").append(deviceInfoBean.getDevice_type()).append("\n");
        builder.append("device auto: ").append(deviceInfoBean.isAuto()).append("\n");

        System.out.println(builder.toString());
    }

    private void exitActivity(){
        Intent intent = new Intent();
        intent.putExtra(Constants.DEVICE_INTENT_NAME, new Gson().toJson(deviceInfoBean));
        // 设置返回值并结束程序
        setResult(Activity.RESULT_OK, intent);

        finish();
    }


















}