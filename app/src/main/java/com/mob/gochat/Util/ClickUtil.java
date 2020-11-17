package com.mob.gochat.Util;

public class ClickUtil {
    public static long lastClickTime = 0L;
    public static boolean isFastDoubleClick(){
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if(timeD < 1000){
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
