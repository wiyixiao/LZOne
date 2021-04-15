package com.wiyixiao.lzone.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.bean.DeviceInfoBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author:Think
 * Time:2021/3/6 9:31
 * Description:This is SettingView
 */
public class SettingView {

    @BindView(R.id.saveTV)
    Button saveTV;
    @BindView(R.id.tcp_rbtn)
    RadioButton tcpRbtn;
    @BindView(R.id.udp_rbtn)
    RadioButton udpRbtn;
    @BindView(R.id.cb_show_send)
    CheckBox cbShowSend;
    @BindView(R.id.cb_show_time)
    CheckBox cbShowTime;
    @BindView(R.id.cb_auto)
    CheckBox cbAuto;
    @BindView(R.id.ascii_rbtn)
    RadioButton asciiRbtn;
    @BindView(R.id.hex_rbtn)
    RadioButton hexRbtn;
    @BindView(R.id.rn_rbtn)
    RadioButton rnRbtn;
    @BindView(R.id.n_rbtn)
    RadioButton nRbtn;
    @BindView(R.id.not_rn_rbtn)
    RadioButton notRnRbtn;
    @BindView(R.id.edit_hex_rn)
    EditText editHexRn;
    private View mView;
    private Context mContext;
    private Dialog mDialog;

    private MyApplication myApplication;

    private LinearLayout durLayout;
    private EditText mEditText;
    private Unbinder unbinder;

    private DeviceInfoBean deviceBean;

    private SettingView(Context context) {
        this.mContext = context;
        this.mDialog = new Dialog(context, R.style.Theme_dialog);
        this.mView = View.inflate(context, R.layout.view_setting, null);
        this.mDialog.setContentView(mView);

        Window window = mDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        myApplication = (MyApplication) context.getApplicationContext();
        unbinder = ButterKnife.bind(this, mView);

        setViewListener();
    }

    public static SettingView getInstance(Context context) {
        return new SettingView(context);
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public void showDialog(DeviceInfoBean bean) {
        if (this.mDialog != null && !this.mDialog.isShowing()) {

            deviceBean = bean;
            initDisplay();

            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void destoryView() {
        unbinder.unbind();
    }

    /******************************view duration set******************************/
    private void setViewListener() {
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                saveSetting();
            }
        });

        mDialog.findViewById(R.id.saveTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

    }

    private void initDisplay() {
        if(null == deviceBean){
            return;
        }

        if(deviceBean.getDevice_type() == 0){
            tcpRbtn.setChecked(true);
        }else{
            udpRbtn.setChecked(true);
        }

        cbAuto.setChecked(deviceBean.isAuto());

        //从数据库加载其他配置项

    }

    private void saveSetting(){
        deviceBean.setDevice_type(tcpRbtn.isChecked() ? 0 : 1);
        deviceBean.setAuto(cbAuto.isChecked());

        //保存其他配置项到数据库

    }
}