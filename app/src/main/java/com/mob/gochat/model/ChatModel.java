package com.mob.gochat.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ChatModel implements MultiItemEntity {
    public static final int FRI = 0;
    public static final int MINE = 1;
    CharSequence msg;
    int type;
    public ChatModel(CharSequence msg,int type){
        this.msg = msg;
        this.type = type;
    }

    public CharSequence getMsg() {
        return msg;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
