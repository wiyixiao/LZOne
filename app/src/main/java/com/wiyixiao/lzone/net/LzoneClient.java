package com.wiyixiao.lzone.net;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.core.PriorityExecutor;
import com.wiyixiao.lzone.core.PriorityRunnable;
import com.wiyixiao.lzone.core.PriorityType;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.interfaces.IClientListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * Author:Think
 * Time:2021/4/24 23:32
 * Description:This is LzoneClient
 */
public class LzoneClient {

    private static final int timeOut = 8000;

    private Context mContext;

    private String mIp;
    private String mPort;

    private Socket mSocket;
    private OutputStream mOs;
    private InputStream mIs;

    private IClientListener iClientListener;

    private ConnDialog mConnDialog;

    /**
     * @Desc: 线程池
     */
    private ExecutorService executorService = new PriorityExecutor(5, false);

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case Constants.NET_CONN_SUCCESS:
                    iClientListener.connSuccess();
                    break;
                case Constants.NET_CONN_FAILED:
                    iClientListener.connFailed();
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    /**********************************Get/Set***********************************/
    public String getmIp() {
        return mIp;
    }

    public String getmPort() {
        return mPort;
    }

    public void setiClientListener(IClientListener listener){
        this.iClientListener = listener;
    }

    public boolean isConn(){
        if(mSocket != null){
            return mSocket.isConnected();
        }

        return false;
    }

    /***********************************Public***********************************/
    public LzoneClient(Context context) {
        this.mContext = context;
        mConnDialog = new ConnDialog(context, "连接中...");
    }

    public void connect(String ip, String port){
        this.mIp = ip;
        this.mPort = port;

        //显示连接弹窗
        if(!mConnDialog.isShowing()){
            mConnDialog.show();
        }

        PriorityRunnable connRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
            @Override
            public void run() {
                mSocket = null;
                Message msg = new Message();
                try{
                    //创建socket连接
                    mSocket = new Socket();
                    SocketAddress socAddress = new InetSocketAddress(mIp, Integer.parseInt(mPort));
                    mSocket.connect(socAddress, timeOut);
                    if(mSocket.isConnected()){
                        //设置读取超时时间
                        mSocket.setSoTimeout(timeOut);

                        //设置输入输出流
                        mOs = mSocket.getOutputStream();
                        mIs= mSocket.getInputStream();

                        //连接成功
                        msg.what = Constants.NET_CONN_SUCCESS;
                    }

                    mConnDialog.dismiss();
                    handler.sendMessage(msg);
                }catch (Exception e){
                    mSocket = null;
                    mConnDialog.dismiss();
                    msg.what = Constants.NET_CONN_FAILED;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        });

        //尝试连接
        executorService.execute(connRunnable);
    }

    /**
     * @Desc: 关闭连接
     * @Author: Aries.hu
     * @Date: 2021/4/27 23:38
     * @param exit 是否退出
     */
    public void close(boolean exit){
        try{
            if(mSocket != null && mSocket.isConnected()){
                mOs.close();
                mIs.close();
                mSocket.close();

                iClientListener.disConn();
                System.out.println("Lzone client close!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mOs = null;
            mIs = null;
            mSocket = null;

            if(exit){
                executorService.shutdown();
            }
        }

    }

    public void write(String str){
        try {

            if(mSocket == null || !mSocket.isConnected()){
                iClientListener.connNo();
                return;
            }

            byte[] data = str.getBytes(StandardCharsets.UTF_8);
            write(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void write(byte[] bytes){
        PriorityRunnable sendRunnable = new PriorityRunnable(PriorityType.HIGH, new Runnable() {
            @Override
            public void run() {
                try{
                    mOs.write(bytes, 0, bytes.length);
                    mOs.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        executorService.execute(sendRunnable);
    }


    /***********************************Private**********************************/



    /***********************************Dialog***********************************/
    static class ConnDialog extends AlertDialog{

        private String msg;
        private View mView;

        protected ConnDialog(@NonNull Context context, final String msg) {
            super(context);

            this.msg = msg;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_progressbar);

            setCanceledOnTouchOutside(false);
            setCancelable(false);

            TextView tv = findViewById(R.id.tv);
            if(tv != null){
                tv.setText(msg);
            }
        }


    }


}
