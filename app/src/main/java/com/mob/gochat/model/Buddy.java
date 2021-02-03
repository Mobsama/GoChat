package com.mob.gochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.github.promeg.pinyinhelper.Pinyin;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity(tableName = "buddy")
public class Buddy {
    @Getter
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.TEXT)
    private String id;

    @Getter
    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.TEXT)
    private String avatar;

    @Getter
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private String name;

    @Getter
    @ColumnInfo(name = "mail", typeAffinity = ColumnInfo.TEXT)
    private String mail;

    @Getter
    @Setter
    @Ignore
    private String letters;

    public Buddy(String id, String name, String avatar, String mail){
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.mail = mail;

        char c;
        if(Pinyin.isChinese(name.charAt(0))){
            c = Pinyin.toPinyin(name.charAt(0)).toUpperCase().charAt(0);
        }else{
            c = name.toUpperCase().charAt(0);
            if(!(c >= 'A' && c <= 'Z')){
                c = '#';
            }
        }
        this.letters = c + "";
    }
}