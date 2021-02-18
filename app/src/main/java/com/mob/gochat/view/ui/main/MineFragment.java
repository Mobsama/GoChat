package com.mob.gochat.view.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopupext.listener.CityPickerListener;
import com.lxj.xpopupext.listener.TimePickerListener;
import com.lxj.xpopupext.popup.CityPickerPopup;
import com.lxj.xpopupext.popup.TimePickerPopup;
import com.mob.gochat.R;
import com.mob.gochat.databinding.FragmentMineBinding;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.view.ui.chat.PicFragment;
import com.mob.gochat.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MineFragment extends Fragment implements View.OnClickListener {

    private FragmentMineBinding binding;
    private ViewModel viewModel;
    private Buddy buddy;
    private final static String userId = MMKVUitl.getString(DataKeyConst.USER_ID);

    public MineFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);

        viewModel.getBuddy(userId).observe(getActivity(), buddy -> {
            this.buddy = buddy;
            initView();
        });
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        binding.tvMineName.setText(buddy.getName());
        if(buddy.getAvatar() != null){
            try {
                binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeFile(buddy.getAvatar()));
            }catch (Exception ignored){
                binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeStream(this.getResources().openRawResource(R.raw.test)));
            }
        }else {
            binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeStream(this.getResources().openRawResource(R.raw.test)));
        }

        binding.tvMineNumber.setText("GoChat号：" + buddy.getId());
        if(buddy.getGender() == 0){
            binding.tvMineGender.setText("保密");
        }else if(buddy.getGender() == 1) {
            binding.tvMineGender.setText("男");
        }else if(buddy.getGender() == 2) {
            binding.tvMineGender.setText("女");
        }
        binding.tvMineBirthday.setText(buddy.getBirth());
        binding.tvMineAddress.setText(buddy.getAddress());

        binding.llMineGender.setOnClickListener(this);
        binding.llMineBirthday.setOnClickListener(this);
        binding.llMineAddress.setOnClickListener(this);
        binding.ivMineEdit.setOnClickListener(this);
        binding.btnMineSave.setOnClickListener(this);
        binding.tvMineName.setOnClickListener(this);
        binding.ivMineAvatarEdit.setOnClickListener(this);

        setClickable(false);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.llMineGender){
            new XPopup.Builder(getContext())
                    .asBottomList("请选择性别", new String[]{"保密", "男", "女"},
                            (position, text) -> {
                                binding.tvMineGender.setText(text);
                                buddy.setGender(position);
                            }).show();
        }else if(v == binding.llMineBirthday){
            Calendar date = Calendar.getInstance();
            date.set(2000,0,1);
            TimePickerPopup popup = new TimePickerPopup(getContext())
                    .setDefaultDate(date)
                    .setTimePickerListener(new TimePickerListener() {
                        @Override
                        public void onTimeChanged(Date date) {

                        }

                        @Override
                        public void onTimeConfirm(Date date, View view) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy - MM - dd");
                            binding.tvMineBirthday.setText(format.format(date));
                            buddy.setBirth(format.format(date));
                        }
                    });
            new XPopup.Builder(getContext())
                    .asCustom(popup)
                    .show();
        }else if(v == binding.llMineAddress){
            CityPickerPopup popup = new CityPickerPopup(getActivity());
            popup.setCityPickerListener(new CityPickerListener() {
                @Override
                public void onCityConfirm(String province, String city, String area, View v) {
                    binding.tvMineAddress.setText(province +" - " +city+" - " +area);
                    buddy.setAddress(province +" - " +city+" - " +area);
                }

                @Override
                public void onCityChange(String province, String city, String area) {

                }
            });
            new XPopup.Builder(getContext())
                    .asCustom(popup)
                    .show();
        }else if(v == binding.tvMineName){
            new XPopup.Builder(getContext()).asInputConfirm(buddy.getName(), "请输入新的昵称：",
                text -> {
                    buddy.setName(text);
                    binding.tvMineName.setText(text);
                }).show();
        }else if(v == binding.ivMineAvatarEdit){
            PicFragment picFragment = new PicFragment();
            PicFragment.Callable callable = path -> {
                buddy.setAvatar(path);
                binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeFile(path));
            };
            picFragment.show(getParentFragmentManager(), "PIC", callable, "确认");
        }else if(v == binding.ivMineEdit){
            binding.ivMineEdit.setVisibility(View.GONE);
            binding.btnMineSave.setVisibility(View.VISIBLE);
            binding.tvMineName.setBackground(getResources().getDrawable(R.drawable.mine_name_edit_bg));
            setClickable(true);
        }else if(v == binding.btnMineSave){
            viewModel.updateBuddy(buddy);
            setClickable(false);
            binding.tvMineName.setBackground(null);
            binding.ivMineEdit.setVisibility(View.VISIBLE);
            binding.btnMineSave.setVisibility(View.GONE);
        }
    }

    private void setClickable(boolean flag){
        binding.llMineGender.setClickable(flag);
        binding.llMineBirthday.setClickable(flag);
        binding.llMineAddress.setClickable(flag);
        binding.ivMineAvatarEdit.setClickable(flag);
        binding.tvMineName.setClickable(flag);

        binding.ivMineAddressNext.setVisibility(flag ? View.VISIBLE : View.GONE);
        binding.ivMineBirthNext.setVisibility(flag ? View.VISIBLE : View.GONE);
        binding.ivMineGenderNext.setVisibility(flag ? View.VISIBLE : View.GONE);
        binding.ivMineAvatarEdit.setVisibility(flag ? View.VISIBLE : View.GONE);
    }
}