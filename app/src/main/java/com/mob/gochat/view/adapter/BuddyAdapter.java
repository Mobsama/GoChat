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
            baseViewHolder.setText(R.id.new_buddy_name, buddy.getRemarks());
        }else{
            baseViewHolder.setText(R.id.new_buddy_name, buddy.getName());
        }
        if(getItemPosition(buddy) == 0 && buddy.getGender() > 0){
            baseViewHolder.setGone(R.id.new_buddy_untreated_num, false);
            baseViewHolder.setText(R.id.new_buddy_untreated_num, buddy.getGender() + "");
        }else {
            baseViewHolder.setGone(R.id.new_buddy_untreated_num, true);
        }

    }
}
