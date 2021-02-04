package com.mob.gochat.view.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopupext.listener.CityPickerListener;
import com.lxj.xpopupext.listener.TimePickerListener;
import com.lxj.xpopupext.popup.CityPickerPopup;
import com.lxj.xpopupext.popup.TimePickerPopup;
import com.mob.gochat.databinding.FragmentMineBinding;
import com.mob.gochat.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MineFragment extends Fragment implements View.OnClickListener {

    private UserViewModel userViewModel;
    private FragmentMineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        binding.tvMineGender.setText("你你你");
        binding.tvMineBirthday.setText("你你你");
        binding.tvMineAddress.setText("你你你");

        binding.llMineGender.setOnClickListener(this);
        binding.llMineBirthday.setOnClickListener(this);
        binding.llMineAddress.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(v == binding.llMineGender){
            new XPopup.Builder(getContext())
                    .asBottomList("请选择性别", new String[]{"男", "女", "保密"},
                            (position, text) -> {
                                binding.tvMineGender.setText(text);
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
                }

                @Override
                public void onCityChange(String province, String city, String area) {

                }
            });
            new XPopup.Builder(getContext())
                    .asCustom(popup)
                    .show();
        }
    }
}