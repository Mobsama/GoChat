package com.mob.gochat.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private volatile static Toast mToast;
    public static void showMsg(Context context, String msg){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_LONG);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }
}
