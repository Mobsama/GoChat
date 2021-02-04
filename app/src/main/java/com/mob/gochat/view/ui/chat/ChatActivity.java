package com.mob.gochat.view.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.effective.android.panel.PanelSwitchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.mob.gochat.databinding.ActivityChatBinding;
import com.mob.gochat.model.Msg;
import com.mob.gochat.R;
import com.mob.gochat.utils.EmotionUtil;
import com.mob.gochat.utils.SpanStringUtils;
import com.mob.gochat.view.adapter.ChatAdapter;
import com.mob.gochat.view.adapter.EmotionAdapter;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.view.ui.widget.EmotionDecoration;
import com.mob.gochat.viewmodel.ChatViewModel;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private PanelSwitchHelper mHelper;
    @NonNull
    protected ActivityChatBinding binding;

    private RecyclerView mRVEmotion;
    private FloatingActionButton mFABDelete;
    private ImageButton mBtnVoice;
    TextView mVoiceTime;


    ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager chatManager;
    private EmotionAdapter emotionAdapter;
    long mStartVoiceTime;
    private final ChatHandle chatHandle = new ChatHandle(this);
    private ScheduledExecutorService scheduledExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        findViewById();
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.chatBtnSend.setEnabled(!TextUtils.isEmpty(binding.chatEdit.getText()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        binding.chatBtnSend.setEnabled(false);

        binding.chatEdit.addTextChangedListener(mTextWatcher);
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

    private void findViewById(){
        mRVEmotion = findViewById(R.id.chat_rv_emotion);
        mFABDelete = findViewById(R.id.chat_fab_emotion);
        mBtnVoice = findViewById(R.id.chat_panel_voice_btn);
        mVoiceTime = findViewById(R.id.chat_panel_voice_tv_time);
    }

    private void initOnClickListener() {
        //表情面板删除按钮点击事件
//        mFABDelete.setOnClickListener(v -> mETChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)));
        mFABDelete.setOnTouchListener(new EmotionDelBtnOnTouchListener());

        //发送按钮点击事件
        binding.chatBtnSend.setOnClickListener(v -> {
//            Msg msg = new Msg(binding.chatEdit.getText(),new Random().nextInt(2), Msg.TEXT);
//            chatViewModel.addMsg(msg);
            binding.chatEdit.setText("");
        });

        //聊天头像点击事件
        chatAdapter.addChildClickViewIds(R.id.chat_iv_item_fri, R.id.chat_iv_item_mine, R.id.chat_pic_item_fri, R.id.chat_pic_item_mine);
        chatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Log.d("click","----------click----------");
            if (view.getId() == R.id.chat_iv_item_fri){
                Intent intent = new Intent(ChatActivity.this, InfoActivity.class);
                startActivity(intent);
            }
            else if(view.getId() == R.id.chat_pic_item_fri || view.getId() == R.id.chat_pic_item_mine){
                Msg msg = (Msg)adapter.getItem(position);
                new XPopup.Builder(this)
                        .asImageViewer((ImageView) view, msg.getMsg(),new ImageLoader())
                        .isShowSaveButton(false)
                        .show();
            }
        });

        //表情面板中的表情的点击事件
        emotionAdapter.setOnItemClickListener((adapter, view, position) -> {
            String emotionName = EmotionUtil.getEmotionMap().keyAt(position);
            int curPosition = binding.chatEdit.getSelectionStart();
            StringBuilder sb = new StringBuilder(binding.chatEdit.getText().toString());
            sb.insert(curPosition, emotionName);
            binding.chatEdit.setText(SpanStringUtils.getEmotionContent(ChatActivity.this, binding.chatEdit, sb.toString()));
            binding.chatEdit.setSelection(curPosition + emotionName.length());
        });

        //照片图标点击事件
        binding.chatBtnPic.setOnClickListener(v -> {
            PicFragment picFragment = new PicFragment();
            picFragment.show(getSupportFragmentManager(),"PIC");
        });

        //录音按钮点击事件
        mBtnVoice.setOnTouchListener(new VoiceBtnOnTouchListener());
    }

    class ImageLoader implements XPopupImageLoader{

        @Override
        public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
            Glide.with(imageView)
                    .load(uri)
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL))
                    .into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            return null;
        }
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
        binding.chatRvChat.setLayoutManager(chatManager);
        chatAdapter = new ChatAdapter(chatViewModel.getChatData().getValue());
        binding.chatRvChat.setAdapter(chatAdapter);
    }

    private void updateUIData(){
        chatAdapter.setList(chatViewModel.getChatData().getValue());
        binding.chatRvChat.setAdapter(chatAdapter);
        chatManager.scrollToPosition(chatViewModel.getChatData().getValue().size()-1);
    }

    static class ChatHandle extends Handler {
        private final WeakReference<ChatActivity> chatActivityWeakReference;
        public ChatHandle(ChatActivity chatActivity){
            chatActivityWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ChatActivity chatActivity = chatActivityWeakReference.get();
            if(chatActivity != null){
                if(msg.what==1){
                    long time = System.currentTimeMillis() - chatActivity.mStartVoiceTime;
                    String min = time / 60000 >= 10 ? time / 60000 + "" : "0" + time / 60000;
                    String sec = time / 1000 % 60 >= 10 ? time / 1000 % 60 + "" : "0" + time / 1000 % 60;
                    String ms = time / 10 % 100 >= 10 ? time / 10 % 100 + "" : "0" + time / 10 % 100;
                    chatActivity.mVoiceTime.setText(min+":"+sec+":"+ms);
                }else if(msg.what == 2){
                    chatActivity.binding.chatEdit.dispatchKeyEvent(
                            new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        }
    }

    void startVoiceTime(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 1;
            chatHandle.sendMessage(msg);
        },0,10, TimeUnit.MILLISECONDS);
    }

    void stopVoiceTime(){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    void startEmotionDel(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 2;
            chatHandle.sendMessage(msg);
        },0,500, TimeUnit.MILLISECONDS);
    }

    void stopEmotionDel(){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    @SuppressWarnings("deprecation")
    class VoiceBtnOnTouchListener implements View.OnTouchListener{
        float x=0 , y=0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.setScaleX(0.98f);
                v.setScaleY(0.98f);
                mStartVoiceTime = System.currentTimeMillis();
                x = event.getX();
                y = event.getY();
                startVoiceTime();
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if(Math.sqrt(Math.pow(event.getX() - x,2)+Math.pow(event.getY() - y,2)) > 200){
                    Log.d("stop","----------stop---------");
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    stopVoiceTime();
                    mVoiceTime.setText("00:00:00");
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                v.setScaleX(1f);
                v.setScaleY(1f);
                stopVoiceTime();
                mVoiceTime.setText("00:00:00");
//                Msg msg = new Msg("",new Random().nextInt(2), Msg.VOICE);
//                chatViewModel.addMsg(msg);
            }
            return true;
        }
    }

    class EmotionDelBtnOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.setScaleX(0.98f);
                v.setScaleY(0.98f);
                startEmotionDel();
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                v.setScaleX(1f);
                v.setScaleY(1f);
                stopEmotionDel();
            }
            return true;
        }
    }

}