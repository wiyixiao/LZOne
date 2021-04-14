package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.wiyixiao.lzone.adapter.KeysAdapter;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IKeyPadListener;
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
    private DeviceInfoBean deviceInfoBean;

    private IKeyPadListener mKeyPadListener = new IKeyPadListener() {
        @Override
        public void keyEdit(KeyInfoBean bean) {
            showKeyEditDialog(bean);
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
        keyPadView.keySetListener(mContext, mKeyPadListener);
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
        builder.append("device ip: ").append(deviceInfoBean.getDevice_ip()).append("\n");
        builder.append("device port: ").append(deviceInfoBean.getDevice_port()).append("\n");
        builder.append("device type: ").append(deviceInfoBean.getDevice_type()).append("\n");
        builder.append("device auto: ").append(deviceInfoBean.isAuto()).append("\n");

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

        final Button save_btn = dialogView.findViewById(R.id.btn_save);
        final Button del_btn = dialogView.findViewById(R.id.btn_del);

        final RadioButton ascii_btn = dialogView.findViewById(R.id.ascii_btn);
        final RadioButton hex_btn = dialogView.findViewById(R.id.hex_btn);
        final EditText edit_name = dialogView.findViewById(R.id.key_name_edit);
        final EditText edit_click_txt = dialogView.findViewById(R.id.key_click_txt_edit);
        final EditText edit_lclick_txt = dialogView.findViewById(R.id.key_lclick_txt_edit);
        final EditText edit_release_txt = dialogView.findViewById(R.id.key_release_txt_edit);
        final EditText edit_time_txt = dialogView.findViewById(R.id.key_time_edit);

        if(bean != null){
            if(bean.getType() == 0){
                ascii_btn.setChecked(true);
            }else{
                hex_btn.setChecked(true);
            }
            edit_name.setText(bean.getName());
            edit_click_txt.setText(bean.getTxt_click());
            edit_lclick_txt.setText(bean.getTxt_lclick());
            edit_release_txt.setText(bean.getTxt_release());
            edit_time_txt.setText(bean.getTime());
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DisplayUtils.showMsg(mContext, "save click");

                final String name = edit_name.getText().toString();

                if(TextUtils.isEmpty(name)){
                    DisplayUtils.showMsg(mContext, getResources().getString(R.string.NAL_input_empty));
                    return;
                }

                //添加自定义按键
                if(bean != null && keyPadView.isCfgMode()){
                    //配置模式仅修改
                    bean.setType(ascii_btn.isChecked() ? 0 : 1);
                    bean.setName(name);
                    bean.setTxt_click(edit_click_txt.getText().toString());
                    bean.setTxt_lclick(edit_lclick_txt.getText().toString());
                    bean.setTxt_release(edit_release_txt.getText().toString());
                    bean.setTime(edit_time_txt.getText().toString());
                    keyPadView.keyUpdate();
                }else{
                    //非配置模式添加
                    KeyInfoBean keyInfoBean = new KeyInfoBean();
                    keyInfoBean.setType(ascii_btn.isChecked() ? 0 : 1);
                    keyInfoBean.setName(name);
                    keyInfoBean.setTxt_click(edit_click_txt.getText().toString());
                    keyInfoBean.setTxt_lclick(edit_lclick_txt.getText().toString());
                    keyInfoBean.setTxt_release(edit_release_txt.getText().toString());
                    keyInfoBean.setTime(edit_time_txt.getText().toString());
                    keyPadView.keyAdd(keyInfoBean);
                }

                saveDialog.dismiss();
            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DisplayUtils.showMsg(mContext, "del click");

                if(keyPadView.isCfgMode() && keyPadView.keyRemove(bean, deviceInfoBean.getDevice_ip())){
                    DisplayUtils.showMsg(mContext, "移除成功");

                    saveDialog.dismiss();
                }else{
                    DisplayUtils.showMsg(mContext, getResources().getString(R.string.NAL_device_invalid));
                }

            }
        });

    }


























}