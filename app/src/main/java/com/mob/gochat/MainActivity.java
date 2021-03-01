package com.mob.gochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.ui.login.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import lombok.Getter;


public class MainActivity extends AppCompatActivity {
    @Getter
    private static MainActivity instance;
    @Getter
    private LocalBroadcastManager localBroadcastManager;
    LogoutBroadcastReceiver localReceiver = new LogoutBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        instance = this;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_msg, R.id.navigation_buddy, R.id.navigation_mine)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        initBroadcastReceiver();
        if(MMKVUitl.getString(DataKeyConst.TOKEN) == null || MMKVUitl.getString(DataKeyConst.TOKEN).equals("")){
            gotoLogin();
        }else{
            MainApp.getInstance().startSocketIO();
        }
    }

    private void initBroadcastReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("gochat.broadcase.LOGOUT");
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void checkPermission(){
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.RECORD_AUDIO)
                .request((permissions, all) -> {
                    if(!all){
                        ToastUtil.showMsg(MainActivity.this, "请授予全部权限");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    public class LogoutBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            gotoLogin();
        }
    }

    void gotoLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}