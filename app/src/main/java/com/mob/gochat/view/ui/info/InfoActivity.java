package com.mob.gochat.view.ui.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.MainApp;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityInfoBinding;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
import com.mob.gochat.model.Request;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.ImageLoader;
import com.mob.gochat.view.ui.chat.ChatActivity;
import com.mob.gochat.viewmodel.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityInfoBinding binding;
    private Buddy buddy;
    private String userId;
    private ViewModel viewModel;
    private boolean isBuddy;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buddy = getIntent().getParcelableExtra("buddy");
        isBuddy = getIntent().getBooleanExtra("isBuddy", false);
        if(buddy == null){
            finish();
            return;
        }
        userId = MMKVUitl.getString(DataKeyConst.USER_ID);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        onObserve();
        updateBuddy();
        initOnClick();
    }

    private void onObserve(){
        viewModel.getBuddy(buddy.getId(), userId).observe(this, b -> {
            if(isBuddy && b != null){
                buddy = b;
                binding.btnInfoChat.setVisibility(View.VISIBLE);
                binding.btnInfoDelete.setVisibility(View.VISIBLE);
                binding.btnInfoAdd.setVisibility(View.GONE);
            }else if(b != null){
                buddy = b;
                binding.btnInfoChat.setVisibility(View.GONE);
                binding.btnInfoDelete.setVisibility(View.VISIBLE);
                binding.btnInfoAdd.setVisibility(View.VISIBLE);
            }else{
                binding.btnInfoChat.setVisibility(View.GONE);
                binding.btnInfoDelete.setVisibility(View.GONE);
                binding.btnInfoAdd.setVisibility(View.VISIBLE);
            }
            initView();
        });
    }

    private void updateBuddy(){
        Http.getUser(this, buddy.getId(), buddy.getUser(), str -> {
            PostRequest req = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
            if(req.getStatus() == 200){
                Buddy b = MainApp.getInstance().getGson().fromJson(req.getMessage(), Buddy.class);
                buddy.setName(b.getName());
                buddy.setGender(b.getGender());
                buddy.setBirth(b.getBirth());
                buddy.setAddress(b.getAddress());
                if(b.getAvatar() == null){
                    viewModel.updateBuddy(buddy);
                }else{
                    Http.getFile(this, Msg.PIC, b.getAvatar(), path -> {
                        buddy.setAvatar(b.getAvatar());
                        viewModel.updateBuddy(buddy);
                    });
                }
            }
        });
    }

    private void initView(){
        if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
            binding.tvInfoName.setText(buddy.getName());
            binding.tvInfoNickname.setVisibility(View.GONE);
        }else{
            binding.tvInfoName.setText(buddy.getRemarks());
            binding.tvInfoNickname.setText("昵称：" + buddy.getName());
        }

        if(buddy.getAvatar() == null || buddy.getAvatar().equals("")){
            binding.ivInfoAvatar.setImageDrawable(getDrawable(R.drawable.buddy));
        }else{
            File file = new File(getFilesDir().getAbsolutePath() + "/pic/" +buddy.getAvatar());
            if(file.exists()){
                binding.ivInfoAvatar.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
            }else{
                binding.ivInfoAvatar.setImageDrawable(getDrawable(R.drawable.buddy));
            }
        }
        binding.tvInfoNumber.setText("GoChat号：" + buddy.getId());
        if(buddy.getGender() == 0){
            binding.tvInfoGender.setText("保密");
        }else if(buddy.getGender() == 1) {
            binding.tvInfoGender.setText("男");
        }else if(buddy.getGender() == 2) {
            binding.tvInfoGender.setText("女");
        }
        binding.tvInfoBirthday.setText(buddy.getBirth());
        binding.tvInfoAddress.setText(buddy.getAddress());
    }

    private void initOnClick(){
        binding.btnInfoAdd.setOnClickListener(this);
        binding.btnInfoChat.setOnClickListener(this);
        binding.btnInfoDelete.setOnClickListener(this);
        binding.ivInfoAvatar.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 16908332:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_info_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("buddy",buddy.getId());
                intent.putExtra("user", userId);
                startActivity(intent);
                InfoActivity.this.finish();
                break;
            case R.id.btn_info_delete:
                String name;
                if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
                    name = buddy.getName();
                }else{
                    name = buddy.getRemarks();
                }
                new XPopup.Builder(this).asConfirm(name, "是否要删除"+name+"？", () -> {
                            if(isBuddy){
                                viewModel.deleteBuddy(buddy.getId(), buddy.getUser(), s -> {
                                    if(s){
                                        viewModel.deleteBuddy(buddy);
                                    }
                                });
                            }else{
                                viewModel.deleteBuddy(buddy);
                            }
                            InfoActivity.this.finish();
                        })
                        .show();
                break;
            case R.id.btn_info_add:
                viewModel.getBuddy(userId, userId).observe(this, user -> {
                    String uuid = UUID.randomUUID().toString();
                    Date date = new Date(System.currentTimeMillis());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    Request request = new Request(uuid, buddy.getId(), user.getId(), user.getName(), user.getAvatar(), format.format(date));
                    viewModel.sendRequest(request, s -> {
                        runOnUiThread(() -> {
                            if(s == 300){
                                ToastUtil.showMsg(InfoActivity.this, "你今天已经发送过请求了哦");
                            }else if(s == 400) {
                                ToastUtil.showMsg(InfoActivity.this, "他已经是你的好友了哦");
                            }else{
                                ToastUtil.showMsg(InfoActivity.this, "发送成功");
                            }
                        });

                    });
                });
                break;
            case R.id.iv_info_avatar:
                if(buddy.getAvatar() == null || buddy.getAvatar().equals("")){
                    return;
                }
                File file = new File(getFilesDir().getAbsolutePath() + "/pic/" +buddy.getAvatar());
                if(!file.exists()){
                    return;
                }
                new XPopup.Builder(this)
                        .asImageViewer((ImageView) v, file.getPath(), new ImageLoader())
                        .isShowSaveButton(false)
                        .show();
                break;
        }
    }
}