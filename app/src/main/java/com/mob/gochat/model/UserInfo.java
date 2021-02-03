package com.mob.gochat.model;

public class UserInfo{
    private int genders;
    private int age;
    private String address;
    private String avatar;

    UserInfo(int genders, int age, String address, String avatar){
        this.genders = genders;
        this.age = age;
        this.address = address;
        this.avatar = avatar;
    }

    public int getAge() {
        return age;
    }

    public int getGenders() {
        return genders;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }
}
