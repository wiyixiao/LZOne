package com.wiyixiao.lzone;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.utils.DisplayUtil;
import com.wiyixiao.lzone.utils.PackageUtil;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
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
        DisplayUtil.setCenterTitleActionBar(actionBar,
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
        versionInfoTv.setText(String.format("V%s", PackageUtil.getVersionName(this)));
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