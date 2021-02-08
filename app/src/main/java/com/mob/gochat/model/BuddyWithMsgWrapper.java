package com.mob.gochat.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BuddyWithMsgWrapper {
    @Getter
    @Setter
    @Embedded
    Buddy buddy;

    @Getter
    @Setter
    @Relation(parentColumn = "id", entityColumn = "buddy_id")
    List<MsgView> msg;
}
