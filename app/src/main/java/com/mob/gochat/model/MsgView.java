package com.mob.gochat.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;

@DatabaseView("select * from msg a where (select count(*) from msg b where a.buddy_id = b.buddy_id and b.time > a.time) < 1 order by a.time desc")
public class MsgView {
    @Getter
    @Setter
    @Embedded
    Msg msg;
}
