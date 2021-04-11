package com.wiyixiao.lzone.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;

public abstract class KeysAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements LoadMoreModule {

    public KeysAdapter(int layoutResId, @Nullable List<T> data) {
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
