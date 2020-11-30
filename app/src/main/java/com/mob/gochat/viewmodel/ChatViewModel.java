package com.mob.gochat.viewmodel;

import com.mob.gochat.model.MsgModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class ChatViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<List<MsgModel>> mChatData = new MutableLiveData<>();
    private List<MsgModel> chatData = new ArrayList<>();

    public MutableLiveData<List<MsgModel>> getChatData() {
        return mChatData;
    }

    public void initChatData(){
        mChatData.setValue(chatData);
    }

    public void addMsg(MsgModel MsgModel){
        chatData.add(MsgModel);
        mChatData.postValue(chatData);
    }

}
