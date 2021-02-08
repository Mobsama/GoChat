package com.mob.gochat.view.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyWithMsgWrapper;
import com.mob.gochat.model.Msg;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MsgAdapter extends BaseQuickAdapter<BuddyWithMsgWrapper, BaseViewHolder> {

    public MsgAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BuddyWithMsgWrapper buddyWithMsgWrapper) {
        Buddy buddy = buddyWithMsgWrapper.getBuddy();
        Msg msg = buddyWithMsgWrapper.getMsg().get(0).getMsg();
        StringBuffer sb = new StringBuffer();
        if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
            baseViewHolder.setText(R.id.tv_title, buddy.getName());
            sb.append(msg.getType() == Msg.FRI ? buddy.getName()+":" : "我:");
        }else{
            baseViewHolder.setText(R.id.tv_title, buddy.getRemarks());
            sb.append(msg.getType() == Msg.FRI ? buddy.getRemarks()+":" : "我:");
        }
        sb.append(msg.getMsg());
        baseViewHolder.setText(R.id.tv_msg_content, sb.toString());
        baseViewHolder.setText(R.id.tv_msg_time, msg.getTime().substring(11,16));
    }
}
