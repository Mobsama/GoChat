package com.mob.gochat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.socketIO.SocketIOClientService;

import java.io.File;

import io.socket.client.Socket;
import lombok.Getter;
import lombok.Setter;

public class MainApp extends Application {
    @Getter
    private static MainApp instance;
    @Getter
    private LifecycleOwner lifecycleOwner;
    @Getter
    Socket socket;
    @Getter
    @Setter
    boolean isNet = true;
    @Getter
    private Gson gson;
    @Setter
    @Getter
    private String currBuddy = null;
    SocketIOClientService.SocketIOClientBinder binder;
    SocketIOClientService socketIOClientService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (SocketIOClientService.SocketIOClientBinder) service;
            socketIOClientService = binder.getService();
            socket = socketIOClientService.getSocket();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        lifecycleOwner = this.getLifecycleOwner();
        MMKVUitl.initialize(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isIgnoringBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }
        initGson();
    }

    private void initGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try{
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+ getPackageName()));
            startActivity(intent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    public void stopSocketIO(){
        socket.disconnect();
        socket.close();
        stopService(new Intent(getBaseContext(), SocketIOClientService.class));
        unbindService(serviceConnection);
    }

    public void startSocketIO(){
        startService();
        bindService();
    }

    private void startService(){
        startService(new Intent(getBaseContext(), SocketIOClientService.class));
    }

    private void bindService(){
        bindService(new Intent(getBaseContext(), SocketIOClientService.class), serviceConnection, BIND_AUTO_CREATE);
    }
}
