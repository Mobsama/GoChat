package com.mob.gochat.view.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import com.mob.gochat.model.Buddy;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyDecoration extends RecyclerView.ItemDecoration{
    private int mTitleHeight;
    private int mTextSize;
    private List<Buddy> mData;
    private Paint mPaint;
    private Rect mBounds;

    private static final int TITLE_BG_COLOR = Color.parseColor("#FFDFDFDF");
    private static final int TITLE_TEXT_COLOR = Color.parseColor("#FF000000");

    public StickyDecoration(Context context, List<Buddy> data){
        super();
        mData = data;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,context.getResources().getDisplayMetrics());
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        if(mData.get(position).getLetters().equals("↑")) return;

        if(!mData.get(position).getLetters().equals(mData.get(position - 1).getLetters())){
            outRect.top = mTitleHeight;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if(mData.get(position).getLetters().equals("↑")) continue;
            if(!mData.get(position).getLetters().equals(mData.get(position - 1).getLetters())){
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                drawTitle(c,left,right,child,params,position);
            }

        }
    }

    private void drawTitle(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        mPaint.setColor(TITLE_BG_COLOR);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(TITLE_TEXT_COLOR);

        mPaint.getTextBounds(mData.get(position).getLetters(), 0, mData.get(position).getLetters().length(), mBounds);
        c.drawText(mData.get(position).getLetters(),
                child.getPaddingLeft(),
                child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int position = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();
        if(mData.get(position).getLetters().equals("↑")) return;
        String tag = mData.get(position).getLetters();
        View child = parent.findViewHolderForAdapterPosition(position).itemView;
        boolean flag = false;
        if ((position + 1) < mData.size()) {
            if (null != tag && !tag.equals(mData.get(position + 1).getLetters())) {
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }
        mPaint.setColor(TITLE_BG_COLOR);
        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(TITLE_TEXT_COLOR);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft(),
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        if (flag)
            c.restore();
    }
}
