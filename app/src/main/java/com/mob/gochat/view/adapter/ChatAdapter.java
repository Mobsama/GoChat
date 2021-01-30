package com.mob.gochat.view.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.MsgModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<MsgModel, BaseViewHolder> {

    public ChatAdapter(List data){
        super(data);
        addItemType(MsgModel.FRI, R.layout.chat_list_item_fri);
        addItemType(MsgModel.MINE, R.layout.chat_list_item_mine);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MsgModel msgModel) {
        switch (baseViewHolder.getItemViewType()){
            case MsgModel.FRI:
                switch (msgModel.getMsgType()){
                    case MsgModel.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_fri, msgModel.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_fri,false);
                        break;
                    case MsgModel.PIC:
                        Glide.with(getContext())
                                .load(msgModel.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_fri));
                        baseViewHolder.setGone(R.id.chat_pic_item_fri,false);
                        break;
                    case MsgModel.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_fri,false);
                        break;
                }
                break;
            case MsgModel.MINE:
                switch (msgModel.getMsgType()){
                    case MsgModel.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_mine, msgModel.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_mine,false);
                        break;
                    case MsgModel.PIC:
                        Glide.with(getContext())
                                .load(msgModel.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_mine));
                        baseViewHolder.setGone(R.id.chat_pic_item_mine,false);
                        break;
                    case MsgModel.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_mine,false);
                        break;
                }
                break;
        }
    }
}