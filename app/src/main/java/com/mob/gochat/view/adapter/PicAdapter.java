package com.mob.gochat.view.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.PicModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PicAdapter extends BaseQuickAdapter<PicModel, BaseViewHolder> {

    public PicAdapter(int layoutResId, @Nullable List<PicModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PicModel picModel) {
        Glide.with(getContext())
                .load(picModel.getPicPath())
                .centerCrop()
                .placeholder(R.mipmap.ic_placeholder)
                .into((ImageView) baseViewHolder.getView(R.id.pic_item_iv));
        if(picModel.isChoose()){
            baseViewHolder.setVisible(R.id.pic_item_choose,true);
        }else{
            baseViewHolder.setVisible(R.id.pic_item_choose,false);
        }
    }
}
