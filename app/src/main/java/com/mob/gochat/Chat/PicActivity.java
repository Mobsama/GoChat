package com.mob.gochat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mob.gochat.R;
import com.mob.gochat.Util.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PicActivity extends AppCompatActivity {

    private RecyclerView mRVPic;
    private Button mBtnSend;
    private int choose = -1;
    private PicAdapter picAdapter;
    private List<PicModel> data;
    private final PicHandle picHandle = new PicHandle(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择图片");
        SmartSwipe.wrap(this)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                .enableDirection(SwipeConsumer.DIRECTION_LEFT)
                .setEdgeSize(100);

        mRVPic = findViewById(R.id.pic_rv);
        mBtnSend = findViewById(R.id.pic_btn_send);
        mBtnSend.setEnabled(false);

        data = new ArrayList<>();
        loadImages();

        picAdapter = new PicAdapter(R.layout.pic_list_item,data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        mRVPic.setLayoutManager(gridLayoutManager);
        picAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(data.get(position).isChoose){
                data.get(position).isChoose = false;
                choose = -1;
                mBtnSend.setEnabled(false);
            }else{
                if(choose != -1) {
                    data.get(choose).isChoose = false;
                }
                choose = position;
                data.get(position).isChoose = true;
                mBtnSend.setEnabled(true);
            }
            picAdapter.notifyDataSetChanged();
        });

        mRVPic.setAdapter(picAdapter);
        mBtnSend.setOnClickListener(v -> {
            ToastUtil.showMsg(this,data.get(choose).picPath);
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

    static class PicModel{
        private boolean isChoose;
        private String picPath;
        PicModel(String picPath){
            isChoose = false;
            this.picPath = picPath;
        }
    }

    private void loadImages(){
        PicThread picThread = new PicThread(this);
        picThread.start();
    }

    static class PicHandle extends Handler{
        private WeakReference<PicActivity> picActivityWeakReference;
        public PicHandle(PicActivity picActivity){
            picActivityWeakReference = new WeakReference<>(picActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            PicActivity picActivity = picActivityWeakReference.get();
            if(msg.what==1){
                picActivity.picAdapter.notifyDataSetChanged();
            }
        }
    }

    static class PicThread extends Thread{
        private WeakReference<PicActivity> picActivityWeakReference;
        public PicThread(PicActivity picActivity){
            picActivityWeakReference = new WeakReference<>(picActivity);
        }

        @Override
        public void run() {
            super.run();
            PicActivity picActivity = picActivityWeakReference.get();
            if(picActivity != null){
                String order=MediaStore.MediaColumns.DATE_ADDED+" DESC ";
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = picActivity.getContentResolver().query(uri,null,null,null,order);
                if(cursor != null && cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String path = cursor.getString(index);
                        File file = new File(path);
                        if(file.exists()){
                            picActivity.data.add(new PicModel(path));
                        }
                    }
                    cursor.close();
                }
                Message message = new Message();
                message.what = 1;
                picActivity.picHandle.sendMessage(message);
            }
        }
    }

    class PicAdapter extends BaseQuickAdapter<PicModel, BaseViewHolder>{

        public PicAdapter(int layoutResId, @Nullable List<PicModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, PicModel picModel) {
            Glide.with(getContext())
                .load(picModel.picPath)
                .centerCrop()
                .into((ImageView) baseViewHolder.getView(R.id.pic_item_iv));
            if(picModel.isChoose){
                baseViewHolder.setVisible(R.id.pic_item_choose,true);
            }else{
                baseViewHolder.setVisible(R.id.pic_item_choose,false);
            }
        }
    }
}