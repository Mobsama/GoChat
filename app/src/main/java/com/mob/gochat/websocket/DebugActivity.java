package com.mob.gochat.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.mob.gochat.MainApp;
import com.mob.gochat.databinding.ActivityDebugBinding;
import com.mob.gochat.http.Http;

import org.json.JSONException;
import org.json.JSONObject;

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