package com.mob.gochat.db.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyWithMsgWrapper;

import java.util.List;

@Dao
public interface BuddyDao {
    @Insert
    void insertBuddy(Buddy buddy);

    @Delete
    void deleteBuddy(Buddy buddy);

    @Update
    void updateBuddy(Buddy buddy);

    @Query("SELECT * FROM buddy WHERE Buddy.user=:id AND Buddy.id!=:id")
    LiveData<List<Buddy>> getBuddyList(String id);

    @Query("SELECT * FROM buddy WHERE id=:id")
    Buddy getBuddyById(String id);

    @Transaction
    @Query("SELECT * FROM buddy b WHERE b.user=:id AND b.id!=:id AND b.id IN (SELECT DISTINCT buddy_id from msg m WHERE m.user_id = :id )")
    LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsg(String id);
}
