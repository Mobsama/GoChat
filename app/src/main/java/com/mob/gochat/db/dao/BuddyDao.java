package com.mob.gochat.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mob.gochat.model.Buddy;

import java.util.List;

@Dao
public interface BuddyDao {
    @Insert
    void insertBuddy(Buddy buddy);

    @Delete
    void deleteBuddy(Buddy buddy);

    @Query("SELECT * FROM buddy")
    List<Buddy> getBuddyList();

    @Query("SELECT * FROM buddy WHERE id = :id")
    Buddy getBuddyById(String id);
}
