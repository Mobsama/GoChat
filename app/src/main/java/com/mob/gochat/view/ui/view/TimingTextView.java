package com.mob.gochat.view.ui.view;

import android.content.Context;
import android.util.AttributeSet;

public class TimingTextView extends androidx.appcompat.widget.AppCompatTextView implements Runnable {

    private int currTime;
    private boolean isRunning = false;
    private final String prefix = "重新发送(";
    private final String suffix = "s)";

    public TimingTextView(Context context) {
        super(context);
    }

    public TimingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start(){
        isRunning = true;
        currTime = 60;
        run();
    }

    public void stop(){
        isRunning = false;
    }

    @Override
    public void run() {
        if(isRunning){
            ComputeTime();
            String text;
            if(currTime == 0){
                text = "重新发送";
            }else{
                text = prefix + currTime + suffix;
            }
            this.setText(text);
            postDelayed(this,1000);
        }else{
            this.setText("重新发送");
            removeCallbacks(this);
        }
    }

    private void ComputeTime(){
        currTime--;
        if(currTime == 0){
            stop();
        }
    }
}
