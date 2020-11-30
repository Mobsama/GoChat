package com.mob.gochat.view.adapter;

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
                baseViewHolder.setText(R.id.chat_tv_item_fir, msgModel.getMsg());
                break;
            case MsgModel.MINE:
                baseViewHolder.setText(R.id.chat_tv_item_mine, msgModel.getMsg());
                break;
        }
    }
}