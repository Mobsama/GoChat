package com.mob.gochat.view.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
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
import com.mob.gochat.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class NewBuddyActivity extends AppCompatActivity {
    private ActivityNewBuddyBinding binding;
    private ViewModel viewModel;
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
        binding.newBuddyBtn.setOnClickListener(v -> {
//            String uuid = UUID.randomUUID().toString();
//            Date date = new Date(System.currentTimeMillis());
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//            Request request = new Request(uuid, userId, "10010", "Test", null, format.format(date));
//            viewModel.insertRequest(request);
            try{
                int id = Integer.parseInt(binding.newBuddySearch.getText().toString());
                if(id <= 10000){
                    ToastUtil.showMsg(this, "账号格式错误");
                }else if((id+"").equals(userId)){
                    ToastUtil.showMsg(this, "这是你哦");
                }else{
                    Http.getUser(this, id+"", userId, str -> {
                        PostRequest request = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
                        if(request.getStatus() == 200){
                            Buddy buddy = MainApp.getInstance().getGson().fromJson(request.getMessage(), Buddy.class);
                            if(buddy.getAvatar() == null){
                                Intent intent = new Intent(this, InfoActivity.class);
                                intent.putExtra("buddy", buddy);
                                startActivity(intent);
                            }else{
                                Http.getFile(this, Msg.PIC, buddy.getAvatar(), path -> {
                                    Intent intent = new Intent(this, InfoActivity.class);
                                    intent.putExtra("buddy", buddy);
                                    startActivity(intent);
                                });
                            }
                        }else{
                            ToastUtil.showMsg(this, "找不到联系人");
                        }
                    });
                }
            }catch (Exception e){
                ToastUtil.showMsg(this, "账号格式错误");
            }
        });
    }

    private void initRecyclerView(){
        binding.newBuddyList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewBuddyAdapter(R.layout.new_buddy_list_item, requestList);
        adapter.addChildClickViewIds(R.id.new_buddy_agree, R.id.new_buddy_refuse);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Request request = requestList.get(position);
            if(view.getId() == R.id.new_buddy_agree){
                request.setIsTreated(Request.APPROVED);
                Buddy buddy = new Buddy(request.getBuddyId(), userId, request.getBuddyName(),null, null, "2000 - 01 - 01", "广东省 - 汕头市 - 潮阳区", 0);
                viewModel.insertBuddy(buddy);
            }else if(view.getId() == R.id.new_buddy_refuse){
                request.setIsTreated(Request.REJECTED);
            }
            viewModel.updateRequest(request);
        });
        binding.newBuddyList.setAdapter(adapter);
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