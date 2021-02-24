package com.mob.gochat.websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mob.gochat.MainApp;
import com.mob.gochat.R;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
import com.mob.gochat.model.Request;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.view.base.Callable;
import com.mob.gochat.view.ui.chat.ChatActivity;

import java.io.IOException;
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
    private NotificationManager mManager;
    private final SocketIOClientBinder mBinder = new SocketIOClientBinder();

    private LiveData<Buddy> buddy,user;

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
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initSocketIOClient();
//        if(Build.VERSION.SDK_INT >= 26){
//            startForeground(0, new Notification());
//        }
        return START_STICKY;
    }

    private void initSocketIOClient(){
        URI uri = URI.create("http://mobsan.top:3000");
        IO.Options options = IO.Options.builder()
                .setAuth(Collections.singletonMap(DataKeyConst.TOKEN, MMKVUitl.getString(DataKeyConst.TOKEN)))
                .setReconnection(true)
                .setTimeout(20_000)
                .build();
        socket = IO.socket(uri, options);
        initSocketOn();
        socket.connect();
    }

    private void initSocketOn(){
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            MainApp.getInstance().setNet(socket.connected());
        });
        socket.on(Socket.EVENT_CONNECT, args -> {
            MainApp.getInstance().setNet(socket.connected());
        });
        socket.on("message", args -> {
            Msg msg = gson.fromJson(args[0].toString(), Msg.class);
            switch (msg.getMsgType()){
                case Msg.TEXT:
                    sendNotifications(msg);
                    new Thread(() -> dataBase.msgDao().insertMsg(msg)).start();
                    break;
                case Msg.PIC:
                case Msg.VOICE:
                    Http.getFile(msg.getMsgType(), msg.getMsg(), s -> new Thread(() -> dataBase.msgDao().insertMsg(msg)).start());
                    break;
            }
        });
        socket.on("request", args -> {
            Request request = gson.fromJson(args[0].toString(), Request.class);
            Http.getFile(Msg.PIC, request.getBuddyAvatar(), s -> new Thread(() -> dataBase.requestDao().insertRequest(request)).start());
        });
        socket.on("token", args -> {
            PostRequest postRequest = gson.fromJson(args[0].toString(), PostRequest.class);
            if(postRequest.getStatus() == 200){
                MMKVUitl.save(DataKeyConst.USER_ID, postRequest.getMessage());
            }else{
                //token过期,强制退出
            }
        });
    }

    @Override
    public void onDestroy() {
        socket.close();
        socket = null;
        super.onDestroy();
    }

    private void sendNotifications(Msg msg){
        String channelId = "message";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName  = "消息";
            createNotificationChannel(channelId, channelName, NotificationManagerCompat.IMPORTANCE_MAX);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.mipmap.go)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.go))
            .setContentTitle(msg.getBuddyId())
            .setContentText(msg.getMsg())
            .setAutoCancel(true);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user", msg.getUserId());
        intent.putExtra("buddy", msg.getBuddyId());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        mManager.notify(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance){
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        mManager.createNotificationChannel(channel);
    }

}
