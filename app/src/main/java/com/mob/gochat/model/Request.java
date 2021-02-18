package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "uuid", typeAffinity = ColumnInfo.TEXT)
    private String uuid;

    @Getter
    @ColumnInfo(name = "user_id", typeAffinity = ColumnInfo.TEXT)
    private String userId;

    @Getter
    @ColumnInfo(name = "buddy_id", typeAffinity = ColumnInfo.TEXT)
    private String buddyId;

    @Getter
    @ColumnInfo(name = "buddy_name", typeAffinity = ColumnInfo.TEXT)
    private String buddyName;

    @Getter
    @ColumnInfo(name = "buddy_avatar", typeAffinity = ColumnInfo.TEXT)
    private String buddyAvatar;

    @Getter
    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    private String time;

    @Getter
    @Setter
    @ColumnInfo(name = "is_treated", typeAffinity = ColumnInfo.INTEGER)
    private int isTreated;

    public Request(@NotNull String uuid, String userId, String buddyId, String buddyName, String buddyAvatar, String time, int isTreated){
        this.uuid = uuid;
        this.userId = userId;
        this.buddyId = buddyId;
        this.buddyName = buddyName;
        this.buddyAvatar = buddyAvatar;
        this.time = time;
        this.isTreated = isTreated;
    }

    @Ignore
    public static final int UNTREATED = 0;
    @Ignore
    public static final int REJECTED = 1;
    @Ignore
    public static final int APPROVED = 2;
}
