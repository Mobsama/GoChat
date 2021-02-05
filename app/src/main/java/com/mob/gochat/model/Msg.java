package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "msg",
        foreignKeys = {
            @ForeignKey(entity = Buddy.class,
                parentColumns = "id",
                childColumns = "from_id",
                onDelete = CASCADE),
            @ForeignKey(entity = Buddy.class,
                parentColumns = "id",
                childColumns = "to_id",
                onDelete = CASCADE)
        })
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
    @ColumnInfo(name = "from_id", typeAffinity = ColumnInfo.TEXT)
    private String fromId;

    @Getter
    @ColumnInfo(name = "to_id", typeAffinity = ColumnInfo.TEXT)
    private String toId;

    @Getter
    @ColumnInfo(name = "msg_type", typeAffinity = ColumnInfo.INTEGER)
    private int msgType;

    @Getter
    @ColumnInfo(name = "msg", typeAffinity = ColumnInfo.TEXT)
    private String msg;

    @Getter
    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    private String time;

    @Ignore
    private int type;

    public Msg(@NotNull String uuid, String fromId, String toId, int msgType, String msg, String time){
        this.uuid = uuid;
        this.fromId = fromId;
        this.toId = toId;
        this.msg = msg;
        this.msgType = msgType;
        this.time = time;

        if(fromId.equals(MMKVUitl.getString(DataKeyConst.USER_ID))){
            this.type = MINE;
        }else{
            this.type = FRI;
        }
    }

    @Ignore
    @Override
    public int getItemType() {
        return type;
    }
}
