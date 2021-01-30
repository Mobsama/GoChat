package com.mob.gochat.view.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mob.gochat.model.BuddyModel;
import com.mob.gochat.view.ui.add.NewBuddyActivity;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.viewmodel.UserViewModel;
import com.mob.gochat.view.ui.view.SideBarView;
import com.mob.gochat.view.ui.view.StickyDecoration;
import com.mob.gochat.view.adapter.BuddyAdapter;
import com.mob.gochat.R;
import com.mob.gochat.utils.ClickUtil;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class BuddyFragment extends Fragment {

    private UserViewModel userViewModel;
    SwipeRecyclerView mSRVBuddy;
    BuddyAdapter buddyAdapter;
    LinearLayoutManager buddyManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.initBuddyData();
        View root = inflater.inflate(R.layout.fragment_buddy, container, false);

        SideBarView mSideBarView = root.findViewById(R.id.sb_buddy);
        TextView mDialog = root.findViewById(R.id.tv_dialog);
        mSideBarView.setDialog(mDialog);

        mSRVBuddy = root.findViewById(R.id.srv_buddy);
        initRecycleView();

        mSideBarView.setOnTouchingListener(s -> {
            mSRVBuddy.smoothCloseMenu();
            for(int i = 0; i < userViewModel.getBuddyData().getValue().size(); i++){
                if(userViewModel.getBuddyData().getValue().get(i).getLetters().equals(s)){
                    buddyManager.scrollToPositionWithOffset(i,0);
                    break;
                }
            }
        });

        return root;
    }

    private void initRecycleView(){
        buddyManager = new LinearLayoutManager(getContext());
        mSRVBuddy.setLayoutManager(buddyManager);
        StickyDecoration stickyDecoration = new StickyDecoration(getContext(),userViewModel.getBuddyData().getValue());
        mSRVBuddy.addItemDecoration(stickyDecoration);
        BuddyModel newBuddy = new BuddyModel();
        newBuddy.setName("新的朋友");
        newBuddy.setLetters("↑");
        List<BuddyModel> buddyList = userViewModel.getBuddyData().getValue();
        buddyList.add(0,newBuddy);
        buddyAdapter = new BuddyAdapter(R.layout.buddy_list_item,buddyList);
        SwipeMenuCreator mSwipeMenuCreator = (leftMenu, rightMenu, position) -> {
            if(position == 0){
                return;
            }
            SwipeMenuItem remarksItem = new SwipeMenuItem(getContext());
            remarksItem.setBackground(R.color.grey);
            remarksItem.setText("备注");
            remarksItem.setTextColor(Color.WHITE);
            remarksItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            remarksItem.setWidth(getResources().getDimensionPixelOffset(R.dimen.dp_80));
            rightMenu.addMenuItem(remarksItem);
        };

        mSRVBuddy.setOnItemClickListener((view, adapterPosition) -> {
            if(!ClickUtil.isFastDoubleClick()){
                Intent intent;
                if(adapterPosition == 0){
                    intent = new Intent(getActivity(), NewBuddyActivity.class);
                }else{
                    intent = new Intent(getActivity(), InfoActivity.class);
                }
                startActivity(intent);
            }
        });
        mSRVBuddy.setSwipeMenuCreator(mSwipeMenuCreator);
        mSRVBuddy.setAdapter(buddyAdapter);
    }
}

