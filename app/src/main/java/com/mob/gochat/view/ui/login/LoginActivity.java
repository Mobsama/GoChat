package com.mob.gochat.view.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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
import com.mob.gochat.view.base.Callable;
import com.mob.gochat.viewmodel.ViewModel;

import org.json.JSONObject;

import java.io.IOException;

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
                                    if(MMKVUitl.getString(DataKeyConst.TOKEN) == null || MMKVUitl.getString(DataKeyConst.TOKEN).equals("")){
                                        MMKVUitl.save(DataKeyConst.USER_ID, id);
                                        Http.getUser(context, id, id, str -> {
                                            PostRequest request = MainApp.getInstance().getGson().fromJson(str, PostRequest.class);
                                            if(request.getStatus() == 200){
                                                Buddy buddy = MainApp.getInstance().getGson().fromJson(request.getMessage(), Buddy.class);
                                                if(buddy.getAvatar() != null || !buddy.getAvatar().equals("")){
                                                    Log.d("LOGIN", buddy.getAvatar());
                                                    Http.getFile(this, Msg.PIC, buddy.getAvatar(), path -> {
                                                        viewModel.insertBuddy(buddy);
                                                        MMKVUitl.save(DataKeyConst.TOKEN, token);
                                                        gotoMain();
                                                    });
                                                }
                                            }
                                        });
                                    }else{
                                        MMKVUitl.save(DataKeyConst.USER_ID, id);
                                        MMKVUitl.save(DataKeyConst.TOKEN, token);
                                        gotoMain();
                                    }
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