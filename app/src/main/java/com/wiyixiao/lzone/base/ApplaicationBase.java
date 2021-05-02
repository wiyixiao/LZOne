package com.wiyixiao.lzone.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.wiyixiao.lzone.utils.PackageUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

import xcrash.ICrashCallback;
import xcrash.TombstoneManager;
import xcrash.TombstoneParser;
import xcrash.XCrash;

public class ApplaicationBase extends Application {
    protected final static String TAG = "com.wiyixiao.app";

    // callback for java crash, native crash and ANR
    ICrashCallback callback = new ICrashCallback() {
        @Override
        public void onCrash(String logPath, String emergency) {
            Log.d(TAG, "log path: " + (logPath != null ? logPath : "(null)") + ", emergency: " + (emergency != null ? emergency : "(null)"));

            if (emergency != null) {
                debug(logPath, emergency);

                // Disk is exhausted, send crash report immediately.
                sendThenDeleteCrashLog(logPath, emergency);
            } else {
                // Add some expanded sections. Send crash report at the next time APP startup.

                // OK
                TombstoneManager.appendSection(logPath, "expanded_key_1", "expanded_content");
                TombstoneManager.appendSection(logPath, "expanded_key_2", "expanded_content_row_1\nexpanded_content_row_2");

                // Invalid. (Do NOT include multiple consecutive newline characters ("\n\n") in the content string.)
                // TombstoneManager.appendSection(logPath, "expanded_key_3", "expanded_content_row_1\n\nexpanded_content_row_2");

                debug(logPath, null);
            }
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.d(TAG, "xCrash SDK init: start");

        final String version = PackageUtil.getAppName(base) + "_" + PackageUtil.getVersionName(base) + "_" + PackageUtil.getVersionCode(base);
//        final String crashDir = FileUtil.getStoragePath(base, false) + logDir;
        //LogDir:/storage/emulated/0/Android/data/com.linkzill.BTtest.debug/files/xcrash

        Log.d(TAG, "Version: " + version);

        XCrash.init(this, new XCrash.InitParameters()
                .setAppVersion(version)
                .setJavaRethrow(true)
                .setJavaLogCountMax(10)
                .setJavaDumpAllThreadsWhiteList(new String[]{"^main$", "^Binder:.*", ".*Finalizer.*"})
                .setJavaDumpAllThreadsCountMax(10)
                .setJavaCallback(callback)
                .setNativeRethrow(true)
                .setNativeLogCountMax(10)
                .setNativeDumpAllThreadsWhiteList(new String[]{"^xcrash\\.sample$", "^Signal Catcher$", "^Jit thread pool$", ".*(R|r)ender.*", ".*Chrome.*"})
                .setNativeDumpAllThreadsCountMax(10)
                .setNativeCallback(callback)
                .setAnrRethrow(true)
                .setAnrLogCountMax(10)
                .setAnrCallback(callback)
                .setPlaceholderCountMax(3)
                .setPlaceholderSizeKb(512)
                .setLogDir(Objects.requireNonNull(getExternalFilesDir("xcrash")).toString())
                .setLogFileMaintainDelayMs(1000));

        Log.d(TAG, XCrash.getLogDir());
        Log.d(TAG, "xCrash SDK init: end");
    }

    private void sendThenDeleteCrashLog(String logPath, String emergency) {
        // Parse
        //Map<String, String> map = TombstoneParser.parse(logPath, emergency);
        //String crashReport = new JSONObject(map).toString();

        // Send the crash report to server-side.
        // ......

        // If the server-side receives successfully, delete the log file.
        //
        // Note: When you use the placeholder file feature,
        //       please always use this method to delete tombstone files.
        //
        //TombstoneManager.deleteTombstone(logPath);
    }

    private void debug(String logPath, String emergency) {
        // Parse and save the crash info to a JSON file for debugging.
        FileWriter writer = null;
        try {
            File debug = new File(XCrash.getLogDir() + "/debug.json");
            debug.createNewFile();
            writer = new FileWriter(debug, false);
            writer.write(new JSONObject(TombstoneParser.parse(logPath, emergency)).toString());
        } catch (Exception e) {
            Log.d(TAG, "debug failed", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
