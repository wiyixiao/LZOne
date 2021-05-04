package com.wiyixiao.lzone.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;
import com.wiyixiao.lzone.interfaces.IZipStateCallback;

import java.io.File;
import java.util.ArrayList;

public class ZipHelper {

    public static void zip(final Context context, String path, ArrayList<String> list, final IZipStateCallback stateCallback) {

        if(path == null || list.size() <= 0) {
            return;
        }

        ArrayList<File> files = new ArrayList<>();

        for (String s: list
        ) {
            files.add(new File(s));
        }

        ZipManager.zip(files, path, new IZipCallback() {
            @Override
            public void onStart() {
                loadingShow(context,-1);
            }

            @Override
            public void onProgress(int percentDone) {
                loadingShow(context, percentDone);
            }

            @Override
            public void onFinish(boolean success) {
                loadingHide();

                if(success) {
                    stateCallback.onSuccess();
                } else {
                    stateCallback.onFailed();
                }
            }
        });
    }

    public void unZip(final Context context, String unzip_file_path, String dir_zip_path, final IZipStateCallback stateCallback) {
        ZipManager.unzip(unzip_file_path, dir_zip_path, new IZipCallback() {
            @Override
            public void onStart() {
                loadingShow(context,-1);
            }

            @Override
            public void onProgress(int percentDone) {
                loadingShow(context, percentDone);
            }

            @Override
            public void onFinish(boolean success) {
                loadingHide();
                if(success) {
                    stateCallback.onSuccess();
                } else {
                    stateCallback.onFailed();
                }
            }
        });
    }

    ///------Progress Loading
    private static ProgressDialog mLoading;

    private static void loadingShow(Context context, int percent) {
        if (mLoading == null) {
            mLoading=  new ProgressDialog(context);
            mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mLoading.setMax(100);
        }
        if (percent > 0) {
            mLoading.setProgress(percent);
            mLoading.setMessage(percent + "%");
        }
        if (!mLoading.isShowing()) {
            mLoading.show();
        }
    }

    private static void loadingHide() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
    }

    ///------Toast
    private static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
