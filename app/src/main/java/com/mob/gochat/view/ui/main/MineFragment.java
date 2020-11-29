package com.mob.gochat.view.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mob.gochat.viewmodel.UserViewModel;
import com.mob.gochat.R;

public class MineFragment extends Fragment {

    private UserViewModel userViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mine, container, false);

        TextView mTVGender = root.findViewById(R.id.tv_mine_gender);
        TextView mTVBirthday = root.findViewById(R.id.tv_mine_birthday);
        TextView mTVAddress = root.findViewById(R.id.tv_mine_address);
        mTVGender.setText("你你你");
        mTVBirthday.setText("你你你");
        mTVAddress.setText("你你你");
        return root;
    }
}