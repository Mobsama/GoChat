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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mob.gochat.MainApp;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Msg;

import java.net.URI;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lombok.Getter;

public class SocketIOClientService extends Service {
    @Getter
    private Socket socket;
    private Gson gson;
    RoomDataBase dataBase;
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
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
        dataBase = RoomDataBase.getInstance(getBaseContext());
        initSocketIOClient();
//        if(Build.VERSION.SDK_INT >= 26){
//            startForeground(0, new Notification());
//        }
        return START_STICKY;
    }

    private void initSocketIOClient(){
        URI uri = URI.create("http://mobsan.top:3000");
        IO.Options options = IO.Options.builder()
                .setAuth(Collections.singletonMap("token", "12345"))
                .setReconnection(true)
                .setTimeout(20_000)
                .build();
        socket = IO.socket(uri, options);
        initSocketOn();
        socket.connect();
    }

    private void initSocketOn(){
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("chat", "----------------1111");
            MainApp.getInstance().setNet(socket.connected());
        });
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("chat", "----------------222");
            MainApp.getInstance().setNet(socket.connected());
        });
        socket.on("message", args -> {
            Log.d("chat", args[0].toString());
            Msg msg = gson.fromJson(args[0].toString(), Msg.class);
            new Thread(() -> dataBase.msgDao().insertMsg(msg)).start();
        });
    }

    @Override
    public void onDestroy() {
        socket.close();
        socket = null;
        super.onDestroy();
    }

}
