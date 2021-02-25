package com.mob.gochat.view.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityForgotPassBinding;
import com.mob.gochat.url.URL;
import com.mob.gochat.utils.EmailUtil;
import com.mob.gochat.http.Http;
import com.mob.gochat.utils.Sha256Util;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.Callable;
import com.mob.gochat.view.ui.widget.TimingTextView;

import java.io.IOException;
import java.util.HashMap;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    @NonNull
    protected ActivityForgotPassBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        binding.btnReset.setOnClickListener(this);
        binding.tvSendCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset:
                if(TextUtils.isEmpty(binding.etMail.getText())
                        || TextUtils.isEmpty(binding.etCode.getText())
                        || TextUtils.isEmpty(binding.etPassword.getText())
                        || TextUtils.isEmpty(binding.etPasswordConfirm.getText())){
                    ToastUtil.showMsg(context, "请输入全部信息");
                }else if(!binding.etPassword.getText().toString().equals(binding.etPasswordConfirm.getText().toString())){
                    ToastUtil.showMsg(context, "两次密码输入不同");
                }else{
                    Http.postForgot(ForgotPassActivity.this,
                            binding.etMail.getText().toString(),
                            Sha256Util.getSHA256(binding.etPassword.getText().toString()),
                            binding.etCode.getText().toString(),
                            obj -> finish());
                }
                break;
            case R.id.tv_send_code:
                TimingTextView ttv = (TimingTextView) v;
                if(!ttv.isRunning()){
                    if(EmailUtil.isEmailValid(binding.etMail.getText().toString())){
                        ttv.start();
                        Http.getCode(ForgotPassActivity.this,
                                binding.etMail.getText().toString(),
                                obj -> {
                                    if (obj == 300) {
                                        ttv.stop();
                                        ToastUtil.showMsg(context, "发送验证码失败");
                                    }
                                });
                    }else{
                        ToastUtil.showMsg(context, "请输入正确的邮箱地址");
                    }
                }
                break;
        }
    }
}