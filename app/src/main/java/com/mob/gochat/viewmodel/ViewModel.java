package com.mob.gochat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.HomeItem;
import com.mob.gochat.model.Msg;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.tencent.mmkv.MMKV;

import java.util.List;

import lombok.Getter;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;

    private LiveData<List<Msg>> mMsgData;

    private LiveData<List<Msg>> mChatMsgData;

    private LiveData<List<Buddy>> mBuddyData;

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

    public LiveData<List<Msg>> getMMsgData() {
        mMsgData = dataBase.msgDao().getMsgList();
        return mMsgData;
    }
}
