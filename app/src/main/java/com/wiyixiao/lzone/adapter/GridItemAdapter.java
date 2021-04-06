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
import com.wiyixiao.lzone.bean.IconItemBean;
import com.wiyixiao.lzone.utils.DisplayUtils;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

public class GridItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<IconItemBean> mArrayList = null;

    public GridItemAdapter(Context context, ArrayList<IconItemBean> arrayList) {
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
        IconItemBean mBean = (IconItemBean) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_components, parent, false);

            convertView.getBackground().setAlpha(0);
//            convertView.setTranslationZ(10);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.textView = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(mBean.getImageId());
        holder.textView.setText(mBean.getIconName());
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
