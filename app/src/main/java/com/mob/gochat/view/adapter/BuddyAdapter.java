package com.mob.gochat.view.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class BuddyAdapter extends BaseQuickAdapter<Buddy, BaseViewHolder> {
    public BuddyAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Buddy buddy) {
        if(buddy.getAvatar() == null || buddy.getAvatar().equals("")){
            baseViewHolder.setImageDrawable(R.id.buddy_avatar, getContext().getDrawable(R.drawable.buddy));
        }else{
            File file = new File(getContext().getFilesDir().getAbsolutePath() + "/pic/" + buddy.getAvatar());
            if(file.exists()){
                baseViewHolder.setImageBitmap(R.id.buddy_avatar, BitmapFactory.decodeFile(file.getPath()));
            }else{
                baseViewHolder.setImageDrawable(R.id.buddy_avatar, getContext().getDrawable(R.drawable.buddy));
            }
        }
        if(buddy.getRemarks() != null && !buddy.getRemarks().equals("")){
            baseViewHolder.setText(R.id.buddy_name, buddy.getRemarks());
        }else{
            baseViewHolder.setText(R.id.buddy_name, buddy.getName());
        }
        if(getItemPosition(buddy) == 0 && buddy.getGender() > 0){
            baseViewHolder.setGone(R.id.buddy_untreated_num, false);
            baseViewHolder.setText(R.id.buddy_untreated_num, buddy.getGender() + "");
        }else {
            baseViewHolder.setGone(R.id.buddy_untreated_num, true);
        }
    }
}
