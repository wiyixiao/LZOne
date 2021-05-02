package com.wiyixiao.lzone.net;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.core.LocalThreadPools;
import com.wiyixiao.lzone.core.PriorityExecutor;
import com.wiyixiao.lzone.core.PriorityRunnable;
import com.wiyixiao.lzone.core.PriorityType;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IClientListener;
import com.wiyixiao.lzone.utils.DataTransform;
import com.wiyixiao.lzone.utils.ModBusData;

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
    private MyApplication myApplication;

    private String mIp;
    private String mPort;

    private Socket mSocket;
    private OutputStream mOs;
    private InputStream mIs;

    private IClientListener iClientListener;

    private ConnDialog mConnDialog;

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
        this.myApplication = (MyApplication)context.getApplicationContext();
        mConnDialog = new ConnDialog(context, mContext.getResources().getString(R.string.NAL_conn_ing));
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
        LocalThreadPools.getInstance().getExecutorService().execute(connRunnable);
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mOs = null;
            mIs = null;
            mSocket = null;

            iClientListener.disConn();
            System.out.println("Lzone client close!");

            if(exit){}
        }

    }

    public void writeStr(String str, int endType){
        try {
            byte[] data = str.getBytes(StandardCharsets.UTF_8);
            writeBytes(data, endType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeBytes(byte[] hexs, int endType){
        try{
            if(mSocket == null || !mSocket.isConnected()){
                iClientListener.connNo();
                return;
            }

            if(hexs == null){
                return;
            }

            byte[] res = joinData(hexs, endType);

            if(res != null){
                write(res);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***********************************Private**********************************/
    private void write(byte[] bytes){
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

        LocalThreadPools.getInstance().getExecutorService().execute(sendRunnable);
    }

    private byte[] joinData(byte[] data, int endFlag){
        byte[] res = null;
        //拷贝终止符
        switch (endFlag){
            case Vars.StopCharType.RN:
                //0D0A
                res = new byte[data.length + 2];
                System.arraycopy(Vars.StopCharVal.RN_BYTE, 0, res, data.length, 2);
                break;
            case Vars.StopCharType.N:
                //0A
                res = new byte[data.length + 1];
                System.arraycopy(Vars.StopCharVal.N_BYTE, 0, res, data.length, 1);
                break;
            case Vars.StopCharType.CUSTOM:
                //Other//Customize
                if(TextUtils.isEmpty(myApplication.cfg.sv_stop_char_val)){
                    res = new byte[data.length];
                }else{
                    final String endStr = DataTransform.checkHexLength(myApplication.cfg.sv_stop_char_val);
                    byte[] endHex = DataTransform.hexTobytes(endStr);
                    if (endHex==null) throw new AssertionError("Object cannot be null");
                    res = new byte[data.length + endHex.length];
                    System.arraycopy(endHex, 0, res, data.length, endHex.length);
                }
                break;
            default:
                break;
        }

        if(res != null){
            System.arraycopy(data, 0, res, 0, data.length);
        }

        return res;
    }


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
