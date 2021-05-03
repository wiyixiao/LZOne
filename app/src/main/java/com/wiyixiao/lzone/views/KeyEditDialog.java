package com.wiyixiao.lzone.views;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.bean.KeyInfoBean;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IKeyEditListener;
import com.wiyixiao.lzone.utils.DataTransform;
import com.wiyixiao.lzone.utils.DisplayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author:Think
 * Time:2021/4/15 9:11
 * Description:This is KeyEditView
 */
public class KeyEditDialog {


    @BindView(R.id.ascii_btn)
    RadioButton asciiBtn;
    @BindView(R.id.hex_btn)
    RadioButton hexBtn;
    @BindView(R.id.key_name_edit)
    EditText keyNameEdit;
    @BindView(R.id.key_click_txt_edit)
    EditText keyClickTxtEdit;
    @BindView(R.id.key_lclick_txt_edit)
    EditText keyLclickTxtEdit;
    @BindView(R.id.key_release_txt_edit)
    EditText keyReleaseTxtEdit;
    @BindView(R.id.key_time_edit)
    EditText keyTimeEdit;
    @BindView(R.id.btn_del)
    Button btnDel;
    @BindView(R.id.btn_save)
    Button btnSave;

    private View mView;
    private Context mContext;
    private MyApplication myApplication;
    private Dialog mDialog;

    private Unbinder unbinder;

    private KeyInfoBean keyInfoBean;
    private IKeyEditListener listener;

    private KeyEditDialog(Context context, IKeyEditListener listener) {
        this.mContext = context;
        this.myApplication = (MyApplication)context.getApplicationContext();
        this.listener = listener;
        this.mDialog = new Dialog(context, R.style.CustomDialog);
        this.mDialog.setTitle(context.getResources().getString(R.string.NAL_key_cfg));
        this.mView = View.inflate(context, R.layout.item_key_info, null);
        this.mDialog.setContentView(mView);

        Window window = mDialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        unbinder = ButterKnife.bind(this, mView);

    }

    public static KeyEditDialog getInstance(Context context, IKeyEditListener listener) {
        return new KeyEditDialog(context, listener);
    }

    public void showDialog(KeyInfoBean bean) {
        if (this.mDialog != null && !this.mDialog.isShowing()) {

            if(bean != null){
                keyInfoBean = bean;

                if(bean.getType() == 0){
                    asciiBtn.setChecked(true);
                }else{
                    hexBtn.setChecked(true);
                }
                keyNameEdit.setText(bean.getName());
                keyClickTxtEdit.setText(bean.getTxt_click());
                keyLclickTxtEdit.setText(bean.getTxt_lclick());
                keyReleaseTxtEdit.setText(bean.getTxt_release());
                keyTimeEdit.setText(bean.getTime());
            }else{
                keyInfoBean = new KeyInfoBean();
            }

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

    @OnClick({R.id.btn_del, R.id.btn_save, R.id.ascii_btn, R.id.hex_btn})
    protected void onClick(View v){
        switch (v.getId()){
            case R.id.btn_del:
                listener.remove(keyInfoBean);
                break;
            case R.id.btn_save:
                final String name = keyNameEdit.getText().toString();

                if(TextUtils.isEmpty(name)){
                    DisplayUtil.showMsg(mContext, mContext.getResources().getString(R.string.NAL_input_empty));
                    return;
                }

                keyInfoBean.setType(asciiBtn.isChecked() ? 0 : 1);
                keyInfoBean.setName(name);

                //检测当前模式为ASCII OR HEX
                if(asciiBtn.isChecked()){
                    keyInfoBean.setTxt_click(keyClickTxtEdit.getText().toString());
                    keyInfoBean.setTxt_lclick(keyLclickTxtEdit.getText().toString());
                    keyInfoBean.setTxt_release(keyReleaseTxtEdit.getText().toString());
                }else{
                    keyInfoBean.setTxt_click(DataTransform.checkHexLength(keyClickTxtEdit.getText().toString()));
                    keyInfoBean.setTxt_lclick(DataTransform.checkHexLength(keyLclickTxtEdit.getText().toString()));
                    keyInfoBean.setTxt_release(DataTransform.checkHexLength(keyReleaseTxtEdit.getText().toString()));
                }

                keyInfoBean.setTime(keyTimeEdit.getText().toString());

                listener.add(keyInfoBean);
                break;
            case R.id.ascii_btn:
                initEdit(Vars.DataType.ASCII);
                break;
            case R.id.hex_btn:
                initEdit(Vars.DataType.HEX);
                break;
            default:
                break;
        }
    }

    private void initEdit(int dataType){
        keyClickTxtEdit.setText("");
        keyLclickTxtEdit.setText("");
        keyReleaseTxtEdit.setText("");

        if(dataType == Vars.DataType.ASCII){
            keyClickTxtEdit.setFilters(myApplication.cmdAsciiFilter);
            keyLclickTxtEdit.setFilters(myApplication.cmdAsciiFilter);
            keyReleaseTxtEdit.setFilters(myApplication.cmdAsciiFilter);
        }else {
            keyClickTxtEdit.setFilters(myApplication.cmdHexFilter);
            keyLclickTxtEdit.setFilters(myApplication.cmdHexFilter);
            keyReleaseTxtEdit.setFilters(myApplication.cmdHexFilter);
        }
    }

}
