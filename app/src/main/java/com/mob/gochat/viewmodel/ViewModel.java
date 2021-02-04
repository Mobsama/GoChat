package com.mob.gochat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;

public class ViewModel extends AndroidViewModel {
    private final RoomDataBase dataBase;
    @Getter
    private LiveData<List<Msg>> mMsgData;
    @Getter
    private LiveData<List<Msg>> mChatMsgData;
    @Getter
    private LiveData<List<Buddy>> mBuddyData;

    public ViewModel(@NonNull Application application) {
        super(application);
        dataBase = RoomDataBase.getInstance(application);
        mBuddyData = dataBase.buddyDao().getBuddyList();
        mMsgData = dataBase.msgDao().getMsgList();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory{
        @NotNull
        private final Application mApplication;
        public Factory(@NotNull Application application){
            mApplication = application;
        }
    }
}
