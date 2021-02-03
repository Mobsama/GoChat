package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "msg")
public class Msg implements MultiItemEntity {
    @Ignore
    public static final int FRI = 0;
    @Ignore
    public static final int MINE = 1;
    @Ignore
    public static final int TEXT = 0;
    @Ignore
    public static final int PIC = 1;
    @Ignore
    public static final int VOICE = 2;

    @Getter
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "uuid", typeAffinity = ColumnInfo.TEXT)
    private String uuid;

    @Getter
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.TEXT)
    private String id;

    @Getter
    @ColumnInfo(name = "type", typeAffinity = ColumnInfo.INTEGER)
    private int type;

    @Getter
    @ColumnInfo(name = "msg_type", typeAffinity = ColumnInfo.INTEGER)
    private int msgType;

    @Getter
    @ColumnInfo(name = "msg", typeAffinity = ColumnInfo.TEXT)
    private String msg;

    @Getter
    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    private String time;

    public Msg(String uuid, String id, int type, int msgType, String msg, String time){
        this.uuid = uuid;
        this.id = id;
        this.msg = msg;
        this.type = type;
        this.msgType = msgType;
        this.time = time;
    }

    @Ignore
    @Override
    public int getItemType() {
        return type;
    }
}
