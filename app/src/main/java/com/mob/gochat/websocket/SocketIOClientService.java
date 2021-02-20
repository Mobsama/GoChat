package com.mob.gochat.websocket;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mob.gochat.MainApp;

import java.net.URI;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lombok.Getter;

public class SocketIOClientService extends Service {
    @Getter
    private Socket socket;
    private final SocketIOClientBinder mBinder = new SocketIOClientBinder();

    public class SocketIOClientBinder extends Binder{
        @NonNull
        public SocketIOClientService getService() {
            return SocketIOClientService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d("service", "------------");
        initSocketIOClient();
        if(Build.VERSION.SDK_INT >= 26){
            startForeground(0, new Notification());
        }
        return START_STICKY;
    }

    private void initSocketIOClient(){
        URI uri = URI.create("http://192.168.215.156:3000");
        IO.Options options = IO.Options.builder()
                .setAuth(Collections.singletonMap("token", "12345"))
                .setReconnection(true)
                .setTimeout(20_000)
                .build();
        socket = IO.socket(uri, options);
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            MainApp.getInstance().setNet(socket.connected());
            System.out.println(socket.connected());
        });
        socket.on(Socket.EVENT_CONNECT, args -> {
            MainApp.getInstance().setNet(socket.connected());
            System.out.println(socket.connected());
        });
        socket.connect();
    }

    @Override
    public void onDestroy() {
        socket.close();
        socket = null;
        super.onDestroy();
    }

}
