package com.mob.gochat.view.ui.customizeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBarView extends View {
    private static String[] text = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#" };
    private TextView mDialog;
    private Paint paint = new Paint();
    private int currChoose = -1;
    private OnTouchingListener onTouchingListener;

    public SideBarView(Context context) {
        super(context);
    }
    public SideBarView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public SideBarView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    public void setDialog(TextView dialog){
        this.mDialog = dialog;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / text.length;

        for(int i = 0; i < text.length; i++){

            if(i == currChoose){
                paint.setColor(Color.parseColor("#FF008080"));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width/2, (float) (singleHeight *(i+0.75)),30 ,paint);
                paint.setTypeface(Typeface.DEFAULT); //字体样式
                paint.setAntiAlias(true); //抗锯齿
                paint.setTextSize(30);
                paint.setColor(Color.parseColor("#FFFFFFFF"));
                float xPos = width /2 - paint.measureText(text[i]) / 2;
                float yPos = singleHeight * ( i + 1 );
                canvas.drawText(text[i], xPos, yPos, paint);
            }else{
                paint.setAntiAlias(true); //抗锯齿
                paint.setTextSize(30);
                paint.setTypeface(Typeface.DEFAULT); //字体样式
                paint.setColor(Color.parseColor("#FF000000"));
                float xPos = width /2 - paint.measureText(text[i]) / 2;
                float yPos = singleHeight * ( i + 1 );
                canvas.drawText(text[i], xPos, yPos, paint);
            }
            paint.reset();

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = currChoose;
        final OnTouchingListener listener = onTouchingListener;
        final int c = (int)( y / getHeight() * text.length);
        setBackground(new ColorDrawable(0x00000000));

        switch (action){
            case MotionEvent.ACTION_UP:
                currChoose = -1;
                invalidate();
                if(mDialog != null) {
                    mDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                if(oldChoose != c){
                    if(c >= 0 && c < text.length){
                        if(listener != null ){
                            listener.onTouching(text[c]);
                        }
                        if(mDialog != null){
                            mDialog.setText((text[c]));
                            mDialog.setVisibility(View.VISIBLE);
                        }

                        currChoose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingListener(OnTouchingListener onTouchingListener){
        this.onTouchingListener = onTouchingListener;
    }

    public interface OnTouchingListener{
        void onTouching(String s);
    }
}
