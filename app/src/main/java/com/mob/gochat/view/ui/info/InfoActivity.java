package com.mob.gochat.view.ui.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityInfoBinding;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.view.base.ImageLoader;
import com.mob.gochat.view.ui.chat.ChatActivity;
import com.mob.gochat.viewmodel.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityInfoBinding binding;
    private Buddy buddy;
    private final String userId = MMKVUitl.getString(DataKeyConst.USER_ID);
    private ViewModel viewModel;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buddy = getIntent().getParcelableExtra("buddy");
        if(buddy == null){
            finish();
            return;
        }
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        viewModel.getBuddy(buddy.getId()).observe(this, b -> {
            if(b == null){
                binding.btnInfoGroup.setVisibility(View.GONE);
                binding.btnInfoAdd.setVisibility(View.VISIBLE);
            }else{
                binding.btnInfoGroup.setVisibility(View.VISIBLE);
                binding.btnInfoAdd.setVisibility(View.GONE);
            }
        });


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
            File file = new File(buddy.getAvatar());
            if(file.exists()){
                binding.ivInfoAvatar.setImageBitmap(BitmapFactory.decodeFile(buddy.getAvatar()));
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
        if(v == binding.btnInfoChat){
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("buddy",buddy.getId());
            intent.putExtra("user", userId);
            startActivity(intent);
            finish();
        }else if(v == binding.btnInfoDelete){
            String name;
            if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
                name = buddy.getName();
            }else{
                name = buddy.getRemarks();
            }
            new XPopup.Builder(this).asConfirm(name, "是否要删除"+name+"？",
                    () -> {
                        viewModel.deleteBuddy(buddy);
                        finish();
                    })
                    .show();
        }else if(v == binding.ivInfoAvatar){
            if(buddy.getAvatar() == null || buddy.getAvatar().equals("")){
                return;
            }
            if(!(new File(buddy.getAvatar())).exists()){
                return;
            }
            new XPopup.Builder(this)
                    .asImageViewer((ImageView) v, buddy.getAvatar(), new ImageLoader())
                    .isShowSaveButton(false)
                    .show();
        }
    }
}