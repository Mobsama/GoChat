package com.mob.gochat.view.ui.main;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopupext.listener.CityPickerListener;
import com.lxj.xpopupext.listener.TimePickerListener;
import com.lxj.xpopupext.popup.CityPickerPopup;
import com.lxj.xpopupext.popup.TimePickerPopup;
import com.mob.gochat.MainActivity;
import com.mob.gochat.MainApp;
import com.mob.gochat.R;
import com.mob.gochat.databinding.FragmentMineBinding;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.FileUtil;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.ParcelUtil;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.Callable;
import com.mob.gochat.view.base.ImageLoader;
import com.mob.gochat.view.ui.chat.PicFragment;
import com.mob.gochat.view.ui.login.LoginActivity;
import com.mob.gochat.viewmodel.ViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import lombok.SneakyThrows;

public class MineFragment extends Fragment implements View.OnClickListener {

    FragmentMineBinding binding;
    private ViewModel viewModel;
    private Buddy buddy, tempBuddy;
    private String userId;
    private boolean isChangeAvatar = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        initOnClick();
        userId = MMKVUitl.getString(DataKeyConst.USER_ID);
        viewModel.getBuddy(userId, userId).observe(getActivity(), buddy -> {
            this.buddy = buddy;
            this.tempBuddy = ParcelUtil.copy(this.buddy);
            initBuddy();
        });
        return binding.getRoot();
    }

    private void initOnClick(){
        binding.llMineGender.setOnClickListener(this);
        binding.llMineBirthday.setOnClickListener(this);
        binding.llMineAddress.setOnClickListener(this);
        binding.ivMineEdit.setOnClickListener(this);
        binding.btnMineSave.setOnClickListener(this);
        binding.tvMineName.setOnClickListener(this);
        binding.ivMineAvatarEdit.setOnClickListener(this);
        binding.btnMineCancel.setOnClickListener(this);
        binding.ivMineAvatar.setOnClickListener(this);
        binding.btnMineLogout.setOnClickListener(this);
        setClickable(false);
    }

    private void initBuddy(){
        binding.tvMineName.setText(buddy.getName());
        if(buddy.getAvatar() != null){
            try {
                binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeFile(MainApp.getInstance().getBaseContext().getFilesDir().getAbsolutePath() + "/pic/" + buddy.getAvatar()));
            }catch (Exception ignored){
                binding.ivMineAvatar.setImageDrawable(getResources().getDrawable(R.drawable.buddy));
            }
        }else {
            binding.ivMineAvatar.setImageDrawable(getResources().getDrawable(R.drawable.buddy));
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
    }

    @SneakyThrows
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_mine_gender:
                new XPopup.Builder(getContext())
                        .asBottomList("请选择性别", new String[]{"保密", "男", "女"},
                                (position, text) -> {
                                    binding.tvMineGender.setText(text);
                                    tempBuddy.setGender(position);
                                }).show();
                break;
            case R.id.ll_mine_birthday:
                Calendar date = Calendar.getInstance();
                date.set(2000,0,1);
                TimePickerPopup popup = new TimePickerPopup(getContext())
                        .setDefaultDate(date)
                        .setTimePickerListener(new TimePickerListener() {
                            @Override
                            public void onTimeChanged(Date date) { }
                            @Override
                            public void onTimeConfirm(Date date, View view) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy - MM - dd");
                                binding.tvMineBirthday.setText(format.format(date));
                                tempBuddy.setBirth(format.format(date));
                            }
                        });
                new XPopup.Builder(getContext())
                        .asCustom(popup)
                        .show();
                break;
            case R.id.ll_mine_address:
                CityPickerPopup pop = new CityPickerPopup(getContext());
                pop.setCityPickerListener(new CityPickerListener() {
                    @Override
                    public void onCityConfirm(String province, String city, String area, View v) {
                        binding.tvMineAddress.setText(province +" - " +city+" - " +area);
                        tempBuddy.setAddress(province +" - " +city+" - " +area);
                    }
                    @Override
                    public void onCityChange(String province, String city, String area) { }
                });
                new XPopup.Builder(getContext())
                        .asCustom(pop)
                        .show();
                break;
            case R.id.tv_mine_name:
                new XPopup.Builder(getContext()).asInputConfirm(buddy.getName(), "请输入新的昵称：",
                        text -> {
                            binding.tvMineName.setText(text);
                            tempBuddy.setName(text);
                        }).show();
                break;
            case R.id.iv_mine_avatar_edit:
                PicFragment picFragment = new PicFragment();
                Callable<String> callable = path -> {
                    isChangeAvatar = true;
                    tempBuddy.setAvatar(path);
                    binding.ivMineAvatar.setImageBitmap(BitmapFactory.decodeFile(path));
                };
                picFragment.show(getParentFragmentManager(), "PIC", callable, "确认");
                break;
            case R.id.iv_mine_edit:
                isChangeAvatar = false;
                binding.ivMineEdit.setVisibility(View.GONE);
                binding.btnMineSave.setVisibility(View.VISIBLE);
                binding.btnMineCancel.setVisibility(View.VISIBLE);
                binding.tvMineName.setBackground(getResources().getDrawable(R.drawable.mine_name_edit_bg));
                setClickable(true);
                break;
            case R.id.btn_mine_save:
                if(isChangeAvatar){
                    String uuid = UUID.randomUUID().toString();
                    String dir_path = getActivity().getFilesDir().getAbsolutePath() + "/pic/";
                    File file = new File(dir_path);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    String path = tempBuddy.getAvatar();
                    String suffix = path.substring(path.lastIndexOf("."));
                    file = new File(dir_path + uuid + suffix );
                    file.createNewFile();
                    FileUtil.copyFile(path, file.getPath());
                    Http.sendFile(Msg.PIC, file.getName(), filename -> {
                        tempBuddy.setAvatar(filename);
                        buddy = ParcelUtil.copy(this.tempBuddy);
                        Http.postUser(getContext(), buddy, status -> {
                            if(status == 200){
                                viewModel.updateBuddy(buddy);
                            }else{
                                ToastUtil.showMsg(getContext(), "更新失败");
                            }
                        });
                    });
                }else{
                    buddy = ParcelUtil.copy(this.tempBuddy);
                    Http.postUser(getContext(), buddy, status -> {
                        if(status == 200){
                            viewModel.updateBuddy(buddy);
                        }else{
                            ToastUtil.showMsg(getContext(), "更新失败");
                        }
                    });
                }
                setClickable(false);
                binding.tvMineName.setBackground(null);
                binding.ivMineEdit.setVisibility(View.VISIBLE);
                binding.btnMineSave.setVisibility(View.GONE);
                binding.btnMineCancel.setVisibility(View.GONE);
                break;
            case R.id.btn_mine_cancel:
                tempBuddy = ParcelUtil.copy(this.buddy);
                setClickable(false);
                binding.tvMineName.setBackground(null);
                binding.ivMineEdit.setVisibility(View.VISIBLE);
                binding.btnMineSave.setVisibility(View.GONE);
                binding.btnMineCancel.setVisibility(View.GONE);
                initBuddy();
                break;
            case R.id.iv_mine_avatar:
                if(buddy.getAvatar() != null){
                    new XPopup.Builder(getActivity())
                            .asImageViewer((ImageView) v, buddy.getAvatar(), new ImageLoader())
                            .isShowSaveButton(false)
                            .show();
                }
                break;
            case R.id.btn_mine_logout:
                new XPopup.Builder(getContext()).asConfirm(buddy.getName(), "是否要退出登录？",
                        () -> {
                            MMKVUitl.clear(DataKeyConst.TOKEN);
                            MMKVUitl.clear(DataKeyConst.USER_ID);
                            MainApp.getInstance().stopSocketIO();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .show();
                break;
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