package com.wiyixiao.lzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wiyixiao.lzone.utils.DisplayUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        switch (id){
            case R.id.item_tcp:
                DisplayUtils.showMsg(mContext, "tcp");
                break;
            case R.id.item_udp:
                break;
            case R.id.item_help:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}