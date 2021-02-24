package com.mob.gochat.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
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
import com.mob.gochat.utils.ToastUtil;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.socket.client.Socket;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;
    private final Socket socket = MainApp.getInstance().getSocket();
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final static String userId = MMKVUitl.getString(DataKeyConst.USER_ID);

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    @NonNull
    public LiveData<List<Msg>> getChatMsgData(@NonNull String id){
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
        mExecutor.execute(() -> dataBase.buddyDao().insertBuddy(buddy));
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

    public void updateMsgStatue(@NonNull String buddyId){
        mExecutor.execute(() -> dataBase.msgDao().updateMsgStatus(buddyId, userId));
    }

    public void deleteMsgWithBuddyId(@NonNull String buddyId){
        mExecutor.execute(() -> dataBase.msgDao().deleteMsgWithBuddyId(buddyId, userId));
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
        mExecutor.execute(() -> dataBase.requestDao().insertRequest(request));
    }

    public void updateRequest(@NonNull Request request){
        mExecutor.execute(() -> dataBase.requestDao().updateRequest(request));
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
