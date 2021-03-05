package com.mob.gochat.view.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.effective.android.panel.PanelSwitchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.MainApp;
import com.mob.gochat.audio.AudioPlayManager;
import com.mob.gochat.audio.AudioRecordManager;
import com.mob.gochat.audio.IAudioPlayListener;
import com.mob.gochat.audio.IAudioRecordListener;
import com.mob.gochat.databinding.ActivityChatBinding;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.R;
import com.mob.gochat.utils.EmotionUtil;
import com.mob.gochat.utils.FileUtil;
import com.mob.gochat.utils.SpanStringUtil;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.utils.VibrateUtil;
import com.mob.gochat.view.adapter.ChatAdapter;
import com.mob.gochat.view.adapter.EmotionAdapter;
import com.mob.gochat.view.base.Callable;
import com.mob.gochat.view.base.ImageLoader;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.view.ui.widget.EmotionDecoration;
import com.mob.gochat.viewmodel.ViewModel;


import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

public class ChatActivity extends AppCompatActivity {

    private PanelSwitchHelper mHelper;
    protected ActivityChatBinding binding;

    private RecyclerView mRVEmotion;
    private FloatingActionButton mFABDelete;
    private ImageButton mBtnVoice;
    @Getter
    private String buddyId;
    private String userId;
    private Buddy buddy, user;
    private ViewModel viewModel;
    private List<Msg> msgs = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private LinearLayoutManager chatManager;
    private EmotionAdapter emotionAdapter;
    private final ChatHandle chatHandle = new ChatHandle(this);
    private ScheduledExecutorService scheduledExecutor;
    private Gson gson;
    private String curUUID;
    long mStartVoiceTime;
    TextView mVoiceTime, mVoiceTip;
    View voiceAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById();
        buddyId = getIntent().getStringExtra("buddy");
        userId = getIntent().getStringExtra("user");
        if(buddyId == null || userId == null){
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        initStatue();
        initAudio();
        initEmotionRecycleView();
        viewModel.getBuddy(buddyId, userId).observe(this, b -> {
            buddy = b;
            viewModel.getBuddy(userId, userId).observe(this, u -> {
                user = u;
                initTitle();
                initChatRecycleView();
                initOnClickListener();
                viewModel.getChatMsgData(buddyId, userId).observe(this, msgs -> {
                    this.msgs.clear();
                    this.msgs.addAll(msgs);
                    viewModel.updateMsgStatue(buddy.getId(), userId);
                    chatAdapter.notifyDataSetChanged();
                    chatManager.scrollToPosition(this.msgs.size()-1);
                });
            });
        });
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
    }

    private void initStatue(){
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
        binding.chatBtnSend.setEnabled(false);
        binding.chatEdit.addTextChangedListener(mTextWatcher);
    }

