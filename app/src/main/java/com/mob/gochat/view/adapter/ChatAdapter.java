package com.mob.gochat.view.adapter;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.utils.SpanStringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<Msg, ChatAdapter.ChatViewHolder> {
    private Buddy buddy, user;
    public ChatAdapter(List<Msg> data, Buddy buddy, Buddy user){
        super(data);
        this.buddy = buddy;
        this.user = user;
        addItemType(Msg.FRI, R.layout.chat_list_item_fri);
        addItemType(Msg.MINE, R.layout.chat_list_item_mine);
        addItemType(Msg.OTHER, R.layout.chat_list_item_other);
    }

    @Override
    protected void convert(@NotNull ChatViewHolder baseViewHolder, Msg msg) {
        switch (baseViewHolder.getItemViewType()){
            case Msg.FRI:
                if(buddy.getAvatar() == null || buddy.getAvatar().equals("")){
                    baseViewHolder.setImageDrawable(R.id.chat_iv_avatar_fri, getContext().getDrawable(R.drawable.buddy));
                }else{
                    File file = new File(buddy.getAvatar());
                    if(file.exists()){
                        baseViewHolder.setImageBitmap(R.id.chat_iv_avatar_fri, BitmapFactory.decodeFile(buddy.getAvatar()));
                    }else{
                        baseViewHolder.setImageDrawable(R.id.chat_iv_avatar_fri, getContext().getDrawable(R.drawable.buddy));
                    }
                }
                baseViewHolder.setText(R.id.chat_tv_item_fri_time, msg.getTime());
                switch (msg.getMsgType()){
                    case Msg.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_fri, msg.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_fri,false);
                        baseViewHolder.setGone(R.id.chat_pic_item_fri,true);
                        baseViewHolder.setGone(R.id.chat_voice_item_fri,true);
                        break;
                    case Msg.PIC:
                        Glide.with(getContext())
                                .load(getContext().getFilesDir().getAbsolutePath() + "/pic/" +msg.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_fri));
                        baseViewHolder.setGone(R.id.chat_pic_item_fri,false);
                        baseViewHolder.setGone(R.id.chat_tv_item_fri,true);
                        baseViewHolder.setGone(R.id.chat_voice_item_fri,true);
                        break;
                    case Msg.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_fri,false);
                        baseViewHolder.setGone(R.id.chat_tv_item_fri,true);
                        baseViewHolder.setGone(R.id.chat_pic_item_fri,true);
                        break;
                }
                break;
            case Msg.MINE:
                if(user.getAvatar() == null || user.getAvatar().equals("")){
                    baseViewHolder.setImageDrawable(R.id.chat_iv_avatar_mine, getContext().getDrawable(R.drawable.buddy));
                }else{
                    File file = new File(user.getAvatar());
                    if(file.exists()){
                        baseViewHolder.setImageBitmap(R.id.chat_iv_avatar_mine, BitmapFactory.decodeFile(user.getAvatar()));
                    }else{
                        baseViewHolder.setImageDrawable(R.id.chat_iv_avatar_mine, getContext().getDrawable(R.drawable.buddy));
                    }
                }
                baseViewHolder.setText(R.id.chat_tv_item_mine_time, msg.getTime());
                switch (msg.getMsgType()){
                    case Msg.TEXT:
                        baseViewHolder.setText(R.id.chat_tv_item_mine, (CharSequence) msg.getMsg());
                        baseViewHolder.setGone(R.id.chat_tv_item_mine,false);
                        baseViewHolder.setGone(R.id.chat_pic_item_mine,true);
                        baseViewHolder.setGone(R.id.chat_voice_item_mine,true);
                        break;
                    case Msg.PIC:
                        Glide.with(getContext())
                                .load(getContext().getFilesDir().getAbsolutePath() + "/pic/" + msg.getMsg())
                                .centerCrop()
                                .placeholder(R.mipmap.ic_placeholder)
                                .into((ImageView) baseViewHolder.getView(R.id.chat_pic_item_mine));
                        baseViewHolder.setGone(R.id.chat_pic_item_mine,false);
                        baseViewHolder.setGone(R.id.chat_tv_item_mine,true);
                        baseViewHolder.setGone(R.id.chat_voice_item_mine,true);
                        break;
                    case Msg.VOICE:
                        baseViewHolder.setGone(R.id.chat_voice_item_mine,false);
                        baseViewHolder.setGone(R.id.chat_tv_item_mine,true);
                        baseViewHolder.setGone(R.id.chat_pic_item_mine,true);
                        break;
                }
                break;
            case Msg.OTHER:
                baseViewHolder.setText(R.id.chat_tv_other, msg.getMsg());
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