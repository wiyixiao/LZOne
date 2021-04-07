package com.wiyixiao.lzone.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiyixiao.lzone.R;
import com.wiyixiao.lzone.bean.DeviceInfoBean;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

public class DeviceAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DeviceInfoBean> mArrayList = null;

    public DeviceAdapter(Context context, ArrayList<DeviceInfoBean> arrayList) {
        mContext = context;
        mArrayList = arrayList;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mArrayList == null ? 0 : mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList == null ? null : mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        DeviceInfoBean mBean = (DeviceInfoBean) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_components, parent, false);

            convertView.getBackground().setAlpha(0);
//            convertView.setTranslationZ(10);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.ip_tv = (TextView) convertView.findViewById(R.id.ip_tv);
            holder.port_tv = (TextView) convertView.findViewById(R.id.port_tv);
            holder.type_tv = (TextView) convertView.findViewById(R.id.type_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(mBean.getImageId());
        holder.ip_tv.setText(mBean.getDevice_ip());
        holder.port_tv.setText(mBean.getDevice_port());
        holder.type_tv.setText(mBean.getDevice_type() == 0 ? "TCP" : "UDP");
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView ip_tv;
        TextView port_tv;
        TextView type_tv;
    }
}
