package com.mob.gochat.db.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.kunminx.architecture.ui.callback.UnPeekLiveData;
import com.mob.gochat.model.Msg;

import java.util.List;

@Dao
public interface MsgDao {
    @Insert
    void insertMsg(Msg msg);

    @Delete
    void deleteMsg(Msg msg);

    @Query("SELECT * FROM msg WHERE (from_id = :id AND to_id=:userId) OR (to_id = :id AND from_id=:userId)")
    LiveData<List<Msg>> getChatMsgList(String id, String userId);

    @Query("SELECT * FROM msg INNER JOIN buddy ON Msg.from_id=Buddy.id OR Msg.to_id=Buddy.id LIMIT 1")
    LiveData<List<Msg>> getMsgList();
}
