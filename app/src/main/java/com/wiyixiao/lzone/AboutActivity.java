package com.wiyixiao.lzone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.utils.DisplayUtils;
import com.wiyixiao.lzone.utils.PackageUtils;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version_info_tv)
    TextView versionInfoTv;
    @BindView(R.id.about_info_tv)
    TextView aboutInfoTv;

    private MyApplication myApplication;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        //设置标题居中
        DisplayUtils.setCenterTitleActionBar(actionBar,
                                    this,
                                            getResources().getString(R.string.NAL_app_about),
                                            getResources().getDimensionPixelOffset(R.dimen.sp_22),
                                            Color.WHITE);
        setContentView(R.layout.activity_about);

        myApplication = (MyApplication)this.getApplicationContext();
        unbinder = ButterKnife.bind(this);

        myApplication.intentType = Vars.IntentType.ABOUT;

        //设置App版本
        Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        versionInfoTv.setTypeface(tfRegular);
        versionInfoTv.setText(String.format("V%s", PackageUtils.getVersionName(this)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销绑定
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}