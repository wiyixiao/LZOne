package com.wiyixiao.lzone.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author:Think
 * Time:2021/3/6 9:31
 * Description:This is SettingView
 */
public class SettingView {

    private View mView;
    private Context mContext;
    private Dialog mDialog;

    private MyApplication myApplication;

    private LinearLayout durLayout;
    private EditText mEditText;
    private Unbinder unbinder;

    private SettingView(Context context){
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

    public static SettingView getInstance(Context context){
        return new SettingView(context);
    }

    public boolean isShowing(){
        return this.mDialog.isShowing();
    }

    public void showDialog(){
        if(this.mDialog != null && !this.mDialog.isShowing()){

            initDisplay();

            mDialog.show();
        }
    }

    public void dismissDialog(){
        if(this.mDialog != null && this.mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    public void destoryView(){
        unbinder.unbind();
    }

    /******************************view duration set******************************/
    private void setViewListener(){
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if("".equals(mEditText.getText().toString())){
//                    setDurEditTxt();
//                }else{
//                    saveDurationEdit();
//                }
//
//                //写入配置文件
//                myApplication.getCfg().writeConfig();
            }
        });

        mDialog.findViewById(R.id.saveTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

    }

//    private void setDurEditTxt(){
//        String dur = myApplication.getCfg().cfg_view_duration;
//        mEditText.setText(dur);
//        mEditText.setSelection(dur.length());
//    }
//
//    private void saveDurationEdit(){
//        String dur = mEditText.getText().toString();
//
//        if("".equals(dur)) {
//            return;
//        } else if(Float.parseFloat(dur) < myApplication.getMinDuration()) {
//            dur = myApplication.getMinDuration()+"";
//        }
//
//        myApplication.getCfg().cfg_view_duration = dur;
//        myApplication.getCfg().writeConfig();
//
//        setDurEditTxt();
//    }

    private void initDisplay(){

    }
}