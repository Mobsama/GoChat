package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmotionAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public EmotionAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Integer id) {
        baseViewHolder.setImageResource(R.id.chat_iv_emotion_item, id);
    }
}
