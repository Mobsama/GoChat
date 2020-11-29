package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MsgAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MsgAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_title,s);
    }
}
