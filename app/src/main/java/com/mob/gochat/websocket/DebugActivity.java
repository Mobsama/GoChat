package com.mob.gochat.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.mob.gochat.MainApp;
import com.mob.gochat.databinding.ActivityDebugBinding;

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
            Log.d("click","---------------");
            String[] msg = {"123"};
            if(MainApp.getInstance().isNet()){
                MainApp.getInstance().getSocket().emit("hello", msg, (args) -> {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        Log.d("chat",object.getString("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

        });
    }
}