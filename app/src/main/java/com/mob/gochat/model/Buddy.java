package com.mob.gochat.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "buddy",
        primaryKeys = {"id", "user"})
public class Buddy implements Parcelable {
    @Getter
    @NotNull
    @Expose
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.TEXT)
    private final String id;

    @NotNull
    @Getter
    @Expose
    @ColumnInfo(name = "user", typeAffinity = ColumnInfo.TEXT)
    private final String user;

    @Setter
    @Getter
    @Expose
    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.TEXT)
    private String avatar;

    @Setter
    @Getter
    @Expose
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private String name;

    @Setter
    @Getter
    @Expose
    @ColumnInfo(name = "mail", typeAffinity = ColumnInfo.TEXT)
    private String mail;

    @Getter
    @Expose
    @ColumnInfo(name = "remarks", typeAffinity = ColumnInfo.TEXT)
    private String remarks;

    @Getter
    @Setter
    @Expose
    @ColumnInfo(name = "birth", typeAffinity = ColumnInfo.TEXT)
    private String birth;

    @Getter
    @Setter
    @Expose
    @ColumnInfo(name = "address", typeAffinity = ColumnInfo.TEXT)
    private String address;

    @Getter
    @Setter
    @Expose
    @ColumnInfo(name = "gender", typeAffinity = ColumnInfo.INTEGER)
    private int gender;

    @Getter
    @Setter
    @Ignore
    @Expose(serialize = false, deserialize = false)
    private String letters;

    public Buddy(@NotNull String id, @NotNull String user, String name, String avatar, String mail, String birth, String address, int gender){
        this.id = id;
        this.user = user;
        this.name = name;
        this.avatar = avatar;
        this.mail = mail;
        this.birth = birth;
        this.address = address;
        this.gender = gender;

        setLettersWithName(name);
    }

    public void setRemarks(String remarks){
        this.remarks = remarks;
        if(remarks != null && !remarks.equals("")){
            setLettersWithName(remarks);
        }else {
            setLettersWithName(this.name);
        }
    }

    public void setLettersWithName(String s){
        char c;
        if(Pinyin.isChinese(s.charAt(0))){
            c = Pinyin.toPinyin(s.charAt(0)).toUpperCase().charAt(0);
        }else{
            c = s.toUpperCase().charAt(0);
            if(!(c >= 'A' && c <= 'Z')){
                c = '#';
            }
        }
        this.letters = c + "";
    }

    @Ignore
    protected Buddy(Parcel in) {
        id = in.readString();
        user = in.readString();
        avatar = in.readString();
        name = in.readString();
        mail = in.readString();
        remarks = in.readString();
        birth = in.readString();
        address = in.readString();
        gender = in.readInt();
        letters = in.readString();
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(avatar);
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(remarks);
        dest.writeString(birth);
        dest.writeString(address);
        dest.writeInt(gender);
        dest.writeString(letters);
    }

    @Ignore
    public static final Creator<Buddy> CREATOR = new Creator<Buddy>() {
        @Override
        public Buddy createFromParcel(Parcel in) {
            return new Buddy(in);
        }

        @Override
        public Buddy[] newArray(int size) {
            return new Buddy[size];
        }
    };
}