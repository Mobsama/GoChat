package com.mob.gochat.db.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kunminx.architecture.ui.callback.UnPeekLiveData;
import com.mob.gochat.model.Msg;

import java.util.List;

@Dao
public interface MsgDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMsg(Msg msg);

    @Delete
    void deleteMsg(Msg msg);

    @Query("UPDATE msg SET is_read=1 WHERE buddy_id=:buddyId AND user_id=:userId AND is_read=0")
    void updateMsgStatus(String buddyId, String userId);

    @Query("SELECT * FROM msg WHERE buddy_id=:buddyId AND user_id=:userId AND msg_type!=3")
    LiveData<List<Msg>> getChatMsgList(String buddyId, String userId);

    @Query("DELETE FROM msg WHERE msg.buddy_id=:buddyId AND msg.user_id=:userId")
    void deleteMsgWithBuddyId(String buddyId, String userId);
}
