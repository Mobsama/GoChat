package com.mob.gochat.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MsgModel implements MultiItemEntity {
    public static final int FRI = 0;
    public static final int MINE = 1;

    public static final int TEXT = 0;
    public static final int PIC = 1;
    public static final int VOICE = 2;

    private int type;
    private int msgType;
    private CharSequence msg;

    public MsgModel(CharSequence msg, int type, int msgType){
        this.msg = msg;
        this.type = type;
        this.msgType = msgType;
    }

    public CharSequence getMsg() {
        return msg;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public int getMsgType() {return msgType; }

}
