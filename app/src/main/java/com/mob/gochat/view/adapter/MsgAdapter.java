package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.Msg;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MsgAdapter extends BaseQuickAdapter<Msg, BaseViewHolder> {

    public MsgAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Msg msg) {
        baseViewHolder.setText(R.id.tv_title,msg.getToId());
    }
}
