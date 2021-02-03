package com.mob.gochat.viewmodel;

import com.mob.gochat.model.Msg;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class ChatViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<List<Msg>> mChatData = new MutableLiveData<>();
    private List<Msg> chatData = new ArrayList<>();

    public MutableLiveData<List<Msg>> getChatData() {
        return mChatData;
    }

    public void initChatData(){
        mChatData.setValue(chatData);
    }

    public void addMsg(Msg Msg){
        chatData.add(Msg);
        mChatData.postValue(chatData);
    }

}
