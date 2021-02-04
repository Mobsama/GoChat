package com.mob.gochat.view.ui.info;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityInfoBinding;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.utils.ThreadUtils;
import com.mob.gochat.viewmodel.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class InfoActivity extends AppCompatActivity {
    private ActivityInfoBinding binding;
    private ViewModel viewModel;
    private RoomDataBase dataBase;
    private Buddy buddy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        buddy = getIntent().getParcelableExtra("buddy");
        if(buddy == null){
            finish();
        }
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        binding.btnInfoGroup.setVisibility(View.VISIBLE);
        binding.tvInfoName.setText(buddy.getName());
        binding.tvInfoNumber.setText(buddy.getId());
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