package com.mob.gochat.view.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.MainApp;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityNewBuddyBinding;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
import com.mob.gochat.model.Request;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.adapter.NewBuddyAdapter;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.view.ui.login.LoginActivity;
import com.mob.gochat.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class NewBuddyActivity extends AppCompatActivity {
    private ActivityNewBuddyBinding binding;
    ViewModel viewModel;
    private List<Request> requestList = new ArrayList<>();
    private NewBuddyAdapter adapter;
    private final String userId = MMKVUitl.getString(DataKeyConst.USER_ID);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewBuddyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新的朋友");
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);
        initRecyclerView();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getRequestData(userId).observe(this, requests -> {
            requestList.clear();
            requestList.addAll(requests);
            adapter.notifyDataSetChanged();
        });
        initOnClick();
    }

    private void initOnClick(){
        binding.newBuddyBtn.setOnClickListener(v -> {
            try{
                int id = Integer.parseInt(binding.newBuddySearch.getText().toString());
                if(id <= 10000){
                    ToastUtil.showMsg(this, "账号格式错误");
                }else if((id+"").equals(userId)){
                    ToastUtil.showMsg(this, "这是你哦");
                }else{
                    getUser(id + "");
                }
            }catch (Exception e){
                ToastUtil.showMsg(this, "账号格式错误");
            }
        });

        binding.newBuddyTipClear.setOnClickListener(v -> {
            new XPopup.Builder(this).asConfirm("清除所有已处理消息", "是否要清除？",
                () -> {
                    viewModel.deleteTreatedRequest(userId);
                })
                .show();
        });
    }

    private void getUser(String id){
        if(MainApp.getInstance().isNet()){
            Http.getUser(this, id, userId, str -> {
                PostRequest request = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
                if(request.getStatus() == 200){
                    Buddy buddy = MainApp.getInstance().getGson().fromJson(request.getMessage(), Buddy.class);
                    if(buddy.getAvatar() == null){
                        Intent intent = new Intent(this, InfoActivity.class);
                        intent.putExtra("buddy", buddy);
                        viewModel.isBuddy(buddy.getId(), buddy.getUser(), is -> {
                            intent.putExtra("isBuddy", is);
                            startActivity(intent);
                        });
                    }else{
                        Http.getFile(this, Msg.PIC, buddy.getAvatar(), path -> {
                            Intent intent = new Intent(this, InfoActivity.class);
                            intent.putExtra("buddy", buddy);
                            viewModel.isBuddy(buddy.getId(), buddy.getUser(), is -> {
                                intent.putExtra("isBuddy", is);
                                startActivity(intent);
                            });
                        });
                    }
                }else{
                    ToastUtil.showMsg(this, "找不到联系人");
                }
            });
        }else{
            ToastUtil.showMsg(this, "无法连接网路");
        }
    }

    private void initRecyclerView(){
        binding.newBuddyList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewBuddyAdapter(R.layout.new_buddy_list_item, requestList);
        adapter.addChildClickViewIds(R.id.new_buddy_agree, R.id.new_buddy_refuse);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Request request = requestList.get(position);
            if(view.getId() == R.id.new_buddy_agree){
                addBuddy(request);
            }else if(view.getId() == R.id.new_buddy_refuse){
                request.setIsTreated(Request.REJECTED);
                viewModel.updateRequest(request);
            }
        });
        binding.newBuddyList.setAdapter(adapter);
    }

    public void addBuddy(Request request){
        viewModel.addNewBuddy(request.getUserId(), request.getBuddyId(), result -> {
            if(result){
                request.setIsTreated(Request.APPROVED);
                viewModel.updateRequest(request);
                Http.getUser(this, request.getBuddyId(), request.getUserId(), str -> {
                    PostRequest req = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
                    if(req.getStatus() == 200){
                        Buddy buddy = MainApp.getInstance().getGson().fromJson(req.getMessage(), Buddy.class);
                        buddy.setLettersWithName(buddy.getName());
                        if(buddy.getAvatar() == null){
                            viewModel.upsertBuddy(buddy);
                            sendTip(buddy);
                        }else{
                            Http.getFile(this, Msg.PIC, buddy.getAvatar(), path -> {
                                viewModel.upsertBuddy(buddy);
                                sendTip(buddy);
                            });
                        }
                    }
                });
            }else{
                ToastUtil.showMsg(this, "添加好友失败");
            }
        });
    }

    private void sendTip(Buddy buddy){
        runOnUiThread(() -> {
            Date date = new Date(System.currentTimeMillis());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            String uuid = UUID.randomUUID().toString();
            Msg tip = new Msg(uuid, buddy.getId(), buddy.getUser(), Msg.OTHER, Msg.TEXT, "我已经添加你为好友了哦。", format.format(date));
            new CountDownTimer(500, 500) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    viewModel.sendMsg(tip, s -> {
                        viewModel.insertMsg(tip);
                    });
                }
            }.start();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 16908332:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}