package com.mob.gochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mob.gochat.view.ui.login.LoginActivity;
import com.mob.gochat.utils.HttpClientUtil;
import com.mob.gochat.websocket.JWebSocketClient;
import com.mob.gochat.websocket.JWebSocketClientService;

import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;

    private SharedPreferences mSharedPreferences;
    private ViewPager2 mVPMain;
    private BottomNavigationView mBNVMain;


    private String number;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (JWebSocketClientService.JWebSocketClientBinder) service;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initUser();
        initView();
        startJWebSClientService();
        bindService();
    }

    private void initUser(){
        mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        String token = mSharedPreferences.getString("token",null);
        if(token == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
//        new Thread(() -> {
//            HashMap<String,String> paramsMap = new HashMap<>();
//            String result = HttpClientUtil.HttpPost("http://10.0.2.2:8182/token",token,paramsMap);
//            JSONObject resultJson = (JSONObject) JSON.parse(result);
//            if(resultJson.getInteger("code") == 0){
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }else{
//                number = resultJson.getString("msg");
//            }
//        }).start();
    }

    private void initView(){

    }

    private void bindService() {
        Intent bindIntent = new Intent(this, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(this, JWebSocketClientService.class);
        startService(intent);
        bindService();
    }

}