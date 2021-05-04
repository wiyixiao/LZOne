package com.wiyixiao.lzone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wiyixiao.lzone.R;

import java.util.ArrayList;

public class ShareItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ShareFileInfo> mArrayList = new ArrayList<>();

    public class ShareFileInfo{
        public boolean isSelect;
        public String filePath;
    }

    public ShareItemAdapter(Context context, ArrayList<String> arrayList) {
        mContext = context;

        for (Object o: arrayList
             ) {
            ShareFileInfo shareFileInfo = new ShareFileInfo();
            shareFileInfo.isSelect = false;
            shareFileInfo.filePath = o.toString();

            mArrayList.add(shareFileInfo);
        }
    }

    public void setSelect(int position, boolean state){
        mArrayList.get(position).isSelect = state;
    }

    public boolean getSelect(int position){
        return mArrayList.get(position).isSelect;
    }

    public String getItemTxt(int position){
        if(mArrayList.size() > 0){
            return mArrayList.get(position).filePath;
        }
        return null;
    }

    public void removeItem(int position){
        if(mArrayList.size() > 0){
            mArrayList.remove(position);
        }
    }

    @Override
    public int getCount() {
        return mArrayList == null ? 0 : mArrayList.size();
    }

    @Override
    public ShareFileInfo getItem(int position) {
        return mArrayList == null ? null : mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = null;
        ViewHolder viewHolder = null;

        //如果缓存为空，我们生成新的布局作为1个item
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_share_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.textView1);
            viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox1);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String name = mArrayList.get(position).filePath;
        boolean select = mArrayList.get(position).isSelect;

        viewHolder.textView.setText(name.replace("\n", ""));
        viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        viewHolder.checkBox.setChecked(select);

        return convertView;
    }

    private class ViewHolder {
        CheckBox checkBox;
        TextView textView;
    }

}
