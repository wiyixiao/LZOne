package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.wiyixiao.lzone.adapter.GridItemAdapter;
import com.wiyixiao.lzone.bean.IconItemBean;
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

    private ArrayList<IconItemBean> cardArrayList;
    private GridItemAdapter cardAdapter;

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
            case R.id.item_tcp:
                showDeviceCfgDialog();
                break;
            case R.id.item_udp:
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
                DisplayUtils.showMsg(mContext, position + " click");
                break;
            default:
                break;
        }
    }

    @OnItemLongClick({R.id.grid_layout})
    public void onLongClick(AdapterView<?> parent, View view, int position, long id){
        switch (parent.getId()){
            case R.id.grid_layout:
            {
                DisplayUtils.showMsg(mContext, position + " longclick");
            }
                break;
            default:
                break;
        }
    }

    private void initDeviceList(){
        cardArrayList = new ArrayList<IconItemBean>();

        cardAdapter = new GridItemAdapter(mContext, cardArrayList);
        cardLayout.setAdapter(cardAdapter);
        cardLayout.setSelection(0);

        StringBuilder builder = new StringBuilder();
        builder.append("8080").append("\n");
        builder.append("255.255.255.255").append("\n");
        builder.append("TCP");

        cardArrayList.add(new IconItemBean(builder.toString(), R.drawable.logo));
        cardArrayList.add(new IconItemBean(builder.toString(), R.drawable.logo));
        cardArrayList.add(new IconItemBean(builder.toString(), R.drawable.logo));
        cardArrayList.add(new IconItemBean(builder.toString(), R.drawable.logo));
        cardAdapter.notifyDataSetChanged();
    }

    private void showDeviceCfgDialog(){
        //图层模板生成器句柄
        LayoutInflater factory = LayoutInflater.from(this);
        //用sname.xml模板生成视图模板
        final View dialogView = factory.inflate(R.layout.item_device_info, null);
        android.app.AlertDialog saveDialog =  new android.app.AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.NAL_device_cfg))
                //设置视图模板
                .setView(dialogView)
                //显示对话框
                .show();
    }

}