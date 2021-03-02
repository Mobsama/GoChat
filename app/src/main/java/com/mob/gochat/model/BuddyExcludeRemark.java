package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.Expose;

import lombok.Getter;

@Entity(tableName = "buddy",
        primaryKeys = {"id", "user"},
        indices = {@Index(value = {"id"},unique = true)})
public class BuddyExcludeRemark {
    @Getter
    @Expose
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.TEXT)
    private final String id;

    @Getter
    @Expose
    @ColumnInfo(name = "user", typeAffinity = ColumnInfo.TEXT)
    private final String user;

    @Getter
    @Expose
    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.TEXT)
    private final String avatar;

    @Getter
    @Expose
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private final String name;

    @Getter
    @Expose
    @ColumnInfo(name = "mail", typeAffinity = ColumnInfo.TEXT)
    private final String mail;

    @Getter
    @Expose
    @ColumnInfo(name = "birth", typeAffinity = ColumnInfo.TEXT)
    private final String birth;

    @Getter
    @Expose
    @ColumnInfo(name = "address", typeAffinity = ColumnInfo.TEXT)
    private final String address;

    @Getter
    @Expose
    @ColumnInfo(name = "gender", typeAffinity = ColumnInfo.INTEGER)
    private final int gender;

    public BuddyExcludeRemark(String id, String user, String avatar, String name, String mail, String birth, String address, int gender){
        this.id = id;
        this.user = user;
        this.avatar = avatar;
        this.address = address;
        this.name = name;
        this.mail = mail;
        this.birth = birth;
        this.gender = gender;
    }
}