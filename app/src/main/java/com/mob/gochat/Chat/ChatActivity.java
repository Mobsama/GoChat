package com.mob.gochat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.billy.android.swipe.listener.SimpleSwipeListener;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.effective.android.panel.PanelSwitchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mob.gochat.R;
import com.mob.gochat.Util.EmotionUtil;
import com.mob.gochat.Util.SpanStringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private PanelSwitchHelper mHelper;
    private RecyclerView mRVEmotion;
    private RecyclerView mRVChat;
    private FloatingActionButton mFABDelete;
    private EditText mETChat;
    private ImageButton mBtnSend;
    private ImageView mIVPic;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(mETChat.getText())){
                mBtnSend.setEnabled(false);
            }else{
                mBtnSend.setEnabled(true);
            }
        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);
        findViewById();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mHelper == null){
            mHelper = new PanelSwitchHelper.Builder(this)
                    .build();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 16908332:
                if(mHelper != null){
                    mHelper.hookSystemBackByPanelSwitcher();
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(mHelper != null && mHelper.hookSystemBackByPanelSwitcher()){
            return;
        }
        super.onBackPressed();
    }

    private void initView() {
        mETChat.addTextChangedListener(mTextWatcher);

        mRVEmotion.setLayoutManager(new GridLayoutManager(this, 7));
        mRVEmotion.addItemDecoration(new EmotionDecoration());
        EmotionAdapter emotionAdapter = new EmotionAdapter(R.layout.chat_emotion_item, EmotionUtil.getEmotionList());

        emotionAdapter.setOnItemClickListener((adapter, view, position) -> {
            String emotionName = EmotionUtil.getEmotionMap().keyAt(position);
            int curPosition = mETChat.getSelectionStart();
            StringBuilder sb = new StringBuilder(mETChat.getText().toString());
            sb.insert(curPosition, emotionName);
            mETChat.setText(SpanStringUtils.getEmotionContent(ChatActivity.this, mETChat, sb.toString()));
            mETChat.setSelection(curPosition + emotionName.length());
        });

        mFABDelete.setOnClickListener(v -> mETChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)));
        mRVEmotion.setAdapter(emotionAdapter);


        LinearLayoutManager chatManager = new LinearLayoutManager(this);
        chatManager.setStackFromEnd(true);
        mRVChat.setLayoutManager(chatManager);
        List<ChatModel> chatData = new ArrayList<>();
        ChatAdapter chatAdapter = new ChatAdapter(chatData);

        mBtnSend.setOnClickListener(v -> {
            ChatModel chatModel = new ChatModel(mETChat.getText(),new Random().nextInt(2));
            chatData.add(chatModel);
            chatAdapter.notifyDataSetChanged();
            chatManager.scrollToPosition(chatData.size()-1);
            mETChat.setText("");
        });

        chatAdapter.addChildClickViewIds(R.id.chat_iv_item_fri, R.id.chat_iv_item_mine);
        chatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.chat_iv_item_fri){
                Intent intent = new Intent(ChatActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        mRVChat.setAdapter(chatAdapter);

        mIVPic.setOnClickListener(v -> {
            Intent intent = new Intent(this,PicActivity.class);
            startActivity(intent);
        });
    }

    void findViewById(){
        mRVEmotion = findViewById(R.id.chat_rv_emotion);
        mFABDelete = findViewById(R.id.chat_fab_emotion);
        mETChat = findViewById(R.id.chat_edit);
        mRVChat = findViewById(R.id.chat_rv_chat);
        mBtnSend = findViewById(R.id.chat_btn_send);
        mBtnSend.setEnabled(false);
        mIVPic = findViewById(R.id.chat_btn_pic);
    }

    class EmotionAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

        public EmotionAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, Integer id) {
            baseViewHolder.setImageResource(R.id.chat_iv_emotion_item, id);
        }
    }

    class ChatModel implements MultiItemEntity {
        public static final int FRI = 0;
        public static final int MINE = 1;
        CharSequence msg;
        int type;
        ChatModel(CharSequence msg,int type){
            this.msg = msg;
            this.type = type;
        }

        public CharSequence getMsg() {
            return msg;
        }

        @Override
        public int getItemType() {
            return type;
        }
    }

    class ChatAdapter extends BaseMultiItemQuickAdapter<ChatModel,BaseViewHolder>{

        ChatAdapter(List data){
            super(data);
            addItemType(ChatModel.FRI, R.layout.chat_list_item_fri);
            addItemType(ChatModel.MINE, R.layout.chat_list_item_mine);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatModel chatModel) {
            switch (baseViewHolder.getItemViewType()){
                case ChatModel.FRI:
                    baseViewHolder.setText(R.id.chat_tv_item_fir,chatModel.getMsg());
                    break;
                case ChatModel.MINE:
                    baseViewHolder.setText(R.id.chat_tv_item_mine,chatModel.getMsg());
                    break;
            }
        }
    }

    /**
     * 为表情面板添加底部留白
     */
    class EmotionDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int size = EmotionUtil.getEmotionList().size();
            int position = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
            int row = size / 7;
            if(position >= row * 7 && position <= size-1){
                outRect.bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        70,getResources().getDisplayMetrics());

            }else if(position >=0 && position < 7){
                outRect.top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        10,getResources().getDisplayMetrics());
            }
        }
    }
}