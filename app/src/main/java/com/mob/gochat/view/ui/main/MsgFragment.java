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

import com.mob.gochat.databinding.FragmentMsgBinding;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Msg;
import com.mob.gochat.view.ui.chat.ChatActivity;
import com.mob.gochat.view.adapter.MsgAdapter;
import com.mob.gochat.R;
import com.mob.gochat.utils.ClickUtil;
import com.mob.gochat.viewmodel.ViewModel;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MsgFragment extends Fragment {
    ViewModel viewModel;
    List<Msg> msgs;
    RoomDataBase dataBase;
    FragmentMsgBinding binding;
    MsgAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMsgBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);

        initRecyclerView();

        viewModel.getMMsgData().observe(getActivity(), msgs -> {
            this.msgs.clear();
            this.msgs.addAll(msgs);
            this.adapter.notifyDataSetChanged();
        });
        return binding.getRoot();
    }

    private void initRecyclerView(){
        binding.srvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.srvMsg.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        initMsgs();
        adapter = new MsgAdapter(R.layout.msg_list_item,msgs);

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

            }
        };

        binding.srvMsg.setOnItemClickListener((view, adapterPosition) -> {
            if(!ClickUtil.isFastDoubleClick()){
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Msg msg = msgs.get(adapterPosition);
                startActivity(intent);
            }
        });

        binding.srvMsg.setOnItemMenuClickListener(mMenuItemClickListener);
        binding.srvMsg.setSwipeMenuCreator(mSwipeMenuCreator);
        binding.srvMsg.setAdapter(adapter);
    }

    private void initMsgs(){
        msgs = viewModel.getMMsgData().getValue();
        if(msgs == null){
            msgs = new ArrayList();
        }
    }

}