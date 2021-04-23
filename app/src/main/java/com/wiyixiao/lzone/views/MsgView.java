package com.wiyixiao.lzone.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wiyixiao.lzone.MyApplication;
import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.adapter.MyAdapter;
import com.wiyixiao.lzone.bean.MsgBean;
import com.wiyixiao.lzone.data.Vars;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author:Think
 * Time:2021/4/18 22:28
 * Description:This is MsgView
 */
public class MsgView extends LinearLayout {


    @BindView(R.id.recycleViewMsg)
    RecyclerView msgRv;

    private Context mContext;
    private MyApplication myApplication;
    private Unbinder unbinder;

    private MyAdapter<MsgBean> msgAdapter;
    private ArrayList<MsgBean> msgArrayList;

    private int msgRvH = 0;

    public MsgView(Context context) {
        super(context);

        myApplication = (MyApplication) context.getApplicationContext();
        initView();
    }

    public MsgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        myApplication = (MyApplication) context.getApplicationContext();

        initView();
    }

    private void initView() {
        View inflate = inflate(getContext(), R.layout.view_msg, this);
        unbinder = ButterKnife.bind(this, inflate);

        msgRv.post(new Runnable() {
            @Override
            public void run() {
                int height = msgRv.getMeasuredHeight();
                int width = msgRv.getMeasuredWidth();

                Log.e(myApplication.getTAG(), String.format("width, height: %s, %s", width, height));
                viewSet(width, height);
            }
        });


    }

    private void viewSet(int width, int height){
        msgArrayList = new ArrayList<>();
        msgRvH = height;

        msgAdapter = new MyAdapter<MsgBean>(R.layout.item_msg, msgArrayList) {
            @Override
            public void setViewData(BaseViewHolder holder, MsgBean item, int position) {
                TextView textView = holder.getView(R.id.msg_tv);
                textView.setText(item.getMsg_txt());
                textView.setTextColor(Vars.MsgType.COLOR[item.getMsg_type()]);
            }

            @Override
            public void setEvent(BaseViewHolder holder, MsgBean item, int position) {

            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        msgRv.setLayoutManager(layoutManager);
        msgRv.setAdapter(msgAdapter);
    }

    public void add(String data, int type){

        if(data == null || "".equals(data)){
            return;
        }

        MsgBean bean = new MsgBean();
        bean.setMsg_txt(data);
        bean.setMsg_type(type);

        msgArrayList.add(bean);

        int pos = msgArrayList.size()-1;

        /*
        LinearLayout layout = (LinearLayout) Objects.requireNonNull(msgRv.getLayoutManager()).getChildAt(0);
        int count = 0;
        if(layout != null){
            count = (int) (msgRvH / layout.getChildAt(0).getHeight()) + 1;
        }
        msgAdapter.notifyItemRangeChanged(Math.max(0, pos-count), count);
        */

        msgAdapter.notifyDataSetChanged();
        msgRv.scrollToPosition(pos);
    }

    public void clear(){
        if(msgArrayList.size() > 0){
            msgArrayList.clear();
            msgAdapter.notifyDataSetChanged();
        }
    }

    public void share(){

    }

    public void close() {
        unbinder.unbind();
    }


}
