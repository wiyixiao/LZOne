package com.wiyixiao.lzone.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.wiyixiao.lzone.BuildConfig;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.adapter.DeviceAdapter;
import com.wiyixiao.lzone.bean.DeviceInfoBean;
import com.wiyixiao.lzone.bean.SettingInfoBean;
import com.wiyixiao.lzone.core.LocalThreadPools;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.db.DBManager;
import com.wiyixiao.lzone.db.DBTable;
import com.wiyixiao.lzone.utils.DisplayUtil;
import com.wiyixiao.lzone.utils.NetUtil;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;

public class DeviceActivity extends AppCompatActivity {

    @BindView(R.id.grid_layout)
    GridView cardLayout;
    @BindView(R.id.localAddrTv)
    TextView localAddrTv;

    private Unbinder unbinder;

    private MyApplication myApplication;
    private Context mContext;

    private ArrayList<DeviceInfoBean> deviceArrayList;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar);

        //??????????????????
        DisplayUtil.setCenterTitleActionBar(actionBar,
                this,
                getResources().getString(R.string.NAL_app_name),
                getResources().getDimensionPixelOffset(R.dimen.sp_22),
                Color.WHITE);

        setContentView(R.layout.activity_device);

        mContext = this.getApplicationContext();
        myApplication = (MyApplication) mContext;
        unbinder = ButterKnife.bind(this);


        //??????????????????
        myApplication.dbManager = new DBManager(mContext);
        System.out.println("**********" + myApplication.dbManager.getDbInfo() + "**********");
        //???????????????
        myApplication.settingData = new SettingInfoBean();

        //?????????????????????
        initSettingData();
        //??????????????????????????????
        initDeviceList();
        localAddrSet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();

        myApplication.dbManager.close();
        myApplication.dbManager = null;

        LocalThreadPools.getInstance().close();
    }

    //TODO ???????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.item_add:
                //??????????????????
                showDeviceCfgDialog(null);
                break;
            case R.id.item_clear:
                //?????????????????????
                deviceClear();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.grid_layout:
                //??????????????????
                Intent intent = new Intent(this, ControlActivity.class);
                //??????????????????
                intent.putExtra(Constants.DEVICE_INTENT_NAME, new Gson().toJson(deviceArrayList.get(position)));
                startActivityForResult(intent, Constants.REQUEST_CONTROL_BACK);
                break;
            default:
                break;
        }
    }

    @OnItemLongClick({R.id.grid_layout})
    public void onLongClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.grid_layout:
                //????????????????????????
                showDeviceCfgDialog(deviceArrayList.get(position));
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CONTROL_BACK:
                String device_json = data.getStringExtra(Constants.DEVICE_INTENT_NAME);
                if (BuildConfig.DEBUG) {
                    Log.e(myApplication.getTAG(), device_json);
                }
                deviceUpdate(device_json);
                localAddrSet();
                break;
            default:
                break;
        }

    }

    private void initSettingData() {
        //????????????????????????????????????
        if (BuildConfig.DEBUG) {
            myApplication.settingData.setEnd_symbol_str("\r\n");
            myApplication.settingData.setShow_send(true);
            myApplication.settingData.setShow_time(true);
            myApplication.settingData.setRev_type(Vars.DataType.ASCII);
            return;
        }
        ;
    }

    private void initDeviceList() {
        deviceArrayList = new ArrayList<DeviceInfoBean>();

        deviceAdapter = new DeviceAdapter(mContext, deviceArrayList);
        cardLayout.setAdapter(deviceAdapter);
        cardLayout.setSelection(0);

        if(myApplication.dbManager.getTableDataCount(DBTable.TableName.device) == 0){
            System.out.println("********** no device in DB **********");
        }else{
            //????????????????????????
            deviceArrayList.addAll(myApplication.dbManager.getDevices());
        }
    }

    private void showDeviceCfgDialog(DeviceInfoBean bean) {
        //???????????????????????????
        LayoutInflater factory = LayoutInflater.from(this);
        //???sname.xml????????????????????????
        final View dialogView = factory.inflate(R.layout.item_device_info, null);

        final RadioButton btn_tcp = dialogView.findViewById(R.id.rbtn_tcp);
        final RadioButton btn_udp = dialogView.findViewById(R.id.rbtn_udp);
        final CheckBox checkBox_auto = dialogView.findViewById(R.id.checkbox_auto);
        final EditText et_ip = dialogView.findViewById(R.id.edit_ip);
        final EditText et_port = dialogView.findViewById(R.id.edit_port);
        final Button btn_save = dialogView.findViewById(R.id.btn_save);
        final Button btn_del = dialogView.findViewById(R.id.btn_del);

        if (bean != null) {
            if (bean.getDevice_type() == 0) {
                btn_tcp.setChecked(true);
            } else {
                btn_udp.setChecked(true);
            }

            checkBox_auto.setChecked(bean.isAuto());

            et_ip.setText(bean.getDevice_ip());
            et_port.setText(bean.getDevice_port());
        }

        AlertDialog saveDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.NAL_device_cfg))
                //??????????????????
                .setView(dialogView)
                //???????????????
                .show();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ip = et_ip.getText().toString();
                final String port = et_port.getText().toString();
                final int type = btn_tcp.isChecked() ? 0 : 1;
                final boolean auto = checkBox_auto.isChecked();

                //????????????????????????
                if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_input_empty));
                    return;
                }

                //??????IP????????????????????????
                if (!NetUtil.isIp(ip)) {
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_device_iperr));
                    return;
                }

                DeviceInfoBean tempBean = new DeviceInfoBean(ip, port, type, auto, R.drawable.logo);

                //??????????????????????????????????????????????????????
                boolean have = false;
                int index = -1;
                for (DeviceInfoBean b : deviceArrayList
                ) {
                    if (b.getDevice_ip().equals(ip)) {
                        have = true;
                        if (!b.getDevice_port().equals(port) || b.getDevice_type() != type || b.isAuto() != auto) {
                            index = deviceArrayList.indexOf(b);
                        }
                        break;
                    }
                }

                if (!have) {
                    //????????????????????????
                    deviceArrayList.add(tempBean);
                } else if (index < 0) {
                    //????????????????????????????????????
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_device_added));
                    return;
                }

                if (index >= 0) {
                    //?????????????????????????????????
                    tempBean = deviceArrayList.get(index);
                    tempBean.setDevice_port(port);
                    tempBean.setDevice_type(type);
                    tempBean.setAuto(auto);
                }

                //????????????????????????
                deviceSet(tempBean);
                saveDialog.dismiss();
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean != null) {
                    deviceDel(bean);
                    //??????????????????
                    saveDialog.dismiss();
                } else {
                    DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_device_invalid));
                }
            }
        });
    }

    private void deviceClear() {
        System.out.println("????????????");
        if (deviceArrayList.size() > 0) {
            deviceArrayList.clear();
            deviceAdapter.notifyDataSetChanged();
        } else {
            DisplayUtil.showMsg(mContext, getResources().getString(R.string.NAL_device_zero));
            return;
        }

        //????????????????????????????????????????????????
        myApplication.dbManager.initDbTable();
    }

    private void deviceDel(DeviceInfoBean bean) {
        deviceArrayList.remove(bean);
        deviceAdapter.notifyDataSetChanged();
        //????????????????????????
        if(deviceArrayList.size() <= 0){
            myApplication.dbManager.initDbTable();
        }
        System.out.println(String.format("????????????: %s", bean.getDevice_ip()));

        //???????????????
        int count = myApplication.dbManager.removeDevice(bean.getDevice_ip());
        //???????????????????????????
        if(myApplication.dbManager.searchData(DBTable.TableName.key, bean.getDevice_ip())){
            myApplication.dbManager.clearKeysByIp(bean.getDevice_ip());
        }
    }

    private void deviceSet(DeviceInfoBean bean) {
        deviceAdapter.notifyDataSetChanged();
        System.out.println(String.format("?????????????????????: %s", bean.getDevice_ip()));
        //???????????????
        //??????IP???????????????????????????????????????????????????????????????
        if(!myApplication.dbManager.searchData(DBTable.TableName.device, bean.getDevice_ip())){
            //??????????????????
            long id = myApplication.dbManager.insertDevice(bean);
        }else{
            //???????????????
            int count = myApplication.dbManager.updateDevice(bean);
        }
    }

    private void deviceUpdate(String json) {
        DeviceInfoBean bean = new Gson().fromJson(json, DeviceInfoBean.class);

        for (DeviceInfoBean b : deviceArrayList
        ) {
            if (b.getDevice_ip().equals(bean.getDevice_ip())) {
                b.setAuto(bean.isAuto());
                b.setDevice_type(bean.getDevice_type());

                //???????????????
                int count = myApplication.dbManager.updateDevice(b);
                break;
            }
        }

        deviceAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void localAddrSet(){

        final String addr = getlocalip();

        if(addr == null){
            localAddrTv.setText("No wifi");
        }else{
            StringBuffer sb = new StringBuffer();
            sb.append(addr).append(":").append(myApplication.udpPort);
            localAddrTv.setText(sb.toString());
        }
    }

    private String getlocalip(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if(ipAddress==0)return null;
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

}