package com.mob.gochat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyWithMsgWrapper;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.Request;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ThreadUtils;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;

    private final static String userId = MMKVUitl.getString(DataKeyConst.USER_ID);

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    public LiveData<List<Msg>> getChatMsgData(String id){
        return dataBase.msgDao().getChatMsgList(id, userId);
    }

    public LiveData<List<Buddy>> getMBuddyData() {
        return dataBase.buddyDao().getBuddyList(userId);
    }

    public LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsgData() {
        return dataBase.buddyDao().getBuddyWithMsg(userId);
    }

    public LiveData<Buddy> getBuddy(String buddyId){
        return dataBase.buddyDao().getBuddyById(buddyId, userId);
    }

    public void insertBuddy(Buddy buddy){
        new Thread(() -> dataBase.buddyDao().insertBuddy(buddy)).start();
    }

    public void updateBuddy(Buddy buddy){
        new Thread(() -> dataBase.buddyDao().updateBuddy(buddy)).start();
    }

    public void deleteBuddy(Buddy buddy){
        new Thread(() -> dataBase.buddyDao().deleteBuddy(buddy)).start();
    }

    public void insertMsg(Msg msg){
        new Thread(() -> dataBase.msgDao().insertMsg(msg)).start();

    }

    public void updateMsgStatue(String buddyId){
        new Thread(() -> dataBase.msgDao().updateMsgStatus(buddyId, userId)).start();
    }

    public void deleteMsgWithBuddyId(String buddyId){
        new Thread(() -> dataBase.msgDao().deleteMsgWithBuddyId(buddyId, userId)).start();
    }

    public LiveData<List<Request>> getRequestData(){
        return dataBase.requestDao().getRequestList(userId);
    }

    public LiveData<Integer> getUntreatedRequestNum(){
        return dataBase.requestDao().getRequestUntreatedNum(userId);
    }

    public void insertRequest(Request request){
        new Thread(() -> dataBase.requestDao().insertRequest(request)).start();
    }

    public void updateRequest(Request request){
        new Thread(() -> dataBase.requestDao().updateRequest(request)).start();
    }
}
