package com.mob.gochat.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.mob.gochat.MainApp;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyWithMsgWrapper;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.Request;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.Callable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.socket.client.Socket;
import lombok.Getter;

public class ViewModel extends AndroidViewModel {
    @Getter
    private final RoomDataBase dataBase;
    private final Socket socket = MainApp.getInstance().getSocket();
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    @NonNull
    public LiveData<List<Msg>> getChatMsgData(@NonNull String id, @NonNull String userId){
        return dataBase.msgDao().getChatMsgList(id, userId);
    }

    @NonNull
    public LiveData<List<Buddy>> getMBuddyData(@NonNull String userId) {
        return dataBase.buddyDao().getBuddyList(userId);
    }

    @NonNull
    public LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsgData(@NonNull String userId) {
        return dataBase.buddyDao().getBuddyWithMsg(userId);
    }

    @NonNull
    public LiveData<Buddy> getBuddy(@NonNull String buddyId, @NonNull String userId){
        return dataBase.buddyDao().getBuddyById(buddyId, userId);
    }

    public void upsertBuddy(@NonNull Buddy buddy){
        mExecutor.execute(() -> dataBase.buddyDao().upsertBuddy(buddy));
    }

    public void updateBuddy(@NonNull Buddy buddy){
        mExecutor.execute(() -> dataBase.buddyDao().updateBuddy(buddy));
    }

    public void deleteBuddy(@NonNull Buddy buddy){
        mExecutor.execute(() -> dataBase.buddyDao().deleteBuddy(buddy));
    }

    public void insertMsg(@NonNull Msg msg){
        mExecutor.execute(() -> dataBase.msgDao().insertMsg(msg));

    }

    public void updateMsgStatue(@NonNull String buddyId, @NonNull String userId){
        mExecutor.execute(() -> dataBase.msgDao().updateMsgStatus(buddyId, userId));
    }

    public void deleteMsgWithBuddyId(@NonNull String buddyId, @NonNull String userId){
        mExecutor.execute(() -> dataBase.msgDao().deleteMsgWithBuddyId(buddyId, userId));
    }

    @NonNull
    public LiveData<List<Request>> getRequestData(@NonNull String userId){
        return dataBase.requestDao().getRequestList(userId);
    }

    @NonNull
    public LiveData<Integer> getUntreatedRequestNum(@NonNull String userId){
        return dataBase.requestDao().getRequestUntreatedNum(userId);
    }

    public void insertRequest(@NonNull Request request){
        mExecutor.execute(() -> dataBase.requestDao().insertRequest(request));
    }

    public void updateRequest(@NonNull Request request){
        mExecutor.execute(() -> dataBase.requestDao().updateRequest(request));
    }

    public void sendMsg(@NonNull Context context, @NonNull Msg msg){
        switch (msg.getMsgType()){
            case Msg.TEXT:
                sendText(context, msg);
                break;
            case Msg.PIC:
            case Msg.VOICE:
                Http.sendFile(msg.getMsgType(), msg.getMsg(), s -> {
                    sendText(context, msg);
                });
                break;
        }
    }

    private void sendText(Context context, Msg msg){
        Http.sendText(msg, s -> {
            if (s == 200){
                insertMsg(msg);
            }else if(s == 300){
                Date date = new Date(System.currentTimeMillis());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                String uuid = UUID.randomUUID().toString();
                Msg err = new Msg(uuid, msg.getBuddyId(), msg.getUserId(), Msg.OTHER, Msg.TEXT, "对方还不是你的好友哦。", format.format(date));
                insertMsg(err);
            }else{
                ThreadToast(context, "发送失败");
            }
        });
    }

    public void sendRequest(@NonNull Context context, @NonNull Request request){
        Http.sendRequest(request, s -> {
            if(s == 300){
                ThreadToast(context, "你今天已经发送过请求了哦");
            }else if(s == 400) {
                ThreadToast(context, "他已经是你的好友了哦");
            }else{
                ThreadToast(context, "发送成功");
            }
        });
    }

    public void addNewBuddy(@NonNull String userId, @NonNull String buddyId, @NonNull Callable<Boolean> callable){
        Http.addNewBuddy(userId, buddyId, s -> callable.call(s == 200));
    }


    public void isBuddy(String buddyId, String userId, @NonNull Callable<Boolean> callable){
        Http.isBuddy(userId, buddyId, s -> callable.call(s == 200));
    }

    public void deleteBuddy(String buddyId, String userId, @NonNull Callable<Boolean> callable){
        Http.deleteBuddy(userId, buddyId, s -> callable.call(s == 200));
    }

    private static void ThreadToast(Context context, String msg) {
        Looper.prepare();
        ToastUtil.showMsg(context, msg);
        Looper.loop();
    }
}
