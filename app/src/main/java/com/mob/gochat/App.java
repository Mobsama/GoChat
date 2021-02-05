package com.mob.gochat;

import android.app.Application;

import com.mob.gochat.utils.MMKVUitl;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKVUitl.initialize(this);

    }
}
