package com.wiyixiao.lzone.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * Author:Think
 * Time:2021/4/18 22:33
 * Description:This is BaseAdapter
 */
public abstract class MyAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements LoadMoreModule {

    public MyAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        setViewData(helper, item, helper.getLayoutPosition());
        setEvent(helper, item, helper.getLayoutPosition());
    }

    public abstract void setViewData(BaseViewHolder holder, T item, int position);

    public abstract void setEvent(BaseViewHolder holder, T item, int position);

}
