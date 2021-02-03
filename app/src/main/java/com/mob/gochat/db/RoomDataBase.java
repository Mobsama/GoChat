package com.mob.gochat.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mob.gochat.db.dao.BuddyDao;
import com.mob.gochat.db.dao.MsgDao;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;

@Database(entities = {Msg.class, Buddy.class}, version = 1)
public abstract class RoomDataBase extends RoomDatabase {
    private static final String DATABASE = "database";
    private volatile static RoomDataBase instance;
    public static RoomDataBase getInstance(Context context){
        if(instance == null){
            synchronized (RoomDataBase.class){
                if(instance == null){
                    instance = Room
                            .databaseBuilder(context.getApplicationContext(), RoomDataBase.class, DATABASE)
                            .build();
                }
            }
        }

        return instance;
    }

    public abstract MsgDao msgDao();
    public abstract BuddyDao buddyDao();
}
