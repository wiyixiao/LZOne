package com.wiyixiao.lzone.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.wiyixiao.lzone.BuildConfig;
import com.wiyixiao.lzone.activity.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.core.LocalThreadPools;
import com.wiyixiao.lzone.core.PriorityRunnable;
import com.wiyixiao.lzone.core.PriorityType;
import com.wiyixiao.lzone.data.Constants;
import com.wiyixiao.lzone.data.Vars;
import com.wiyixiao.lzone.interfaces.IClientListener;
import com.wiyixiao.lzone.utils.DataTransform;
import com.wiyixiao.lzone.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Author:Think
 * Time:2021/4/24 23:32
 * Description:This is LzoneClient
 */
public class LzoneClient {

    private static final int TCP_TIME_OUT = 8000;
    private static final int UDP_BUFF_SIZE = 1024;

    private Context mContext;
    private MyApplication myApplication;

    private String mIp;
    private String mPort;
    private int mConnType;

    private DatagramSocket mDSocket;
    private DatagramPacket mDPacketSend;
    private DatagramPacket mDPacketRev;
    private byte[] packSend = new byte[UDP_BUFF_SIZE];
    private byte[] packRev = new byte[UDP_BUFF_SIZE];

    private Socket mSocket;
    private OutputStream mOs;
    private InputStream mIs;

    private IClientListener iClientListener;

    private ConnDialog mConnDialog;

    private boolean heartBeatFlag = false;
    private boolean connSuccess = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case Constants.NET_CONN_SUCCESS:
                    connSuccess = true;
                    iClientListener.connSuccess();
                    runReceiveTask();
                    setHeatBeat(myApplication.cfg.sv_heat_beat_state);
                    break;
                case Constants.NET_CONN_FAILED:
                    iClientListener.connFailed();
                    //成功连接过，异常断开，尝试重新连接
                    if(connSuccess){
                        connect(mIp, mPort, mConnType);
                    }
                    break;
                case Constants.NET_DATA_REV:
                    Bundle b = (Bundle) msg.obj;
                    iClientListener.revCall(b.getByteArray("rev"));
                    break;
                case Constants.NET_CONN_LOST:
                    iClientListener.disConn(msg.arg1);
                    System.out.println("Lzone client close!");
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
        boolean conn = false;

        if(mSocket != null){
            conn = !mSocket.isClosed() && mSocket.isConnected() && !mSocket.isInputShutdown();
        }

