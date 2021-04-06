package com.wiyixiao.lzone.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author:Think
 * Time:2021/3/4 17:46
 * Description:This is DisplayUtils
 */
public class DisplayUtils {

    /**
     * @Desc: 设置ActionBar标题居中
     * @Author: Aries.hu
     * @Date: 2021/3/4 17:50
     */
    public static void setCenterTitleActionBar(ActionBar actionBar, Context context, String title, int fs, int color){
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fs);
        textView.setTextColor(color);

        LinearLayout actionbarLayout = new LinearLayout(context);
        actionBar.setCustomView(actionbarLayout,new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams mP = (ActionBar.LayoutParams) actionbarLayout.getLayoutParams();
        mP.gravity = mP.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK| Gravity.CENTER_HORIZONTAL;
        actionbarLayout.addView(textView);
        actionBar.setCustomView(actionbarLayout, mP);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context
     * @param pxValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void saveBitmap(Bitmap bm, String filepath) {

//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//
//        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bm);
//        v.draw(canvas);

        File f = new File(filepath);
        if (f.exists()) {
            boolean save = f.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void showMsg(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
