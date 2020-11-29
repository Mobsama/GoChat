package com.mob.gochat.model;

public class PicModel{
    private boolean isChoose;
    private String picPath;
    public PicModel(String picPath){
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
        isChoose = choose;
    }
}
