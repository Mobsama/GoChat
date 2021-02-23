package com.mob.gochat.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
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
import com.mob.gochat.utils.ThreadUtils;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.Callable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.socket.client.Socket;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;
    private final Socket socket = MainApp.getInstance().getSocket();

    private final static String userId = MMKVUitl.getString(DataKeyConst.USER_ID);

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    @NonNull
    public LiveData<List<Msg>> getChatMsgData(String id){
        return dataBase.msgDao().getChatMsgList(id, userId);
    }

    @NonNull
    public LiveData<List<Buddy>> getMBuddyData() {
        return dataBase.buddyDao().getBuddyList(userId);
    }

    @NonNull
    public LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsgData() {
        return dataBase.buddyDao().getBuddyWithMsg(userId);
    }

    @NonNull
    public LiveData<Buddy> getBuddy(@NonNull String buddyId){
        return dataBase.buddyDao().getBuddyById(buddyId, userId);
    }

    public void insertBuddy(@NonNull Buddy buddy){
        new Thread(() -> dataBase.buddyDao().insertBuddy(buddy)).start();
    }

    public void updateBuddy(@NonNull Buddy buddy){
        new Thread(() -> dataBase.buddyDao().updateBuddy(buddy)).start();
    }

    public void deleteBuddy(@NonNull Buddy buddy){
        new Thread(() -> dataBase.buddyDao().deleteBuddy(buddy)).start();
    }

    public void insertMsg(@NonNull Msg msg){
        new Thread(() -> dataBase.msgDao().insertMsg(msg)).start();

    }

    public void updateMsgStatue(@NonNull String buddyId){
        new Thread(() -> dataBase.msgDao().updateMsgStatus(buddyId, userId)).start();
    }

    public void deleteMsgWithBuddyId(@NonNull String buddyId){
        new Thread(() -> dataBase.msgDao().deleteMsgWithBuddyId(buddyId, userId)).start();
    }

    @NonNull
    public LiveData<List<Request>> getRequestData(){
        return dataBase.requestDao().getRequestList(userId);
    }

    @NonNull
    public LiveData<Integer> getUntreatedRequestNum(){
        return dataBase.requestDao().getRequestUntreatedNum(userId);
    }

    public void insertRequest(@NonNull Request request){
        new Thread(() -> dataBase.requestDao().insertRequest(request)).start();
    }

    public void updateRequest(@NonNull Request request){
        new Thread(() -> dataBase.requestDao().updateRequest(request)).start();
    }

    public void sendMsg(@NonNull Context context, @NonNull Msg msg){
        String[] msgJson = new String[]{MainApp.getInstance().getGson().toJson(msg)};
        switch (msg.getMsgType()){
            case Msg.TEXT:
                sendText(context, msg, msgJson);
                break;
            case Msg.PIC:
            case Msg.VOICE:
                Http.sendFile(msg.getMsgType(), msg.getMsg(), s -> {
                    if (s.equals("success")){
                        sendText(context, msg, msgJson);
                    }else{
                        ToastUtil.showMsg(context, "发送失败");
                    }
                });
                break;
        }
    }

    private void sendText(Context context, Msg msg, String[] msgJson){
        Http.sendText(msg, msgJson, s -> {
            if (s.equals("success")){
                insertMsg(msg);
            }else{
                ToastUtil.showMsg(context, "发送失败");
            }
        });
    }
}
