package com.mob.gochat.model;

public class User {
    private int number;
    private String mail;
    private UserInfo userInfo;

    public User(int number, String mail, UserInfo userInfo){
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
