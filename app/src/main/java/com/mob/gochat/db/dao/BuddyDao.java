package com.mob.gochat.db.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mob.gochat.model.Buddy;

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
}
