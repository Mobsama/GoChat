package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.Expose;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "msg",
        foreignKeys = {
            @ForeignKey(entity = Buddy.class,
                parentColumns = "id",
                childColumns = "buddy_id",
                onDelete = CASCADE),
            @ForeignKey(entity = Buddy.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = CASCADE)},
        indices = {@Index(value = "buddy_id"), @Index(value = "user_id")})
public class Msg implements MultiItemEntity {
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int FRI = 0;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int MINE = 1;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int OTHER = 2;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int TEXT = 0;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int PIC = 1;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int VOICE = 2;

    @Getter
    @NotNull
    @Expose()
    @PrimaryKey
    @ColumnInfo(name = "uuid", typeAffinity = ColumnInfo.TEXT)
    private String uuid;

    @Getter
    @Expose()
    @ColumnInfo(name = "buddy_id", typeAffinity = ColumnInfo.TEXT)
    private String buddyId;

    @Getter
    @Expose()
    @ColumnInfo(name = "user_id", typeAffinity = ColumnInfo.TEXT)
    private String userId;

    @Getter
    @Expose()
    @ColumnInfo(name = "msg_type", typeAffinity = ColumnInfo.INTEGER)
    private int msgType;

    @Getter
    @Setter
    @Expose()
    @ColumnInfo(name = "msg", typeAffinity = ColumnInfo.TEXT)
    private String msg;

    @Getter
    @Expose()
    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    private String time;

    @Getter
    @Expose()
    @ColumnInfo(name = "type", typeAffinity = ColumnInfo.INTEGER)
    private int type;

    @Getter
    @Setter
    @Expose(serialize = false, deserialize = false)
    @ColumnInfo(name = "is_read", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    private boolean isRead;

    public Msg(@NotNull String uuid, String buddyId, String userId, int type, int msgType, String msg, String time){
        this.uuid = uuid;
        this.buddyId = buddyId;
        this.userId = userId;
        this.msg = msg;
        this.msgType = msgType;
        this.time = time;
        this.type = type;
    }

    @Ignore
    @Override
    public int getItemType() {
        return type;
    }
}
