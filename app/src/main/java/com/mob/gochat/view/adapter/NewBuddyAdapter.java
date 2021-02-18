package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.Request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NewBuddyAdapter extends BaseQuickAdapter<Request, BaseViewHolder> {

    public NewBuddyAdapter(int layoutResId, @Nullable List<Request> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Request request) {
        baseViewHolder.setText(R.id.new_buddy_name, request.getBuddyName());
        if(request.getIsTreated() == Request.UNTREATED){
            baseViewHolder.setGone(R.id.new_buddy_btn_group, false);
            baseViewHolder.setGone(R.id.new_buddy_tip, true);
        }else {
            baseViewHolder.setGone(R.id.new_buddy_btn_group, true);
            baseViewHolder.setGone(R.id.new_buddy_tip, false);
            if(request.getIsTreated() == Request.REJECTED){
                //拒绝
                baseViewHolder.setText(R.id.new_buddy_tip, "已拒绝");
            }else if(request.getIsTreated() == Request.APPROVED){
                //同意
                baseViewHolder.setText(R.id.new_buddy_tip, "已同意");
            }
        }
    }
}
