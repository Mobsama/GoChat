package com.mob.gochat.view.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mob.gochat.MainActivity;
import com.mob.gochat.MainApp;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityLoginBinding;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
import com.mob.gochat.utils.DataKeyConst;
import com.mob.gochat.utils.MMKVUitl;
import com.mob.gochat.utils.Sha256Util;
import com.mob.gochat.viewmodel.ViewModel;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    ViewModel viewModel;
    private Context context;
    LifecycleOwner lifecycleOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        lifecycleOwner = this;
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.btnLogin.setOnClickListener(this);
        binding.tvRegister.setOnClickListener(this);
        binding.tvForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(TextUtils.isEmpty(binding.etMail.getText())
                        || TextUtils.isEmpty(binding.etPassword.getText())){
                    Toast.makeText(context,"请输入全部信息",Toast.LENGTH_LONG).show();
                }else{
                    Http.postLogin(context, binding.etMail.getText().toString()
                            , Sha256Util.getSHA256(binding.etPassword.getText().toString()), s -> {
                                try{
                                    JSONObject object = new JSONObject(s);
                                    String token = object.getString("token");
                                    String id = object.getInt("id") + "";
                                    getUser(token, id);
                                }catch (Exception ignored){ }
                            });
                }
                break;
            case R.id.tv_register:
                gotoRegister();
                break;
            case R.id.tv_forgot:
                gotoForgot();
                break;
        }
    }

    public void getUser(String token, String id){
        Http.getUser(context, id, id, str -> {
            PostRequest request = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
            if(request.getStatus() == 200){
                Buddy buddy = MainApp.getInstance().getGson().fromJson(request.getMessage(), Buddy.class);
                buddy.setLettersWithName(buddy.getName());
                if(buddy.getAvatar() == null){
                    viewModel.insertBuddy(buddy);
                    resetMMKV(id, token);
                    gotoMain();
                }else{
                    Log.d("LOGIN", buddy.getName());
                    Http.getFile(this, Msg.PIC, buddy.getAvatar(), path -> {
                        viewModel.insertBuddy(buddy);
                        resetMMKV(id, token);
                        gotoMain();
                    });
                }
            }
        });
    }

    private void resetMMKV(String id, String token){
        MMKVUitl.clear(DataKeyConst.TOKEN);
        MMKVUitl.clear(DataKeyConst.USER_ID);
        MMKVUitl.save(DataKeyConst.USER_ID, id);
        MMKVUitl.save(DataKeyConst.TOKEN, token);
    }

    private void gotoMain(){
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoRegister(){
        Intent intent = new Intent(context,RegisterActivity.class);
        startActivity(intent);
    }

    private void gotoForgot(){
        Intent intent = new Intent(context,ForgotPassActivity.class);
        startActivity(intent);
    }
}