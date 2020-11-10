package com.mob.gochat.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mob.gochat.Model.User;
import com.mob.gochat.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewModel extends androidx.lifecycle.ViewModel {

    private MutableLiveData<User> User;
    private MutableLiveData<RecyclerView> mRVMsg;


    public UserViewModel() {
        mRVMsg = new MutableLiveData<>();
    }

    public void setRV(RecyclerView recyclerView){
        mRVMsg.setValue(recyclerView);
    }
}