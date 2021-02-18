package com.mob.gochat.view.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityNewBuddyBinding;
import com.mob.gochat.model.Request;
import com.mob.gochat.view.adapter.NewBuddyAdapter;
import com.mob.gochat.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewBuddyActivity extends AppCompatActivity {
    private ActivityNewBuddyBinding binding;
    private ViewModel viewModel;
    private List<Request> requestList = new ArrayList<>();
    private NewBuddyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewBuddyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新的朋友");
        initRecyclerView();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getRequestData().observe(this, requests -> {
            requestList.clear();
            requestList.addAll(requests);
            adapter.notifyDataSetChanged();
        });
        binding.newBuddyBtn.setOnClickListener(v -> {

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