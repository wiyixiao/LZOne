package com.wiyixiao.lzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.MsgBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IClientListener;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;
import com.wiyixiao.lzone.net.LzoneClient;
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
    @BindView(R.id.send_btn)
    ImageButton sendBtn;
    @BindView(R.id.clear_btn)
    ImageButton clearBtn;
    @BindView(R.id.ascii_btn)
    RadioButton asciiBtn;
    @BindView(R.id.hex_btn)
    RadioButton hexBtn;
    @BindView(R.id.msg_edit)
    EditText msgEdit;

    private Context mContext;
    private MyApplication myApplication;
    private Unbinder unbinder;

    private volatile boolean exitFlag = false;

    //设置
    private SettingView settingView;
    private DeviceInfoBean deviceInfoBean;

    //命令行模式定时发送开启标志
    private Thread autoSendThread = null;
    private boolean autoSendFlag = false;
    private final int autoSendTime = 200;

    //按键菜单项
    private MenuItem keyCfgItem = null;
    private MenuItem connItem = null;

    //连接客户端
    private LzoneClient lzoneClient;

    /**
     * @Desc: 按键回调
     */
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

    /**
     * @Desc: 网络监听
     */
    private IClientListener mClientListener = new IClientListener() {
        @Override
        public void connSuccess() {
            DisplayUtils.showMsg(mContext, "连接成功");
            connItem.setTitle(getResources().getString(R.string.NAL_menu_disconn));
        }

        @Override
        public void connFailed() {
            DisplayUtils.showMsg(mContext, "连接失败");
        }

        @Override
        public void connNo() {
            DisplayUtils.showMsg(mContext, "请连接设备");
        }

        @Override
        public void disConn() {
            DisplayUtils.showMsg(mContext, "连接断开");
            connItem.setTitle(getResources().getString(R.string.NAL_menu_connect));
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case Constants.KEY_CMD_AUTO:
                    //定时发送
                    String cmd = msgEdit.getText().toString();
                    if(asciiBtn.isChecked()){
                        //发送字符串
                        lzoneClient.write(cmd);
                    }else{
                        //发送hex
                    }

                    if(lzoneClient.isConn()){
                        msgView.add(cmd, Vars.MsgType.SEND);
                    }
                    break;
                default:
                    break;
            }

            return false;
        }
    });

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

        //初始化客户端
        lzoneClient = new LzoneClient(this);
        lzoneClient.setiClientListener(mClientListener);

        //加载命令行发送类型
        if(myApplication.cfg.sv_send_type == 0){
            asciiBtn.setChecked(true);
        }else{
            hexBtn.setChecked(true);
        }

        //初始化命令行定时发送线程
        initCmdSendThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exitFlag = true;

        //保存配置
        myApplication.cfg.cfgWrite();

        keyPadView.close();
        msgView.close();
        settingView.close();
        lzoneClient.close(true);

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
            case R.id.item_connect:
                if(!lzoneClient.isConn()){
                    lzoneClient.connect(deviceInfoBean.getDevice_ip(), deviceInfoBean.getDevice_port());
                }else{
                    lzoneClient.close(false);
                }
                break;
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
                    keyCfgItem.setTitle(R.string.NAL_menu_key_save);

                }else{
                    DisplayUtils.showMsg(mContext, "退出按键配置模式");
                    keyPadView.setCfgMode(false);
                    keyCfgItem.setTitle(R.string.NAL_menu_key_set);
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
        keyCfgItem = menu.findItem(R.id.item_key_set);
        connItem = menu.findItem(R.id.item_connect);
        return true;
    }

    @OnClick({R.id.clear_btn, R.id.ascii_btn, R.id.hex_btn, R.id.send_btn})
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.ascii_btn:
                myApplication.cfg.sv_send_type = 0;
                break;
            case R.id.hex_btn:
                myApplication.cfg.sv_send_type = 1;
                break;
            case R.id.send_btn:
                if(deviceInfoBean.isAuto()){
                    if(!autoSendFlag){
                        //定时发送线程
                        autoSendFlag = true;
                        DisplayUtils.showMsg(mContext, "定时发送(200ms)");

                    }else{
                        //清除定时发送标志位
                        autoSendFlag = false;
                        DisplayUtils.showMsg(mContext, "退出定时发送");

                    }
                }else{
                    if(autoSendFlag){
                        autoSendFlag = false;
                    }
                    Message msg = new Message();
                    msg.what = Constants.KEY_CMD_AUTO;
                    handler.sendMessage(msg);
                }
                break;
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

    private void initCmdSendThread(){
        autoSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exitFlag){
                    try {
                        if(autoSendFlag){
                            Message msg = new Message();
                            msg.what = Constants.KEY_CMD_AUTO;
                            handler.sendMessage(msg);
                            Thread.sleep(autoSendTime);
                        }else{
                            Thread.sleep(2);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                System.out.println("CmdSendThread Exit!");
            }
        });
        autoSendThread.setName("CmdSendThread");
        autoSendThread.start();
    }















}