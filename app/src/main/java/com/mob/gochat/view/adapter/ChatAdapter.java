package com.mob.gochat.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.Msg;
import com.mob.gochat.utils.SpanStringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<Msg, ChatAdapter.ChatViewHolder> {

    public ChatAdapter(List data){
        super(data);
        addItemType(Msg.FRI, R.layout.chat_list_item_fri);
        addItemType(Msg.MINE, R.layout.chat_list_item_mine);
    }

    @Override
    protected void convert(@NotNull ChatViewHolder baseViewHolder, Msg msg) {
        switch (baseViewHolder.getItemViewType()){
            case Msg.FRI:
                switch (msg.getMsgType()){
                    case Msg.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_fri, msg.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_fri,false);
                        break;
                    case Msg.PIC:
                        Glide.with(getContext())
                                .load(msg.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_fri));
                        baseViewHolder.setGone(R.id.chat_pic_item_fri,false);
                        break;
                    case Msg.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_fri,false);
                        break;
                }
                break;
            case Msg.MINE:
                switch (msg.getMsgType()){
                    case Msg.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_mine, (CharSequence) msg.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_mine,false);
                        break;
                    case Msg.PIC:
                        Glide.with(getContext())
                                .load(msg.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_mine));
                        baseViewHolder.setGone(R.id.chat_pic_item_mine,false);
                        break;
                    case Msg.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_mine,false);
                        break;
                }
                break;
        }
    }

    class ChatViewHolder extends BaseViewHolder{

        public ChatViewHolder(@NotNull View view) {
            super(view);
        }

        @NotNull
        @Override
        public BaseViewHolder setText(int viewId, @Nullable CharSequence value) {
            TextView textView = getView(viewId);
            textView.setText(SpanStringUtils.getEmotionContent(getContext(),textView,value.toString()));
            return this;
        }
    }
}