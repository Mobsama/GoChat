package com.mob.gochat.view.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.mob.gochat.utils.EmotionUtil;
import com.mob.gochat.utils.UIUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 为表情面板添加底部留白
 */
public class EmotionDecoration extends RecyclerView.ItemDecoration{
    private Context context;
    public EmotionDecoration(Context context){
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int size = EmotionUtil.getEmotionList().size();
        int position = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        int row = size / 7;
        if(position >= row * 7 && position <= size-1){
            outRect.bottom = (int) UIUtil.dp2px(context,70);

        }else if(position >=0 && position < 7){
            outRect.top = (int) UIUtil.dp2px(context,10);
        }
    }
}
