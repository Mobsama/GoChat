package com.mob.gochat.view.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.promeg.pinyinhelper.Pinyin;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.databinding.FragmentBuddyBinding;
import com.mob.gochat.db.RoomDataBase;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ThreadUtils;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.view.ui.widget.StickyDecoration;
import com.mob.gochat.view.adapter.BuddyAdapter;
import com.mob.gochat.R;
import com.mob.gochat.utils.ClickUtil;
import com.mob.gochat.viewmodel.ViewModel;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BuddyFragment extends Fragment {

    private FragmentBuddyBinding binding;
    private RoomDataBase dataBase;
    private ViewModel viewModel;
    List<Buddy> buddies;
    BuddyAdapter buddyAdapter;
    LinearLayoutManager buddyManager;

    List<String> list = Arrays.asList("胡泉源",
            "李相赫", "唐志明", "朱泽楷", "郑锐轩", "陈继恩", "李旭辉", "詹智聪", "坠毁", "李科",
            "吴彦祖", "王健成", "张伪娘", "林丰涛", "序伟", "张伪娘", "郑泽芬", "魏法峰", "%guozehong",
            "我就是大名鼎鼎的鸟人辉了", "欧阳说ok", "一斤", "冼毅贤", "欧阳说ok", "刘启炬", "英语六级小天才胡茄声",
            "钧仔", "花都嗨", "张伪娘", "男神", "张伪娘", "张伪娘", "张飞", "jcj", "刘大壮", "李素函",
            "比利王", "龟王李科", "郑泽芬", "吴彦祖", "陈铎友", "?????", "黄家欣", "陈彦儒", "李白",
            "赖杰颖", "阿桂", "邓沛锦", "斯内克", "李升辉", "鸭王钟", "关羽", "欧阳宇豪", "高中大馒头",
            "邓银生", "林小七", "uzi", "张飞", "韶关学院地头蛇", "duckingwu", "卢本伟", "高渐离婚",
            "韶院过江龙欧阳宇豪", "爱新觉罗福泉", "%guozehong", "郭仔凡");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBuddyBinding.inflate(inflater, container, false);
        dataBase = RoomDataBase.getInstance(getContext());
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        viewModel.getMBuddyData().observe(getActivity(), buddies -> {
            this.buddies.clear();
            this.buddies.addAll(buddies);
            Collections.sort(this.buddies, (o1, o2) -> {
                if(o1.getLetters().equals("#") && !o2.getLetters().equals("#")){
                    return 1;
                }else if(!o1.getLetters().equals("#") && o2.getLetters().equals("#")){
                    return -1;
                }else{
                    if(!o1.getLetters().equals(o2.getLetters())) {
                        return o1.getLetters().compareToIgnoreCase(o2.getLetters());
                    }else{
                        return Pinyin.toPinyin(o1.getName(),"")
                                .compareToIgnoreCase(Pinyin.toPinyin(o2.getName(),""));
                    }
                }
            });
            addNewFriItem();
            buddyAdapter.notifyDataSetChanged();
        });

        binding.sbBuddy.setDialog(binding.tvDialog);
        initRecycleView();

        binding.sbBuddy.setOnTouchingListener(s -> {
            binding.srvBuddy.smoothCloseMenu();
            for(int i = 0; i < buddies.size(); i++){
                if(buddies.get(i).getLetters().equals(s)){
                    buddyManager.scrollToPositionWithOffset(i,0);
                    break;
                }
            }
        });

        return binding.getRoot();
    }

    private void addNewFriItem(){
        Buddy newBuddy = new Buddy("0","0","新的朋友","","");
        newBuddy.setLetters("↑");
        this.buddies.add(0, newBuddy);
    }

    private void initRecycleView(){
        buddyManager = new LinearLayoutManager(getContext());
        binding.srvBuddy.setLayoutManager(buddyManager);
        if(viewModel.getMBuddyData().getValue() != null){
            buddies = viewModel.getMBuddyData().getValue();
        }else{
            buddies = new LinkedList<>();
        }

        addNewFriItem();
        StickyDecoration stickyDecoration = new StickyDecoration(getContext(), buddies);
        binding.srvBuddy.addItemDecoration(stickyDecoration);
        buddyAdapter = new BuddyAdapter(R.layout.buddy_list_item, buddies);
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

        OnItemMenuClickListener mItemMenuClickListener = (menuBridge, adapterPosition) -> {
            menuBridge.closeMenu();
            Buddy buddy = buddies.get(adapterPosition);
            new XPopup.Builder(getContext()).asInputConfirm(buddy.getName(), "请输入备注：",
                    text -> {
                        buddy.setRemarks(text);
                        buddyDao(buddy,update);
                    }).show();
        };

        binding.srvBuddy.setOnItemClickListener((view, adapterPosition) -> {
            if(!ClickUtil.isFastDoubleClick()){
                if(adapterPosition == 0){
//                    intent = new Intent(getActivity(), NewBuddyActivity.class);
                    int size = buddies.size();
                    if(size < list.size()){
                        Buddy buddy = new Buddy("000"+size, MMKVUitl.getString(DataKeyConst.USER_ID), list.get(size),null, null);
                        buddyDao(buddy,insert);
                    }

                }else{
                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    intent.putExtra("buddy", buddies.get(adapterPosition));
                    startActivity(intent);
                }
            }
        });
        binding.srvBuddy.setSwipeMenuCreator(mSwipeMenuCreator);
        binding.srvBuddy.setOnItemMenuClickListener(mItemMenuClickListener);
        binding.srvBuddy.setAdapter(buddyAdapter);
    }


    private final static int insert = 1;
    private final static int update = 0;
    private void buddyDao(Buddy buddy, int statue){
        ThreadUtils.executeByCpu(new ThreadUtils.Task() {
            @Override
            public Object doInBackground() throws Throwable {
                if(statue == insert){
                    dataBase.buddyDao().insertBuddy(buddy);
                }else if(statue == update){
                    dataBase.buddyDao().updateBuddy(buddy);
                }

                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }
}

