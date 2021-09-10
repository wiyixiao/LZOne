package com.wiyixiao.lzone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.wiyixiao.lzone.BuildConfig;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.core.LocalThreadPools;
import com.wiyixiao.lzone.core.PriorityRunnable;
import com.wiyixiao.lzone.core.PriorityType;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IClientListener;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;
import com.wiyixiao.lzone.interfaces.ISettingListener;
import com.wiyixiao.lzone.net.LzoneClient;
import com.wiyixiao.lzone.utils.DataTransform;
import com.wiyixiao.lzone.utils.DisplayUtil;
import com.wiyixiao.lzone.utils.FileUtil;
import com.wiyixiao.lzone.utils.Utils;
import com.wiyixiao.lzone.views.KeyPadView;
import com.wiyixiao.lzone.views.MsgView;
import com.wiyixiao.lzone.views.SettingView;
import com.wiyixiao.lzone.views.ShareFileView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ControlActivity extends AppCompatActivity implements ISettingListener {

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
    private boolean autoSendFlag = false;
    private final int autoSendTime = 200;

    //按键菜单项
    private MenuItem keyCfgItem = null;
    private MenuItem connItem = null;

    //连接客户端
    private LzoneClient lzoneClient;

    //分享文件弹窗
    private ShareFileView shareFileView;

    //记录上一次命令行值，避免每次发送重复检测有效字符
    private String lastHexCmd = "";
    private byte[] hexCmd = null;

    /**
     * @Desc: 按键回调
     */
    private IKeyPadListener mKeyPadListener = new IKeyPadListener() {

        @Override
        public void touch_callback(KeyInfoBean bean, int mode) {
            String cmd = "";
            byte[] hex = null;
            switch (mode){
                case Vars.KeyTouchMode.PRESS:
                    cmd = bean.getTxt_click();
                    if(bean.getType() == Vars.DataType.HEX){
                        if(!bean.getLastClickHexCmd().equals(cmd)){
                            cmd = DataTransform.checkHexLength(cmd);
                            bean.setHexClickCmd(DataTransform.hexTobytes(cmd));
                            bean.setLastClickHexCmd(cmd);
                        }
                        hex = bean.getHexClickCmd();
                    }
                    break;
                case Vars.KeyTouchMode.HOLD:
                    cmd = bean.getTxt_lclick();
                    if(bean.getType() == Vars.DataType.HEX){
                        if(!bean.getLastLClickHexCmd().equals(cmd)){
                            cmd = DataTransform.checkHexLength(cmd);
                            bean.setHexLClickCmd(DataTransform.hexTobytes(cmd));
                            bean.setLastLClickHexCmd(cmd);
                        }
                        hex = bean.getHexLClickCmd();
                    }
                    break;
                case Vars.KeyTouchMode.RELEASE:
                    cmd = bean.getTxt_release();
                    if(bean.getType() == Vars.DataType.HEX){
                        if(!bean.getLastReleaseHexCmd().equals(cmd)){
                            cmd = DataTransform.checkHexLength(cmd);
                            bean.setHexReleaseCmd(DataTransform.hexTobytes(cmd));
                            bean.setLastReleaseHexCmd(cmd);
                        }
                        hex = bean.getHexReleaseCmd();
                    }
                    break;
                default:
                    break;
            }

            if(bean.getType() == Vars.DataType.ASCII){
                lzoneClient.writeStr(cmd, myApplication.cfg.sv_stop_char_type);
            }else{
                lzoneClient.writeBytes(hex, myApplication.cfg.sv_stop_char_type);
            }

            if(lzoneClient.isConn() && myApplication.cfg.sv_display_send){
                msgView.add(getMsgItem(cmd), Vars.MsgType.SEND);
            }
        }
    };

    /**
     * @Desc: 网络监听
     */
    private IClientListener mClientListener = new IClientListener() {
        @Override
        public void connSuccess() {
            DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_conn_success));
            connItem.setTitle(getResources().getString(R.string.NAL_menu_disconn));
        }

        @Override
        public void connFailed() {
            DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_conn_failed));
        }

        @Override
        public void connNo() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_conn_no));
                }
            });
        }

        @Override
        public void disConn(int arg) {
            System.out.println(arg);
            if(arg == 0){
                DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_conn_discon));
                connItem.setTitle(getResources().getString(R.string.NAL_menu_connect));
            }else if(arg == 1){
                if(!lzoneClient.isConn()){
                    lzoneClient.connect(deviceInfoBean.getDevice_ip(),
                            deviceInfoBean.getDevice_port(),
                            deviceInfoBean.getDevice_type());
                }
            }
        }

        @Override
        public void revCall(byte[] rev) {
            if(myApplication.cfg.sv_display_type == Vars.DataType.ASCII){
                //字节数组以ASCII形式显示
                msgView.add(getMsgRevItem(DataTransform.byte2Char(rev)), Vars.MsgType.REV);
            }else{
                //字节数组以HEX形式显示
                msgView.add(getMsgRevItem(DataTransform.byte2hex(rev).toString()), Vars.MsgType.REV);
            }
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
                        lzoneClient.writeStr(cmd, myApplication.cfg.sv_stop_char_type);
                    }else{
                        //发送hex
                        //检测有效字符
                        if(!lastHexCmd.equals(cmd)){
                            if(BuildConfig.DEBUG) {System.out.println("**********Hex cmd set**********"); }
                            cmd = DataTransform.checkHexLength(cmd);
                            msgEdit.setText(cmd);
                            hexCmd = DataTransform.hexTobytes(cmd);
                            lastHexCmd = cmd;
                        }
                        lzoneClient.writeBytes(hexCmd, myApplication.cfg.sv_stop_char_type);
                    }

                    if(lzoneClient.isConn() && myApplication.cfg.sv_display_send){
                        msgView.add(getMsgItem(cmd), Vars.MsgType.SEND);
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
        DisplayUtil.setCenterTitleActionBar(actionBar,
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

        //加载命令行发送类型,设置初始过滤器
        if(myApplication.cfg.sv_send_type == 0){
            asciiBtn.setChecked(true);
            msgEdit.setFilters(myApplication.cmdAsciiFilter);
        }else{
            hexBtn.setChecked(true);
            msgEdit.setFilters(myApplication.cmdHexFilter);
        }

        //初始化自动发送线程
        initCmdSendThread();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exitFlag = true;

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
                if(!lzoneClient.isConnSuccess()){
                    lzoneClient.connect(deviceInfoBean.getDevice_ip(),
                                        deviceInfoBean.getDevice_port(),
                                        deviceInfoBean.getDevice_type());
                }else{
                    lzoneClient.close(true);
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
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_key_cfgmode));
                    keyPadView.setCfgMode(true);
                    keyCfgItem.setTitle(R.string.NAL_menu_key_save);

                }else{
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_key_runmode));
                    keyPadView.setCfgMode(false);
                    keyCfgItem.setTitle(R.string.NAL_menu_key_set);
                }
                break;
            case R.id.item_set:
                //显示设置弹窗
                settingView.showDialog(deviceInfoBean);
                break;
            case R.id.item_save:
                saveShare();
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
                msgEdit.setText("");
                myApplication.cfg.sv_send_type = 0;
                msgEdit.setFilters(myApplication.cmdAsciiFilter);
                break;
            case R.id.hex_btn:
                msgEdit.setText("");
                myApplication.cfg.sv_send_type = 1;
                msgEdit.setFilters(myApplication.cmdHexFilter);
                break;
            case R.id.send_btn:
                if(deviceInfoBean.isAuto() && lzoneClient.isConn()){
                    if(!autoSendFlag){
                        //定时发送线程
                        autoSendFlag = true;
                        DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_cmd_automode));

                    }else{
                        //清除定时发送标志位
                        autoSendFlag = false;
                        DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_cmd_pressmode));

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

        //保存配置
        myApplication.cfg.cfgWrite();

        keyPadView.close();
        msgView.close();
        settingView.close();
        lzoneClient.close(true);

        finish();
    }

    private void initCmdSendThread(){

        PriorityRunnable autoSendRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
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

        LocalThreadPools.getInstance().getExecutorService().execute(autoSendRunnable);
    }

    private String getMsgItem(String cmd){
        StringBuffer sb = new StringBuffer();
        if(myApplication.cfg.sv_display_time){
            sb.append(Utils.getSysTime()).append(" ");
        }
        sb.append(cmd);
        return sb.toString();
    }

    private String getMsgRevItem(String data){
        StringBuffer sb = new StringBuffer();
        if(myApplication.cfg.sv_display_rev_time){
            sb.append(Utils.getSysTime()).append(" ");
        }
        sb.append(data);
        return sb.toString();
    }

    /******************************************Share Data***************************************/
     @Override
     protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         switch (requestCode){
             case Constants.REQUEST_SHARE_FILE_CODE:
                 if(shareFileView != null){
                 FileUtil.deleteSingleFile(shareFileView.getmUnZipFilePath());
                 }
             break;
             default:
             break;
         }
     }

    private void shareLocalFile(String path, String name){
        shareFileView = new ShareFileView(this,
                path,
                name,
                FileUtil.getFilesAllName(path));

        shareFileView.show();
    }

    private void saveShare() {
        //图层模板生成器句柄
        LayoutInflater factory = LayoutInflater.from(this);
        //用sname.xml模板生成视图模板
        final View DialogView = factory.inflate(R.layout.item_input_sname, null);
        AlertDialog saveDialog =  new AlertDialog.Builder(this)
                //设置视图模板
                .setView(DialogView)
                //确定按键响应函数
                .setPositiveButton(getResources().getString(R.string.NAL_btn_ok),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText text1 = (EditText) DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
                                String fileName = text1.getText().toString();  //得到文件名

                                if(TextUtils.isEmpty(fileName)) {
                                    return;
                                }

                                try {
                                    //如果SD卡已准备好
                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        saveMsgData(fileName);
                                    } else {
                                        DisplayUtil.showMsg(mContext, "Save file error!");
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                //取消按键响应函数,直接退出对话框不做任何处理
                .setNegativeButton(getResources().getString(R.string.NAL_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();  //显示对话框

        Button btnPositive = saveDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = saveDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        int btnColor = ContextCompat.getColor(this, R.color.colorPrimary);
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
        btnPositive.setTextColor(btnColor);
        btnNegative.setTextColor(btnColor);

    }

    private void saveMsgData(String fileName){
        String savePath = Environment.getExternalStorageDirectory().getPath() + myApplication.saveDir;

        //判断文件夹是否存在，不存在创建
        FileUtil.checkDir(savePath);

        try{
            //保存文件
            if(msgView.save(savePath + fileName + ".csv")){
                //保存完成，分享文件
                shareLocalFile(savePath, fileName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /********************************Setting Listener************************************/
    @Override
    public void heartBeatCall(boolean state) {
        if(lzoneClient != null){
            lzoneClient.setHeatBeat(state);
        }
    }
}