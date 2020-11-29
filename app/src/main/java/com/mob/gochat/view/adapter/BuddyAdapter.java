package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.model.BuddyModel;
import com.mob.gochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuddyAdapter extends BaseQuickAdapter<BuddyModel, BaseViewHolder> {

    public BuddyAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BuddyModel buddyModel) {
        baseViewHolder.setText(R.id.tv_buddy_user, buddyModel.getName());
    }
}
