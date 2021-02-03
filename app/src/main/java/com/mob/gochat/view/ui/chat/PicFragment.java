package com.mob.gochat.view.ui.chat;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mob.gochat.R;
import com.mob.gochat.databinding.FragmentPicBinding;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.Pic;
import com.mob.gochat.view.adapter.PicAdapter;
import com.mob.gochat.viewmodel.ChatViewModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

public class PicFragment extends BottomSheetDialogFragment {
    private FragmentPicBinding binding;

    private ChatViewModel chatViewModel;

    private int choose = -1;
    private PicAdapter picAdapter;
    private List<Pic> data;
    private final PicHandle picHandle = new PicHandle(this);
    private BottomSheetBehavior behavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(),R.layout.fragment_pic,null);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPicBinding.inflate(inflater,container,false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(getActivity()).get(ChatViewModel.class);
        data = new LinkedList<>();
        picAdapter = new PicAdapter(R.layout.pic_list_item,data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        binding.picRv.setLayoutManager(gridLayoutManager);

        picAdapter.setOnItemClickListener((adapter, v, position) -> {
            if(data.get(position).isChoose()){
                data.get(position).setChoose(false);
                choose = -1;
            }else{
                if(choose != -1) {
                    data.get(choose).setChoose(false);
                }
                choose = position;
                data.get(position).setChoose(true);

            }
            binding.picBtnSend.setEnabled(choose != -1);
            picAdapter.notifyDataSetChanged();
        });

        binding.picRv.setAdapter(picAdapter);
        binding.picBtnSend.setOnClickListener(v -> {
//            Msg msg = new Msg(data.get(choose).getPicPath(),new Random().nextInt(2), Msg.PIC);
//            chatViewModel.addMsg(msg);
            dismiss();
        });
        binding.picClose.setOnClickListener(v -> {
            dismiss();
        });
        loadImages();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.picBtnSend.setEnabled(choose != -1);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.getRoot().getLayoutParams().height = getPeekHeight();
        behavior.setPeekHeight(getPeekHeight());
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight - peekHeight / 8;
    }

    private void loadImages(){
        PicThread picThread = new PicThread(this);
        picThread.start();
    }

    static class PicHandle extends Handler {
        private WeakReference<PicFragment> picFragmentWeakReference;
        public PicHandle(PicFragment picFragment){
            picFragmentWeakReference = new WeakReference<>(picFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            PicFragment picFragment = picFragmentWeakReference.get();
            if(picFragment != null){
                if(msg.what==1){
                    picFragment.picAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    @Override
    public void dismiss() {
        choose = -1;
        super.dismiss();
    }

    static class PicThread extends Thread{
        private WeakReference<PicFragment> picFragmentWeakReference;
        public PicThread(PicFragment picFragment){
            picFragmentWeakReference = new WeakReference<>(picFragment);
        }

        @Override
        public void run() {
            super.run();
            PicFragment picFragment = picFragmentWeakReference.get();
            if(picFragment != null){
                String order= MediaStore.MediaColumns.DATE_ADDED+" DESC ";
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = picFragment.getActivity().getContentResolver().query(uri,null,null,null,order);
                if(cursor != null && cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String path = cursor.getString(index);
                        File file = new File(path);
                        if(file.exists()){
                            picFragment.data.add(new Pic(path));
                        }
                    }
                    cursor.close();
                }
                Message message = new Message();
                message.what = 1;
                picFragment.picHandle.sendMessage(message);
            }
        }
    }
}