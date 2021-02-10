package com.mob.gochat.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Vibrator;

public class VibrateUtil {
    public static void playVibrate(Context context, boolean isRepeat){
        try {
            Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] patern = new long[]{0, 100};
            AudioAttributes audioAttributes = null;
            /**
             * 适配android7.0以上版本的震动
             * 说明：如果发现5.0或6.0版本在app退到后台之后也无法震动，那么只需要改下方的Build.VERSION_CODES.N版本号即可
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM) //key
                        .build();
                mVibrator.vibrate(patern, isRepeat ? 1 : -1, audioAttributes);
            }else {
                mVibrator.vibrate(patern, isRepeat ? 1 : -1);
            }
        } catch (Exception ex) {
        }

    }
}
