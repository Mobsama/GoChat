package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.ChatModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatModel, BaseViewHolder> {

    public ChatAdapter(List data){
        super(data);
        addItemType(ChatModel.FRI, R.layout.chat_list_item_fri);
        addItemType(ChatModel.MINE, R.layout.chat_list_item_mine);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatModel chatModel) {
        switch (baseViewHolder.getItemViewType()){
            case ChatModel.FRI:
                baseViewHolder.setText(R.id.chat_tv_item_fir,chatModel.getMsg());
                break;
            case ChatModel.MINE:
                baseViewHolder.setText(R.id.chat_tv_item_mine,chatModel.getMsg());
                break;
        }
    }
}