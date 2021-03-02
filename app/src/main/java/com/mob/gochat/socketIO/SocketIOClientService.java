package com.mob.gochat.socketIO;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mob.gochat.MainActivity;
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
import com.mob.gochat.view.ui.add.NewBuddyActivity;
import com.mob.gochat.view.ui.chat.ChatActivity;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;

public class SocketIOClientService extends Service {
    @Getter
    private Socket socket;
    private Gson gson;
    RoomDataBase dataBase;
    private NotificationManager mManager;
    private final SocketIOClientBinder mBinder = new SocketIOClientBinder();
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private static boolean flag = false;
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
        URI uri = URI.create("http://mobsan.top:3001");
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
            if(MainApp.getInstance().getCurrBuddy() == null || !MainApp.getInstance().getCurrBuddy().equals(msg.getBuddyId())){
                flag = true;
                sendNotifications(msg);
            }
            switch (msg.getMsgType()){
                case Msg.TEXT:
                    mExecutor.execute(() -> dataBase.msgDao().insertMsg(msg));
                    break;
                case Msg.PIC:
                case Msg.VOICE:
                    Http.getFile(getBaseContext(), msg.getMsgType(), msg.getMsg(), s -> mExecutor.execute(() -> dataBase.msgDao().insertMsg(msg)));
                    break;
            }
        });
        socket.on("request", args -> {
            Request request = gson.fromJson(args[0].toString(), Request.class);
            sendNotifications(request);
            if(request.getBuddyAvatar() == null){
                mExecutor.execute(() -> dataBase.requestDao().insertRequest(request));
            }else{
                Http.getFile(getBaseContext(), Msg.PIC, request.getBuddyAvatar(), s -> mExecutor.execute(() -> dataBase.requestDao().insertRequest(request)));
            }
        });
        socket.on("token", args -> {
            PostRequest postRequest = gson.fromJson(args[0].toString(), PostRequest.class);
            if(postRequest.getStatus() == 200){
                MMKVUitl.clear(DataKeyConst.USER_ID);
                MMKVUitl.save(DataKeyConst.USER_ID, postRequest.getMessage());
            }else{
                MMKVUitl.clear(DataKeyConst.TOKEN);
                Intent intent = new Intent("gochat.broadcase.LOGOUT");
                MainActivity.getInstance().getLocalBroadcastManager().sendBroadcast(intent);
            }
        });
        socket.on("buddy", args -> {
            Buddy buddy = gson.fromJson(args[0].toString(), Buddy.class);
            if(buddy.getAvatar() == null){
                mExecutor.execute(() -> dataBase.buddyDao().upsertBuddy(buddy));
            }else{
                Http.getFile(getBaseContext(), Msg.PIC, buddy.getAvatar(), s -> mExecutor.execute(() -> dataBase.buddyDao().upsertBuddy(buddy)));
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
        String text = msg.getMsg();
        if(msg.getMsgType() == Msg.PIC){
            text = "[图片]";
        }else if(msg.getMsgType() == Msg.VOICE){
            text = "[语音]";
        }
        String finalText = text;
        new Handler(Looper.getMainLooper()).post(() -> dataBase.buddyDao().getBuddyById(msg.getBuddyId(), msg.getUserId()).observeForever(buddy -> {
            if(flag && buddy!=null){
                String title = buddy.getName();
                if(buddy.getRemarks() != null && !buddy.getRemarks().equals("")){
                    title = buddy.getRemarks();
                }
                builder.setSmallIcon(R.mipmap.go)
                        .setContentTitle(title)
                        .setContentText(finalText)
                        .setAutoCancel(true);
                File file = new File(getFilesDir().getAbsolutePath() + "/pic/" + buddy.getAvatar());
                if(file.exists()){
                    builder.setLargeIcon(BitmapFactory.decodeFile(file.getPath()));
                }else{
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.go));
                }
                Intent intent = new Intent(SocketIOClientService.this, ChatActivity.class);
                intent.putExtra("user", msg.getUserId());
                intent.putExtra("buddy", msg.getBuddyId());
                PendingIntent pendingIntent = PendingIntent.getActivity(SocketIOClientService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();
                mManager.notify(1, notification);
                flag = false;
            }
        }));
    }

    private void sendNotifications(Request request){
        String channelId = "notification";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName  = "通知";
            createNotificationChannel(channelId, channelName, NotificationManagerCompat.IMPORTANCE_MAX);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.mipmap.go)
                .setContentTitle(request.getBuddyName())
                .setContentText(request.getBuddyName() + "请求添加你为好友")
                .setAutoCancel(true);
        File file = new File(getFilesDir().getAbsolutePath() + "/pic/" + request.getBuddyAvatar());
        if(file.exists()){
            builder.setLargeIcon(BitmapFactory.decodeFile(file.getPath()));
        }else{
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.go));
        }
        Intent intent = new Intent(SocketIOClientService.this, NewBuddyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SocketIOClientService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
