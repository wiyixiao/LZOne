package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;

import com.wiyixiao.lzone.adapter.DeviceAdapter;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.db.DBManager;
import com.wiyixiao.lzone.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.grid_layout)
    GridView cardLayout;

    private Unbinder unbinder;

    private MyApplication myApplication;
    private Context mContext;
    private DBManager dbManager;

    private ArrayList<DeviceInfoBean> deviceArrayList;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar);

        //设置标题居中
        DisplayUtils.setCenterTitleActionBar(actionBar,
                this,
                getResources().getString(R.string.NAL_app_name),
                getResources().getDimensionPixelOffset(R.dimen.sp_22),
                Color.WHITE);

        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();
        myApplication = (MyApplication)mContext;
        unbinder = ButterKnife.bind(this);


        //初始化数据库
        dbManager = new DBManager(mContext);

        //初始化已连接设备列表
        initDeviceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }

    //TODO 状态栏按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id){
            case R.id.item_add:
                //添加新的设备
                showDeviceCfgDialog(null);
                break;
            case R.id.item_help:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnItemClick({R.id.grid_layout})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        switch (parent.getId()){
            case R.id.grid_layout:
                break;
            default:
                break;
        }
    }

    @OnItemLongClick({R.id.grid_layout})
    public void onLongClick(AdapterView<?> parent, View view, int position, long id){
        switch (parent.getId()){
            case R.id.grid_layout:
                //长按弹窗进行修改
                showDeviceCfgDialog(deviceArrayList.get(position));
                break;
            default:
                break;
        }
    }

    private void initDeviceList(){
        deviceArrayList = new ArrayList<DeviceInfoBean>();

        deviceAdapter = new DeviceAdapter(mContext, deviceArrayList);
        cardLayout.setAdapter(deviceAdapter);
        cardLayout.setSelection(0);
    }

    private void showDeviceCfgDialog(DeviceInfoBean bean){
        //图层模板生成器句柄
        LayoutInflater factory = LayoutInflater.from(this);
        //用sname.xml模板生成视图模板
        final View dialogView = factory.inflate(R.layout.item_device_info, null);

        final RadioButton btn_tcp = dialogView.findViewById(R.id.rbtn_tcp);
        final RadioButton btn_udp = dialogView.findViewById(R.id.rbtn_udp);
        final CheckBox checkBox_auto = dialogView.findViewById(R.id.checkbox_auto);
        final EditText et_ip = dialogView.findViewById(R.id.edit_ip);
        final EditText et_port = dialogView.findViewById(R.id.edit_port);
        final Button btn_save = dialogView.findViewById(R.id.btn_save);
        final Button btn_del = dialogView.findViewById(R.id.btn_del);

        if(bean != null){
            if(bean.getDevice_type() == 0){
                btn_tcp.setChecked(true);
            } else{
                btn_udp.setChecked(true);
            }

            checkBox_auto.setChecked(bean.isAuto());

            et_ip.setText(bean.getDevice_ip());
            et_port.setText(bean.getDevice_port());
        }

        android.app.AlertDialog saveDialog =  new android.app.AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.NAL_device_cfg))
                //设置视图模板
                .setView(dialogView)
                //显示对话框
                .show();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ip = et_ip.getText().toString();
                final String port = et_port.getText().toString();
                final int type = btn_tcp.isChecked() ? 0 : 1;
                final boolean auto = checkBox_auto.isChecked();

                if(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){
                    DisplayUtils.showMsg(mContext, "数值不能为空!");
                    return;
                }

                DeviceInfoBean tempBean = new DeviceInfoBean(ip, port, type, auto, R.drawable.logo);

                //判断设备是否存在，进行添加或更新配置
                boolean have = false;
                int index = -1;
                for (DeviceInfoBean b: deviceArrayList
                     ) {
                    if(b.getDevice_ip().equals(ip)){
                        have = true;
                        if(!b.getDevice_port().equals(port) || b.getDevice_type() != type || b.isAuto() != auto){
                            index = deviceArrayList.indexOf(b);
                        }
                        break;
                    }
                }

                if(!have){
                    //未找到设备，添加
                    deviceArrayList.add(tempBean);
                }else{
                    DisplayUtils.showMsg(mContext, "设备已添加");
                }

                if(index >= 0){
                    //找到设备，更新配置
                    DeviceInfoBean bean1 = deviceArrayList.get(index);
                    bean1.setDevice_port(port);
                    bean1.setDevice_type(type);
                    bean1.setAuto(auto);
                }

                //保存数据到数据库
                deviceSet();

                saveDialog.dismiss();
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean != null){
                    deviceDel(bean);
                    //取消关闭弹窗
                    saveDialog.dismiss();
                }else{
                    DisplayUtils.showMsg(mContext, "无效操作");
                }
            }
        });
    }

    private DeviceInfoBean findDevice(String ip){
        for (DeviceInfoBean b: deviceArrayList
             ) {
            if(b.getDevice_ip().equals(ip));
            return b;
        }

        return null;
    }


    private void deviceDel(DeviceInfoBean bean){
        deviceArrayList.remove(bean);
        deviceAdapter.notifyDataSetChanged();

        //更新数据库

    }

    private void deviceSet()
    {
        deviceAdapter.notifyDataSetChanged();

        //更新数据库

    }

}