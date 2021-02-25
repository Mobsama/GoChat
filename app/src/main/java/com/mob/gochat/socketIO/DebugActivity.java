package com.mob.gochat.socketIO;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.mob.gochat.databinding.ActivityDebugBinding;

public class DebugActivity extends AppCompatActivity {

    ActivityDebugBinding binding;
    @SuppressLint("LogConditional")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn.setOnClickListener((view) -> {

        });
    }
}