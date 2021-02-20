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

import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.websocket.SocketIOClientService;

import io.socket.client.Socket;
import lombok.Getter;
import lombok.Setter;

public class MainApp extends Application {
    @Getter
    private static MainApp instance;
    @Getter
    Socket socket;
    @Getter
    @Setter
    boolean isNet = true;
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
        MMKVUitl.initialize(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isIgnoringBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }
        startService();
        bindService();
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

    private void startService(){
        if(Build.VERSION.SDK_INT >= 26){
            startForegroundService(new Intent(getBaseContext(), SocketIOClientService.class));
        }else {
            startService(new Intent(getBaseContext(), SocketIOClientService.class));
        }
    }

    private void bindService(){
        bindService(new Intent(getBaseContext(), SocketIOClientService.class), serviceConnection, BIND_AUTO_CREATE);
    }
}