    private void initAudio(){
        AudioRecordManager.getInstance(this).setAudioSavePath("");
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {
            @Override
            public void initTipView() {
                voiceAnim.setVisibility(View.VISIBLE);
            }

            @Override
            public void setTimeoutTipView(int counter) { }

            @Override
            public void setRecordingTipView() { }

            @Override
            public void setAudioShortTipView() {
                mVoiceTip.setText("录音时间太短啦");
            }

            @Override
            public void setCancelTipView() { }

            @Override
            public void destroyTipView() { }

            @Override
            public void onStartRecord() {
                mVoiceTip.setText("松开按钮发送录音,滑动取消录音");
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                voiceAnim.setVisibility(View.GONE);
                sendMsg(Msg.VOICE, curUUID + ".voice");
            }

            @Override
            public void onAudioDBChanged(int db) {
                float p = db * 0.15f;
                voiceAnim.setScaleX(p);
                voiceAnim.setScaleY(p);
            }
        });
    }

    private void initTitle(){
        if(buddy == null || user == null){
            finish();
            return;
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
            actionBar.setTitle(buddy.getName());
        }else{
            actionBar.setTitle(buddy.getRemarks());
        }
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
    protected void onResume() {
        MainApp.getInstance().setCurrBuddy(buddyId);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MainApp.getInstance().setCurrBuddy(null);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 16908332) {
            if (mHelper != null) {
                mHelper.hookSystemBackByPanelSwitcher();
            }
            AudioPlayManager.getInstance().stopPlay();
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
        mVoiceTip = findViewById(R.id.chat_panel_voice_tv_tip);
        voiceAnim = findViewById(R.id.chat_panel_voice_anim);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnClickListener() {
        //表情面板删除按钮点击事件
        mFABDelete.setOnTouchListener(new EmotionDelBtnOnTouchListener());

        //发送按钮点击事件
        binding.chatBtnSend.setOnClickListener(v -> {
            curUUID = UUID.randomUUID().toString();
            if(MainApp.getInstance().isNet()){
                sendMsg(Msg.TEXT, binding.chatEdit.getText().toString());
                binding.chatEdit.setText("");
            }else{
                ToastUtil.showMsg(this,"无法连接服务器");
            }

        });

        //聊天消息点击事件
        chatAdapter.addChildClickViewIds(R.id.chat_iv_avatar_fri, R.id.chat_pic_item_fri, R.id.chat_pic_item_mine, R.id.chat_voice_item_mine, R.id.chat_voice_item_fri);
        chatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.chat_iv_avatar_fri){
                Intent intent = new Intent(ChatActivity.this, InfoActivity.class);
                intent.putExtra("buddy", buddy);
                if(MainApp.getInstance().isNet()){
                    viewModel.isBuddy(buddy.getId(), buddy.getUser(), is -> {
                        intent.putExtra("isBuddy", is);
                        startActivity(intent);
                    });
                }else{
                    intent.putExtra("isBuddy", true);
                    startActivity(intent);
                }
            }
            else if(view.getId() == R.id.chat_pic_item_fri || view.getId() == R.id.chat_pic_item_mine){
                Msg msg = (Msg)adapter.getItem(position);
                new XPopup.Builder(this)
                        .asImageViewer((ImageView) view, getFilesDir().getAbsolutePath() + "/pic/" + msg.getMsg(), new ImageLoader())
                        .isShowSaveButton(false)
                        .show();
            }
            else if(view.getId() == R.id.chat_voice_item_mine || view.getId() == R.id.chat_voice_item_fri){
                Msg msg = (Msg)adapter.getItem(position);
                AudioPlayManager.getInstance().startPlay(this, Uri.parse(getFilesDir().getAbsolutePath() + "/audio/" + msg.getMsg()), new IAudioPlayListener() {
                    @Override
                    public void onStart(Uri var1) {
                        binding.chatVoiceTip.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStop(Uri var1) {

                    }

                    @Override
                    public void onComplete(Uri var1) {
                        binding.chatVoiceTip.setVisibility(View.GONE);
                    }
                });
            }
        });

        //表情面板中的表情的点击事件
        emotionAdapter.setOnItemClickListener((adapter, view, position) -> {
            String emotionName = EmotionUtil.getEmotionMap().keyAt(position);
            int curPosition = binding.chatEdit.getSelectionStart();
            StringBuilder sb = new StringBuilder(binding.chatEdit.getText().toString());
            sb.insert(curPosition, emotionName);
            binding.chatEdit.setText(SpanStringUtil.getEmotionContent(ChatActivity.this, binding.chatEdit, sb.toString()));
            binding.chatEdit.setSelection(curPosition + emotionName.length());
        });

        //照片图标点击事件
        binding.chatBtnPic.setOnClickListener(v -> {
            PicFragment picFragment = new PicFragment();
            Callable<String> callable = path -> {
                curUUID = UUID.randomUUID().toString();
                String dir_path = getFilesDir().getAbsolutePath() + "/pic/";
                File file = new File(dir_path);
                if(!file.exists()){
                    file.mkdirs();
                }
                String suffix = path.substring(path.lastIndexOf("."));
                file = new File(dir_path + curUUID + suffix );
                file.createNewFile();
                FileUtil.copyFile(path, file.getPath());
                sendMsg(Msg.PIC, file.getName());
            };
            picFragment.show(getSupportFragmentManager(),"PIC", callable, "发送");
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

    @SuppressLint("ClickableViewAccessibility")
    private void initChatRecycleView(){
        chatManager = new LinearLayoutManager(this);
        chatManager.setStackFromEnd(true);
        binding.chatRvMsg.setLayoutManager(chatManager);
        chatAdapter = new ChatAdapter(msgs, buddy, user);
        binding.chatRvMsg.setAdapter(chatAdapter);
        binding.chatRvMsg.setOnTouchListener((v, event) -> {
            mHelper.resetState();
            return false;
        });
    }

    static class ChatHandle extends Handler {
        private final WeakReference<ChatActivity> chatActivityWeakReference;
        public ChatHandle(ChatActivity chatActivity){
            chatActivityWeakReference = new WeakReference<>(chatActivity);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ChatActivity chatActivity = chatActivityWeakReference.get();
            if(chatActivity != null){
                if(msg.what==1){
                    long time = System.currentTimeMillis() - chatActivity.mStartVoiceTime;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("mm:ss:SS");
                    chatActivity.mVoiceTime.setText(format.format(new Date(time)));
                    if(time > 60 * 1000){
                        chatActivity.stopVoice(stop);
                        chatActivity.mVoiceTip.setText("录音最长60s");
                    }
                    if(chatActivity.status == cancel || chatActivity.status == stop){
                        chatActivity.mVoiceTime.setText("00:00:00");
                    }
                }else if(msg.what == 2){
                    chatActivity.binding.chatEdit.dispatchKeyEvent(
                            new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        }
    }

    void startVoice(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 1;
            chatHandle.sendMessage(msg);
        },0,10, TimeUnit.MILLISECONDS);
        VibrateUtil.playVibrate(this, false);
        AudioRecordManager.getInstance(ChatActivity.this).startRecord(curUUID);
    }

    @SuppressLint("SetTextI18n")
    void stopVoice(int status){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
        if(status == stop){
            mVoiceTip.setText("按住按钮进行录音");
            this.status = stop;
            AudioRecordManager.getInstance(ChatActivity.this).stopRecord();
        }else if(status == cancel){
            mVoiceTip.setText("已取消录音");
            this.status = cancel;
            AudioRecordManager.getInstance(ChatActivity.this).destroyRecord();
        }
        VibrateUtil.playVibrate(this, false);
        mVoiceTime.setText("00:00:00");
    }

    void startEmotionDel(){
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = 2;
            chatHandle.sendMessage(msg);
        },0,300, TimeUnit.MILLISECONDS);
    }

    void stopEmotionDel(){
        if(scheduledExecutor != null){
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    final static int start = 0, stop = 1, cancel = 2;
    int status = stop;
    @SuppressLint("ClickableViewAccessibility")
    class VoiceBtnOnTouchListener implements View.OnTouchListener{
        float x=0 , y=0;
        boolean isCancel = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                touchAnim(v, 0.98f);
                mStartVoiceTime = System.currentTimeMillis();
                isCancel = false;
                x = event.getX();
                y = event.getY();
                curUUID = UUID.randomUUID().toString();
                status = start;
                startVoice();
            }else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                touchAnim(v, 1f);
                if(status == start){
                    stopVoice(stop);
                }
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if(Math.sqrt(Math.pow(event.getX() - x,2)+Math.pow(event.getY() - y,2)) > 100 && !isCancel){
                    mVoiceTip.setText("继续滑动取消录音");
                } else if(Math.sqrt(Math.pow(event.getX() - x,2)+Math.pow(event.getY() - y,2)) < 50 && !isCancel){
                    mVoiceTip.setText("松开按钮发送录音,滑动取消录音");
                }
                if(Math.sqrt(Math.pow(event.getX() - x,2)+Math.pow(event.getY() - y,2)) > 200 && !isCancel){
                    isCancel = true;
                    touchAnim(v, 1f);
                    if(status == start){
                        stopVoice(cancel);
                    }
                }
            }
            return true;
        }
    }

    private void sendMsg(int msgType, String message){
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Msg msg = new Msg(curUUID, buddy.getId(), user.getId(), Msg.MINE, msgType, message, format.format(date));
        viewModel.sendMsg(msg, flag -> {
            if(!flag){
                ToastUtil.showMsg(this, "发送失败");
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    class EmotionDelBtnOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                touchAnim(v, 0.98f);
                startEmotionDel();
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                touchAnim(v, 1f);
                stopEmotionDel();
            }
            return true;
        }
    }

    void touchAnim(View v, float proportion){
        v.setScaleX(proportion);
        v.setScaleY(proportion);
    }

    @Override
    protected void onDestroy() {
        AudioPlayManager.getInstance().stopPlay();
        super.onDestroy();
    }
}