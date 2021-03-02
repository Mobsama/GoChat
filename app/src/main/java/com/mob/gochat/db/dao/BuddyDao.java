package com.mob.gochat.db.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.BuddyExcludeRemark;
import com.mob.gochat.model.BuddyWithMsgWrapper;

import java.util.List;

@Dao
public abstract class BuddyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertBuddy(@NonNull Buddy buddy);

    @Delete
    public abstract void deleteBuddy(@NonNull Buddy buddy);

    @Update
    public abstract void updateBuddy(@NonNull Buddy buddy);

    @Update(entity = Buddy.class)
    public abstract void updateBuddyExcludeRemark(@NonNull BuddyExcludeRemark buddy);

    @NonNull
    @Query("SELECT * FROM buddy WHERE Buddy.user=:id AND Buddy.id!=:id")
    public abstract LiveData<List<Buddy>> getBuddyList(@NonNull String id);

    @NonNull
    @Query("SELECT * FROM buddy WHERE id=:BuddyId AND user=:userId")
    public abstract LiveData<Buddy> getBuddyById(@NonNull String BuddyId, @NonNull String userId);

    @NonNull
    @Transaction
    @Query("SELECT * FROM buddy b WHERE b.user=:id AND b.id!=:id AND b.id IN (SELECT DISTINCT buddy_id from msg m WHERE m.user_id = :id )")
    public abstract LiveData<List<BuddyWithMsgWrapper>> getBuddyWithMsg(@NonNull String id);

    @Transaction
    public boolean upsertBuddy(Buddy buddy){
        long id = insertBuddy(buddy);
        if(id == -1){
            BuddyExcludeRemark buddyExcludeRemark =
                    new BuddyExcludeRemark(buddy.getId(),
                                            buddy.getUser(),
                                            buddy.getAvatar(),
                                            buddy.getName(),
                                            buddy.getMail(),
                                            buddy.getBirth(),
                                            buddy.getAddress(),
                                            buddy.getGender());
            updateBuddyExcludeRemark(buddyExcludeRemark);
            return false;
        }
        return true;
    }
}
