package com.mob.gochat.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.github.promeg.pinyinhelper.Pinyin;
import com.mob.gochat.CustomizeView.SideBarView;
import com.mob.gochat.R;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuddyFragment extends Fragment {

    private UserViewModel userViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_buddy, container, false);

        SideBarView mSideBarView = root.findViewById(R.id.sb_buddy);
        TextView mDialog = root.findViewById(R.id.tv_dialog);
        mSideBarView.setDialog(mDialog);

        SwipeRecyclerView mSRVBuddy = root.findViewById(R.id.srv_buddy);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mSRVBuddy.setLayoutManager(manager);

        List<String> list = Arrays.asList("胡泉源",
                "李相赫", "唐志明", "朱泽楷", "郑锐轩", "陈继恩", "李旭辉", "詹智聪", "坠毁", "李科",
                "吴彦祖", "王健成", "张伪娘", "林丰涛", "序伟", "张伪娘", "郑泽芬", "魏法峰", "%guozehong",
                "我就是大名鼎鼎的鸟人辉了", "欧阳说ok", "一斤", "冼毅贤", "欧阳说ok", "刘启炬", "英语六级小天才胡茄声",
                "钧仔", "花都嗨", "张伪娘", "男神", "张伪娘", "张伪娘", "张飞", "jcj", "刘大壮", "李素函",
                "比利王", "龟王李科", "郑泽芬", "吴彦祖", "陈铎友", "?????", "黄家欣", "陈彦儒", "李白",
                "赖杰颖", "阿桂", "邓沛锦", "斯内克", "李升辉", "鸭王钟", "关羽", "欧阳宇豪", "高中大馒头",
                "邓银生", "林小七", "uzi", "张飞", "韶关学院地头蛇", "duckingwu", "卢本伟", "高渐离婚",
                "韶院过江龙欧阳宇豪", "爱新觉罗福泉", "%guozehong", "郭仔凡");
        List<SortModel> models = new ArrayList<>();
        for(String s : list){
            SortModel model = new SortModel();
            model.setName(s);
            models.add(model);
        }
        Collections.sort(models, (o1, o2) -> {
            if(o1.getLetters().equals("#") && !o2.getLetters().equals("#")){
                return 1;
            }else if(!o1.getLetters().equals("#") && o2.getLetters().equals("#")){
                return -1;
            }else{
                if(!o1.getLetters().equals(o2.getLetters())) {
                    return o1.getLetters().compareToIgnoreCase(o2.getLetters());
                }else{
                    return Pinyin.toPinyin(o1.getName(),"")
                            .compareToIgnoreCase(Pinyin.toPinyin(o2.getName(),""));
                }
            }
        });
        StickyDecoration stickyDecoration = new StickyDecoration(getContext(),models);
        mSRVBuddy.addItemDecoration(stickyDecoration);
        BuddyAdapter buddyAdapter = new BuddyAdapter(R.layout.list_item_buddy,models);

        SwipeMenuCreator mSwipeMenuCreator = (leftMenu, rightMenu, position) -> {
            SwipeMenuItem remarksItem = new SwipeMenuItem(getContext());
            remarksItem.setBackground(R.color.gray);
            remarksItem.setText("备注");
            remarksItem.setTextColor(Color.WHITE);
            remarksItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            remarksItem.setWidth(getResources().getDimensionPixelOffset(R.dimen.dp_80));
            rightMenu.addMenuItem(remarksItem);
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
            deleteItem.setBackground(R.color.red);
            deleteItem.setText("删除");
            deleteItem.setTextColor(Color.WHITE);
            deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            deleteItem.setWidth(getResources().getDimensionPixelOffset(R.dimen.dp_80));
            rightMenu.addMenuItem(deleteItem);
        };
        OnItemMenuClickListener mMenuItemClickListener = (menuBridge, adapterPosition) -> {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection();
            int menuPosition = menuBridge.getPosition();
            if(direction == SwipeRecyclerView.RIGHT_DIRECTION && menuPosition == 1){
                models.remove(adapterPosition);
                buddyAdapter.notifyDataSetChanged();
            }
        };
        mSideBarView.setOnTouchingListener(s -> {
            mSRVBuddy.smoothCloseMenu();
            for(int i = 0; i < models.size(); i++){
                if(models.get(i).getLetters().equals(s)){
                    manager.scrollToPositionWithOffset(i,0);
                    break;
                }else if(models.get(i).getLetters().charAt(0) > s.charAt(0)){
                    break;
                }
            }
        });


        mSRVBuddy.setSwipeMenuCreator(mSwipeMenuCreator);
        mSRVBuddy.setOnItemMenuClickListener(mMenuItemClickListener);
        mSRVBuddy.setAdapter(buddyAdapter);
        return root;
    }
}

class SortModel{
    private String name;
    private String letters;

    public String getName(){ return name; }

    public void setName(String name) {
        char c;
        if(Pinyin.isChinese(name.charAt(0))){
            c = Pinyin.toPinyin(name.charAt(0)).toUpperCase().charAt(0);
        }else{
            c = name.toUpperCase().charAt(0);
            if(!(c >= 'A' && c <= 'Z')){
                c = '#';
            }
        }
        this.name = name;
        this.letters = c + "";
    }

    public String getLetters() {
        return letters;
    }
}

class StickyDecoration extends RecyclerView.ItemDecoration{
    private int mTitleHeight;
    private int mTextSize;
    private List<SortModel> mData;
    private Paint mPaint;
    private Rect mBounds;

    private static int TITLE_BG_COLOR = Color.parseColor("#FFDFDFDF");
    private static int TITLE_TEXT_COLOR = Color.parseColor("#FF000000");

    public StickyDecoration(Context context,List<SortModel> data){
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
        if(position == 0
                || !mData.get(position).getLetters().equals(mData.get(position - 1).getLetters())){
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
            if(position == 0
                    || !mData.get(position).getLetters().equals(mData.get(position - 1).getLetters())){
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
        String tag = mData.get(position).getLetters();
        View child = parent.findViewHolderForAdapterPosition(position).itemView;
        boolean flag = false;
        if ((position + 1) < mData.size()) {
            //当前第一个可见的Item的字母索引，不等于其后一个item的字母索引，说明悬浮的View要切换了
            if (null != tag && !tag.equals(mData.get(position + 1).getLetters())) {
                //当第一个可见的item在屏幕中剩下的高度小于title的高度时，开始悬浮Title的动画
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
            c.restore();//恢复画布到之前保存的状态
    }
}

class BuddyAdapter extends BaseQuickAdapter<SortModel, BaseViewHolder> {

    public BuddyAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SortModel sortModel) {
        baseViewHolder.setText(R.id.tv_buddy_user,sortModel.getName());
    }
}
