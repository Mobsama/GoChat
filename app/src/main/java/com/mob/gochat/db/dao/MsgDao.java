package com.mob.gochat.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mob.gochat.model.Msg;

import java.util.List;

@Dao
public interface MsgDao {
    @Insert
    void insertMsg(Msg msg);

    @Delete
    void deleteMsg(Msg msg);

    @Query("SELECT * FROM msg WHERE id = :id")
    List<Msg> getMsgList(String id);
}
