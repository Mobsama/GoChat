package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "request",
        foreignKeys = {
        @ForeignKey(entity = Buddy.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = CASCADE)},
        indices = {@Index(value = "user_id")})
public class Request {
    @Getter
    @Expose()
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "uuid", typeAffinity = ColumnInfo.TEXT)
    private String uuid;

    @Getter
    @Expose()
    @ColumnInfo(name = "user_id", typeAffinity = ColumnInfo.TEXT)
    private String userId;

    @Getter
    @Expose()
    @ColumnInfo(name = "buddy_id", typeAffinity = ColumnInfo.TEXT)
    private String buddyId;

    @Getter
    @Expose()
    @ColumnInfo(name = "buddy_name", typeAffinity = ColumnInfo.TEXT)
    private String buddyName;

    @Getter
    @Setter
    @Expose()
    @ColumnInfo(name = "buddy_avatar", typeAffinity = ColumnInfo.TEXT)
    private String buddyAvatar;

    @Getter
    @Expose()
    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    private String time;

    @Getter
    @Setter
    @Expose(serialize = false, deserialize = false)
    @ColumnInfo(name = "is_treated", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    private int isTreated;

    public Request(@NotNull String uuid, String userId, String buddyId, String buddyName, String buddyAvatar, String time){
        this.uuid = uuid;
        this.userId = userId;
        this.buddyId = buddyId;
        this.buddyName = buddyName;
        this.buddyAvatar = buddyAvatar;
        this.time = time;
        this.isTreated = UNTREATED;
    }

    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int UNTREATED = 0;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int REJECTED = 1;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public static final int APPROVED = 2;
}
