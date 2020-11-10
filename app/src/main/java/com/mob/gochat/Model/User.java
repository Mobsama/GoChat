package com.mob.gochat.Model;

public class User {
    private int number;
    private String mail;
    private UserInfo userInfo;

    User(int number, String mail, UserInfo userInfo){
        this.number = number;
        this.mail = mail;
        this.userInfo = userInfo;
    }

    public int getNumber() {
        return number;
    }

    public String getMail() {
        return mail;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}

class UserInfo{
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
