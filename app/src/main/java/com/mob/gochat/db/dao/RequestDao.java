package com.mob.gochat.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mob.gochat.model.Request;

import java.util.List;

@Dao
public interface RequestDao {
    @Insert
    void insertRequest(Request request);

    @Delete
    void deleteRequest(Request request);

    @Update
    void updateRequest(Request request);

    @Query("SELECT * FROM request WHERE user_id=:userId")
    LiveData<List<Request>> getRequestList(String userId);

    @Query("SELECT count(*) FROM request WHERE user_id=:userId AND is_treated=0")
    LiveData<Integer> getRequestUntreatedNum(String userId);
}
