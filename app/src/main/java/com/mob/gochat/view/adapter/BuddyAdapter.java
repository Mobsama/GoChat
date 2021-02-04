package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuddyAdapter extends BaseQuickAdapter<Buddy, BaseViewHolder> {

    public BuddyAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Buddy buddy) {
        if(buddy.getRemarks() != null && !buddy.getRemarks().equals("")){
            baseViewHolder.setText(R.id.tv_buddy_user, buddy.getRemarks());
        }else{
            baseViewHolder.setText(R.id.tv_buddy_user, buddy.getName());
        }

    }
}
