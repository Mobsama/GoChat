package com.mob.gochat.view.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.promeg.pinyinhelper.Pinyin;
import com.lxj.xpopup.XPopup;
import com.mob.gochat.databinding.FragmentBuddyBinding;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.view.ui.add.NewBuddyActivity;
import com.mob.gochat.view.ui.info.InfoActivity;
import com.mob.gochat.view.ui.widget.StickyDecoration;
import com.mob.gochat.view.adapter.BuddyAdapter;
import com.mob.gochat.R;
import com.mob.gochat.utils.ClickUtil;
import com.mob.gochat.viewmodel.ViewModel;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuddyFragment extends Fragment {

    private FragmentBuddyBinding binding;
    private ViewModel viewModel;
    private List<Buddy> buddies = new ArrayList<>();
    private BuddyAdapter buddyAdapter;
    private LinearLayoutManager buddyManager;
    private final String userId = MMKVUitl.getString(DataKeyConst.USER_ID);
    private final Buddy newBuddy = new Buddy("0","0","新的朋友",null,null, null, null, 0);

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
        newBuddy.setLetters("↑");
        this.buddies.add(0, newBuddy);
        initRecycleView();
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        viewModel.getMBuddyData().observe(getActivity(), buddies -> {
            this.buddies.clear();
            this.buddies.addAll(buddies);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("联系人（"+(this.buddies.size()+"）"));
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
            this.buddies.add(0, newBuddy);
            buddyAdapter.notifyDataSetChanged();
        });
        viewModel.getUntreatedRequestNum().observe(getActivity(), num -> {
            if(num != 0){
                buddies.remove(0);
                newBuddy.setGender(num);
                buddies.add(0, newBuddy);
                buddyAdapter.notifyDataSetChanged();
            }
        });
        binding.sbBuddy.setDialog(binding.tvDialog);

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

    private void initRecycleView(){
        buddyManager = new LinearLayoutManager(getContext());
        binding.srvBuddy.setLayoutManager(buddyManager);
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
                        viewModel.updateBuddy(buddy);
                    }).show();
        };

        binding.srvBuddy.setOnItemClickListener((view, adapterPosition) -> {
            if(!ClickUtil.isFastDoubleClick()){
                if(adapterPosition == 0){
                    Intent intent = new Intent(getActivity(), NewBuddyActivity.class);
                    startActivity(intent);
//                    int size = buddies.size();
//                    if(size < list.size()){
//                        Buddy buddy = new Buddy("000"+size, MMKVUitl.getString(DataKeyConst.USER_ID), list.get(size),null, null, "2000 - 01 - 01", "广东省 - 汕头市 - 潮阳区", 0);
//                        viewModel.insertBuddy(buddy);
//                    }
//                    String uuid = UUID.randomUUID().toString();
//                    Date date = new Date(System.currentTimeMillis());
//                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//                    Request request = new Request(uuid, userId, "10010", "Test", null, format.format(date), Request.UNTREATED);
//                    viewModel.insertRequest(request);
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
}

