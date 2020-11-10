package com.mob.gochat.WebSocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class JWebSocketClientService extends Service {
    public JWebSocketClient client;
    public boolean connFlag = false;
    private final JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocketClient();
        mHandle.postDelayed(mHeartBeatRunnable,Heart_Beat_SPACE);
        return START_STICKY;
    }

    private void initSocketClient(){
        URI uri = URI.create("ws://10.0.2.2:8181");
        client = new JWebSocketClient(uri){
            @Override
            public void onMessage(String message) {

            }

        };
        startConnect();
    }

    private void startConnect(){
        new Thread(){
            @Override
            public void run() {
                try {
                    if(client.connectBlocking(1000, TimeUnit.MILLISECONDS)){
                        connFlag = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void closeConnect() {
        try {
            if (client != null) client.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    private static final long Heart_Beat_SPACE = 1000 * 5;
    private final Handler mHandle = new Handler();
    private final Runnable mHeartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if(client != null){
                if(client.isClosed() || !connFlag) {
                    reconnectWebSocket();
                }
            }else{
                connFlag = false;
                client = null;
                initSocketClient();
            }
            mHandle.postDelayed(this,Heart_Beat_SPACE);
        }
    };
    private void reconnectWebSocket(){
        mHandle.removeCallbacks(mHeartBeatRunnable);
        new Thread(){
            @Override
            public void run() {
                try {
                    if(client.reconnectBlocking()){
                        connFlag = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
