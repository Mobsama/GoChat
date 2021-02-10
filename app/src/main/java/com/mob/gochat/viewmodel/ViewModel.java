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
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ThreadUtils;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;

    private LiveData<List<Msg>> mChatMsgData;

    private LiveData<List<Buddy>> mBuddyData;

    private LiveData<List<BuddyWithMsgWrapper>> mBuddyWithMsgData;

    private LiveData<Buddy> mCurrBuddy;

    private final static String userId = MMKVUitl.getString(DataKeyConst.USER_ID);

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    public LiveData<List<Msg>> getChatMsgData(String id){
        this.mChatMsgData = dataBase.msgDao().getChatMsgList(id, userId);
        return mChatMsgData;
    }

    public LiveData<List<Buddy>> getMBuddyData() {
        mBuddyData = dataBase.buddyDao().getBuddyList(userId);
        return mBuddyData;
    }

    public LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsgData() {
        mBuddyWithMsgData = dataBase.buddyDao().getBuddyWithMsg(userId);
        return mBuddyWithMsgData;
    }

    public LiveData<Buddy> getBuddy(String buddyId){
        mCurrBuddy = dataBase.buddyDao().getBuddyById(buddyId, userId);
        return mCurrBuddy;
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
}
