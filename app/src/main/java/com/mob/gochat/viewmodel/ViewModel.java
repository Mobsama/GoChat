package com.mob.gochat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyWithMsgWrapper;
import com.mob.gochat.model.Msg;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;

    private LiveData<List<Msg>> mChatMsgData;

    private LiveData<List<Buddy>> mBuddyData;

    private LiveData<List<BuddyWithMsgWrapper>> mBuddyWithMsgData;

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
    }

    public LiveData<List<Msg>> getChatMsgData(String id){
        this.mChatMsgData = dataBase.msgDao().getChatMsgList(id, MMKVUitl.getString(DataKeyConst.USER_ID));
        return mChatMsgData;
    }

    public LiveData<List<Buddy>> getMBuddyData() {
        mBuddyData = dataBase.buddyDao().getBuddyList(MMKVUitl.getString(DataKeyConst.USER_ID));
        return mBuddyData;
    }

    public LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsgData() {
        mBuddyWithMsgData = dataBase.buddyDao().getBuddyWithMsg(MMKVUitl.getString(DataKeyConst.USER_ID));
        return mBuddyWithMsgData;
    }
}
