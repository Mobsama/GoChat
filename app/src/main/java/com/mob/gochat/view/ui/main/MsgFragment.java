package com.mob.gochat.view.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mob.gochat.view.ui.chat.ChatActivity;
import com.mob.gochat.viewmodel.UserViewModel;
import com.mob.gochat.view.adapter.MsgAdapter;
import com.mob.gochat.R;
import com.mob.gochat.utils.ClickUtil;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

public class MsgFragment extends Fragment {
    UserViewModel userViewModel;
    SwipeRecyclerView mSRVMsg;
    MsgAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.initMsgData();

        View root = inflater.inflate(R.layout.fragment_msg, container, false);
        mSRVMsg = root.findViewById(R.id.srv_msg);

        initRecyclerView();

        userViewModel.getMsgData().observe(getViewLifecycleOwner(), strings -> updateUIData());
        return root;
    }

    private void initRecyclerView(){
        mSRVMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        mSRVMsg.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        adapter = new MsgAdapter(R.layout.msg_list_item,userViewModel.getMsgData().getValue());

        SwipeMenuCreator mSwipeMenuCreator = (leftMenu, rightMenu, position) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
            deleteItem.setBackground(R.color.red);
            deleteItem.setText("删除");
            deleteItem.setTextColor(Color.WHITE);
            deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            deleteItem.setWidth(getResources().getDimensionPixelOffset(R.dimen.dp_80));
            rightMenu.addMenuItem(deleteItem);
        };

        OnItemMenuClickListener mMenuItemClickListener = (menuBridge, adapterPosition) -> {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection();
            int menuPosition = menuBridge.getPosition();
            if(direction == SwipeRecyclerView.RIGHT_DIRECTION && menuPosition == 0){
                userViewModel.removeMsgData(adapterPosition);
            }
        };

        mSRVMsg.setOnItemClickListener((view, adapterPosition) -> {
            if(!ClickUtil.isFastDoubleClick()){
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        mSRVMsg.setOnItemMenuClickListener(mMenuItemClickListener);
        mSRVMsg.setSwipeMenuCreator(mSwipeMenuCreator);
        mSRVMsg.setAdapter(adapter);
    }

    private void updateUIData(){
        adapter.setList(userViewModel.getMsgData().getValue());
        mSRVMsg.setAdapter(adapter);
    }

}