        return conn;
    }

    public boolean isHeartBeatFlag() {
        return heartBeatFlag;
    }

    public boolean isConnSuccess() {
        return connSuccess;
    }

    /***********************************Public***********************************/
    public LzoneClient(Context context) {
        this.mContext = context;
        this.myApplication = (MyApplication)context.getApplicationContext();
        mConnDialog = new ConnDialog(context, mContext.getResources().getString(R.string.NAL_conn_ing));
    }

    public void connect(String ip, String port, int type){
        this.mIp = ip;
        this.mPort = port;
        this.mConnType = type;

        //显示连接弹窗
        if(!mConnDialog.isShowing()){
            mConnDialog.show();
        }

        PriorityRunnable connRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
            @Override
            public void run() {
                mSocket = null;
                mDSocket = null;
                try{
                    if(type == Vars.ConnType.TCP){
                        connTcp();
                    }else{
                        connUdp();
                    }

                    //连接成功
                    Message msg = handler.obtainMessage();
                    msg.what = Constants.NET_CONN_SUCCESS;
                    msg.sendToTarget();

                }catch (Exception e){
                    mSocket = null;
                    mDSocket = null;
                    Log.e(myApplication.getTAG(), "Connect failed!");

                    long t = Utils.getCurrTimeMillis();
                    while (Utils.getCurrTimeMillis() - t < 5000){};
                    Message msg = handler.obtainMessage();
                    msg.what = Constants.NET_CONN_FAILED;
                    msg.sendToTarget();
                }finally {
                    mConnDialog.dismiss();
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

            if(mDSocket != null && mDSocket.isBound()){
                mDSocket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mOs = null;
            mIs = null;
            mSocket = null;

            mDPacketSend = null;
            mDPacketRev = null;
            mDSocket = null;

            heartBeatFlag = false;

            Message msg = handler.obtainMessage();
            msg.what = Constants.NET_CONN_LOST;
            //0: 手动关闭 ; 1: 自动关闭(需重新连接)
            msg.arg1 = exit ? 0 : 1;
            msg.sendToTarget();

            if(exit){
                connSuccess = false;
            }
        }

    }

    public void setHeatBeat(boolean state){
        heartBeatFlag = state;

        if(state){
            heartBeatTask();
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

            if(!isConn()){
                iClientListener.connNo();
                return;
            }

            if(hexs == null){
                return;
            }

            byte[] res = joinData(hexs, endType);

            if(res != null){
                if(mConnType == Vars.ConnType.TCP){
                    write(res);
                }else{
                    pack(res);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***********************************Private**********************************/
    private void write(byte[] bytes) throws Exception{
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

    private void pack(byte[] bytes){
        PriorityRunnable packRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
            @Override
            public void run() {
                try{
                    mDPacketSend.setData(bytes);
                    mDSocket.send(mDPacketSend);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        LocalThreadPools.getInstance().getExecutorService().execute(packRunnable);
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

    private void connTcp() throws IOException {
        //创建socket连接
        mSocket = new Socket();
        SocketAddress socAddress = new InetSocketAddress(mIp, Integer.parseInt(mPort));
        mSocket.connect(socAddress, TCP_TIME_OUT);
        if(mSocket.isConnected()){
            //设置读取超时时间
            mSocket.setSoTimeout(TCP_TIME_OUT);

            //设置输入输出流
            mOs = mSocket.getOutputStream();
            mIs= mSocket.getInputStream();

            writeStr("Hello, I Lzone TCP Client", Vars.StopCharType.RN);
        }
    }

    private void connUdp() throws IOException{
        mDSocket = new DatagramSocket(myApplication.udpPort);
        SocketAddress socAddress = new InetSocketAddress(mIp, Integer.parseInt(mPort));

        byte[] data = "Hello, I Lzone UDP Client\r\n".getBytes(StandardCharsets.UTF_8);
        mDPacketSend = new DatagramPacket(packSend, UDP_BUFF_SIZE, socAddress);
        mDPacketRev = new DatagramPacket(packRev, UDP_BUFF_SIZE);

        mDPacketSend.setData(data);
        mDSocket.send(mDPacketSend);
    }

    /**
     * @Desc: 数据接收线程
     * @Author: Aries.hu
     * @Date: 2021/5/3 22:19
     */
    private void runReceiveTask(){
        PriorityRunnable revRunnable = new PriorityRunnable(PriorityType.HIGH, new Runnable() {
            @Override
            public void run() {
                try{
                    while (isConn()){
                        byte[] data = null;
                        byte[] mbyte = new byte[1];

                        if(mConnType == Vars.ConnType.TCP){
                            data = readBuffer();
                        }else{
                            data = readPack();
                        }

                        if(data == null){
                            Thread.sleep(2);
                        }else{
                            Bundle bundle =new Bundle();
                            bundle.putByteArray("rev", data);

                            Message msg = handler.obtainMessage();
                            msg.what = Constants.NET_DATA_REV;
                            msg.obj = bundle;
                            msg.sendToTarget();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(BuildConfig.DEBUG){
                        System.out.println("receiveThread Exit!");
                    }
                }
            }
        });

        LocalThreadPools.getInstance().getExecutorService().execute(revRunnable);
    }

    private void heartBeatTask(){
        System.out.println("heartBeatThread task create!");
        PriorityRunnable heartBeatRunnable = new PriorityRunnable(PriorityType.NORMAL, new Runnable() {
            @Override
            public void run() {
                long heartBeatTime = Utils.getCurrTimeMillis();
                while (heartBeatFlag && isConn()){
                    try{
                        if(Utils.getCurrTimeMillis() - heartBeatTime >= (myApplication.cfg.sv_heart_beat_time * 1000)){
                            byte[] heartBeatData = DataTransform.hexTobytes(myApplication.cfg.sv_heart_beat_data);
                            if(heartBeatData == null){
                                heartBeatData = new byte[2];
                            }
                            mOs.write(heartBeatData, 0, heartBeatData.length);
                            mOs.flush();
                            heartBeatTime = Utils.getCurrTimeMillis();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        close(false);
                        break;
                    }
                }
                System.out.println("heartBeatThread Exit!");

            }
        });

        LocalThreadPools.getInstance().getExecutorService().execute(heartBeatRunnable);
    }

    /**
     * 读取输入流
     * @return 字节数组
     * @throws IOException IO错误
     */
    private byte[] readBuffer() throws IOException{
        byte[] buf = null;
        if(mIs == null){
            return buf;
        }

        int count = mIs.available();

        if(count > 0){
            buf = new byte[count];
            int len = mIs.read(buf);
            System.out.println(Arrays.toString(buf));
        }

        return buf;
    }

    /**
     * 读取udp报文
     * @return 读取字节
     * @throws IOException IO错误
     */
    private byte[] readPack() throws IOException{
        byte[] buf = null;

        if(mDSocket == null){
            return buf;
        }

        mDSocket.receive(mDPacketRev);

        if(mDPacketRev == null || mDPacketRev.getLength() == 0){
            return buf;
        }else{
            final int len = mDPacketRev.getLength();
            buf = new byte[len];
            System.arraycopy(mDPacketRev.getData(), 0, buf, 0, len);
        }

        if (mDPacketRev != null) {
            mDPacketRev.setLength(UDP_BUFF_SIZE);
        }

        return buf;
    }

    /**
     * @Desc: 清空
     * @Author: Aries.hu
     * @Date: 2021/4/28 17:30
     */
    protected void skipBuffer() throws IOException{
        if(mIs == null){
            return;
        }

        long n = mIs.skip(mIs.available());
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
