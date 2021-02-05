package com.mob.gochat.model;

import lombok.Getter;

public class HomeItem {
    @Getter
    private Buddy buddy;
    @Getter
    private Msg msg;

    HomeItem(Buddy buddy, Msg msg){
        this.buddy = buddy;
        this.msg = msg;
    }
}
