package com.mob.gochat.model;

public class Pic {
    private boolean isChoose;
    private String picPath;
    public Pic(String picPath){
        isChoose = false;
        this.picPath = picPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        this.isChoose = choose;
    }
}
