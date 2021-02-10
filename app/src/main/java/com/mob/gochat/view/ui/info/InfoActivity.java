package com.mob.gochat.view.ui.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.databinding.ActivityInfoBinding;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.view.ui.chat.ChatActivity;
import com.mob.gochat.viewmodel.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityInfoBinding binding;
    private Buddy buddy;
    private RoomDataBase dataBase;
    private ViewModel viewModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buddy = getIntent().getParcelableExtra("buddy");
        if(buddy == null){
            finish();
            return;
        }
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        dataBase = RoomDataBase.getInstance(this);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        binding.btnInfoGroup.setVisibility(View.VISIBLE);
        binding.btnInfoAdd.setVisibility(View.GONE);

        if(buddy.getRemarks() == null || buddy.getRemarks().equals("")){
            binding.tvInfoName.setText(buddy.getName());
            binding.tvInfoNickname.setVisibility(View.GONE);
        }else{
            binding.tvInfoName.setText(buddy.getRemarks());
            binding.tvInfoNickname.setText("昵称：" + buddy.getName());
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
            intent.putExtra("buddy",buddy);
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
        }
    }
}