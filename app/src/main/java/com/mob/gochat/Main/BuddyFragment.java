package com.mob.gochat.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mob.gochat.CustomizeView.SideBarView;
import com.mob.gochat.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BuddyFragment extends Fragment {

    private UserViewModel userViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_buddy, container, false);

        SideBarView mSideBarView = root.findViewById(R.id.sb_buddy);
        TextView mDialog = root.findViewById(R.id.tv_dialog);
        mSideBarView.setDialog(mDialog);

        SwipeRecyclerView mSRVBuddy = root.findViewById(R.id.srv_buddy);

        List<String> list = Arrays.asList("胡泉源",
                "李相赫", "唐志明", "朱泽楷", "郑锐轩", "陈继恩", "李旭辉", "詹智聪", "坠毁", "李科",
                "吴彦祖", "王健成", "张伪娘", "林丰涛", "序伟", "张伪娘", "郑泽芬", "魏法峰", "%guozehong",
                "我就是大名鼎鼎的鸟人辉了", "欧阳说ok", "一斤", "冼毅贤", "欧阳说ok", "刘启炬", "英语六级小天才胡茄声",
                "钧仔", "花都嗨", "张伪娘", "男神", "张伪娘", "张伪娘", "张飞", "jcj", "刘大壮", "李素函",
                "比利王", "龟王李科", "郑泽芬", "吴彦祖", "陈铎友", "?????", "黄家欣", "陈彦儒", "李白",
                "赖杰颖", "阿桂", "邓沛锦", "斯内克", "李升辉", "鸭王钟", "关羽", "欧阳宇豪", "高中大馒头",
                "邓银生", "林小七", "uzi", "张飞", "韶关学院地头蛇", "duckingwu", "卢本伟", "高渐离婚",
                "韶院过江龙欧阳宇豪", "爱新觉罗福泉", "%guozehong", "郭仔凡");
//        List<SortModel> models = new LinkedList<>();
//        for(String s : list){
//            SortModel model = new SortModel();
//            model.setName(s);
//            model.setLetters(Pinyin.toPinyin(s,"").charAt(0)+"");
//            models.add(model);
//        }
        GroupAdapter adapter = new GroupAdapter();
        adapter.setListItem(list);
        mSRVBuddy.setAdapter(adapter);

        return root;
    }
}

class SortModel{
    private String name;
    private String letters;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder{
    private TextView user;
    GroupViewHolder(View itemView){
        super(itemView);
        user = itemView.findViewById(R.id.tv_buddy_user);
    }

    void bind(ListItem item){
        user.setText(item.user);
    }
}

class ListItem{
    protected String user;
    protected String letters;
    ListItem(String user){
        this.user = user;
        if(Pinyin.isChinese(user.charAt(0))){
            char c = Pinyin.toPinyin(user,"").toUpperCase().charAt(0);
            letters = c+"";
        }else{
            char c = user.toUpperCase().charAt(0);
            if(c >= 'A' && c <= 'Z'){
                letters = c+"";
            }else{
                letters = "#";
            }
        }
    }
}

class StickyListItem extends ListItem{
    StickyListItem(String text){
        super(text);
    }
}

class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder>{
    static final int VIEW_TYPE_NON_STICKY = R.layout.list_item_buddy;
    static final int VIEW_TYPE_STICKY = R.layout.list_item_buddy_sticky;

    private List<ListItem> mListItem = new ArrayList<>();

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bind(mListItem.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(mListItem.get(position) instanceof StickyListItem){
            return VIEW_TYPE_STICKY;
        }
        return VIEW_TYPE_NON_STICKY;
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    void setListItem(List<String> newItems){
        mListItem.clear();
        for(String item:newItems){
            mListItem.add(new ListItem(item));
        }
        Collections.sort(mListItem, (o1, o2) -> {
            if(o1.letters.equals("#") && !o2.letters.equals("#")){
                return -1;
            }else if(!o1.letters.equals("#") && o2.letters.equals("#")){
                return 1;
            }else{
                return o1.letters.compareToIgnoreCase(o2.letters);
            }
        });

        StickyListItem stickyListItem = null;
        for(int i=0,size = mListItem.size();i < size;i++){
            if(stickyListItem == null
                    || !mListItem.get(i).letters.equals(mListItem.get(i+1).letters)){
                stickyListItem = new StickyListItem(mListItem.get(i).letters);
                mListItem.add(i,stickyListItem);
                size += 1;
            }
        }
        notifyDataSetChanged();
    }
}