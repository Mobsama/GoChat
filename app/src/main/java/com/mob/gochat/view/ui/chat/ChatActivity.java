package com.mob.gochat.view.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.effective.android.panel.PanelSwitchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mob.gochat.model.MsgModel;
import com.mob.gochat.R;
import com.mob.gochat.utils.EmotionUtil;
import com.mob.gochat.utils.SpanStringUtils;
import com.mob.gochat.view.adapter.ChatAdapter;
import com.mob.gochat.view.adapter.EmotionAdapter;
import com.mob.gochat.view.ui.InfoActivity;
import com.mob.gochat.view.ui.customizeview.EmotionDecoration;
import com.mob.gochat.viewmodel.ChatViewModel;


import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private PanelSwitchHelper mHelper;
    private RecyclerView mRVEmotion;
    private RecyclerView mRVChat;
    private FloatingActionButton mFABDelete;
    private EditText mETChat;
    private ImageButton mBtnSend,mBtnVoice;
    private ImageView mIVPic;
    private TextView mVoiceTime;

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager chatManager;
    private EmotionAdapter emotionAdapter;
    private long mStartVoiceTime;
    private ChatHandle chatHandle = new ChatHandle(this);
    private ScheduledExecutorService scheduledExecutor;

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnSend.setEnabled(!TextUtils.isEmpty(mETChat.getText()));
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

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        findViewById();
        mETChat.addTextChangedListener(mTextWatcher);
        initEmotionRecycleView();
        initChatRecycleView();
        initOnClickListener();
        chatViewModel.getChatData().observe(this, msgModels -> updateUIData());
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
        if (item.getItemId() == 16908332) {
            if (mHelper != null) {
                mHelper.hookSystemBackByPanelSwitcher();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mHelper != null && mHelper.hookSystemBackByPanelSwitcher()){
            return;
        }
        super.onBackPressed();
    }

    void findViewById(){
        mRVEmotion = findViewById(R.id.chat_rv_emotion);
        mFABDelete = findViewById(R.id.chat_fab_emotion);
        mETChat = findViewById(R.id.chat_edit);
        mRVChat = findViewById(R.id.chat_rv_chat);
        mBtnSend = findViewById(R.id.chat_btn_send);
        mBtnSend.setEnabled(false);
        mIVPic = findViewById(R.id.chat_btn_pic);
        mBtnVoice = findViewById(R.id.chat_panel_voice_btn);
        mVoiceTime = findViewById(R.id.chat_panel_voice_tv_time);
    }

    private void initOnClickListener() {
        //表情面板删除按钮点击事件
//        mFABDelete.setOnClickListener(v -> mETChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)));
        mFABDelete.setOnTouchListener(new EmotionDelBtnOnTouchListener());

        //发送按钮点击事件
        mBtnSend.setOnClickListener(v -> {
            MsgModel msgModel = new MsgModel(mETChat.getText(),new Random().nextInt(2));
            chatViewModel.addMsg(msgModel);
            mETChat.setText("");
        });

        //聊天头像点击事件
        chatAdapter.addChildClickViewIds(R.id.chat_iv_item_fri, R.id.chat_iv_item_mine);
        chatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.chat_iv_item_fri){
                Intent intent = new Intent(ChatActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        //表情面板中的表情的点击事件
        emotionAdapter.setOnItemClickListener((adapter, view, position) -> {
            String emotionName = EmotionUtil.getEmotionMap().keyAt(position);
            int curPosition = mETChat.getSelectionStart();
            StringBuilder sb = new StringBuilder(mETChat.getText().toString());
            sb.insert(curPosition, emotionName);
            mETChat.setText(SpanStringUtils.getEmotionContent(ChatActivity.this, mETChat, sb.toString()));
            mETChat.setSelection(curPosition + emotionName.length());
        });

        //照片图标点击事件
        mIVPic.setOnClickListener(v -> {
            Intent intent = new Intent(this,PicActivity.class);
            startActivity(intent);
        });

        //录音按钮点击事件
        mBtnVoice.setOnTouchListener(new VoiceBtnOnTouchListener());
    }

    private void initEmotionRecycleView(){
        emotionAdapter = new EmotionAdapter(R.layout.chat_emotion_item, EmotionUtil.getEmotionList());
        mRVEmotion.setLayoutManager(new GridLayoutManager(this, 7));
        mRVEmotion.addItemDecoration(new EmotionDecoration(this));
        mRVEmotion.setAdapter(emotionAdapter);
    }

    private void initChatRecycleView(){
        chatManager = new LinearLayoutManager(this);
        chatManager.setStackFromEnd(true);
        mRVChat.setLayoutManager(chatManager);
        chatAdapter = new ChatAdapter(chatViewModel.getChatData().getValue());
        mRVChat.setAdapter(chatAdapter);
    }

    private void updateUIData(){
        chatAdapter.setList(chatViewModel.getChatData().getValue());
        mRVChat.setAdapter(chatAdapter);
        chatManager.scrollToPosition(chatViewModel.getChatData().getValue().size()-1);
    }

    static class ChatHandle extends Handler {
        private WeakReference<ChatActivity> picActivityWeakReference;
        public ChatHandle(ChatActivity chatActivity){
            picActivityWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ChatActivity chatActivity = picActivityWeakReference.get();
            if(msg.what==1){
                long time = System.currentTimeMillis() - chatActivity.mStartVoiceTime;
                String min = time / 60000 >= 10 ? time / 60000 + "" : "0" + time / 60000;
                String sec = time / 1000 % 60 >= 10 ? time / 1000 % 60 + "" : "0" + time / 1000 % 60;
                String ms = time / 10 % 100 >= 10 ? time / 10 % 100 + "" : "0" + time / 10 % 100;
                chatActivity.mVoiceTime.setText(min+":"+sec+":"+ms);
            }else if(msg.what == 2){
                chatActivity.mETChat.dispatchKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        }
    }

    private void startVoiceTime(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 1;
            chatHandle.sendMessage(msg);
        },0,10, TimeUnit.MILLISECONDS);
    }

    private void stopVoiceTime(){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    private void startEmotionDel(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 2;
            chatHandle.sendMessage(msg);
        },0,500, TimeUnit.MILLISECONDS);
    }

    private void stopEmotionDel(){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    class VoiceBtnOnTouchListener implements View.OnTouchListener{
        float x=0 , y=0;
        @SuppressWarnings("deprecation")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.startAnimation(AnimationUtils
                        .loadAnimation(ChatActivity.this,R.anim.btn_scale_down));
                mStartVoiceTime = System.currentTimeMillis();
                x = event.getX();
                y = event.getY();
                startVoiceTime();
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if(Math.sqrt(Math.pow(event.getX() - x,2)+Math.pow(event.getY() - y,2)) > 200){
                    Log.d("stop","----------stop---------");
                    stopVoiceTime();
                    mVoiceTime.setText("00:00:00");
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                v.startAnimation(AnimationUtils
                        .loadAnimation(ChatActivity.this,R.anim.btn_scale_up));
                stopVoiceTime();
                mVoiceTime.setText("00:00:00");
            }
            return true;
        }
    }

    class EmotionDelBtnOnTouchListener implements View.OnTouchListener{
        @SuppressWarnings("deprecation")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.startAnimation(AnimationUtils
                        .loadAnimation(ChatActivity.this,R.anim.btn_scale_down));
                startEmotionDel();
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                stopEmotionDel();
            }
            return true;
        }
    }

}