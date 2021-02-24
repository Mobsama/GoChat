package com.mob.gochat.view.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.mob.gochat.MainActivity;
import com.mob.gochat.R;
import com.mob.gochat.databinding.ActivityLoginBinding;
import com.mob.gochat.http.Http;
import com.mob.gochat.model.Buddy;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.btnLogin.setOnClickListener(this);
        binding.tvRegister.setOnClickListener(this);
        binding.tvForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_login:
                if(TextUtils.isEmpty(binding.etMail.getText())
                        || TextUtils.isEmpty(binding.etPassword.getText())){
                    Toast.makeText(LoginActivity.this,"请输入全部信息",Toast.LENGTH_LONG).show();
                }else{
                    Http.postLogin(LoginActivity.this, binding.etMail.getText().toString()
                            , Sha256Util.getSHA256(binding.etPassword.getText().toString()), s -> {
                                try{
                                    JSONObject object = new JSONObject(s);
                                    String token = object.getString("token");
                                    String id = object.getString("id");
                                    MMKVUitl.save(DataKeyConst.TOKEN, token);
                                    MMKVUitl.save(DataKeyConst.USER_ID, id);
                                    viewModel.getBuddy(id).observe(LoginActivity.this, b -> {
                                        if(b == null){
                                            Http.getUser(LoginActivity.this, id, new Callable<Buddy>() {
                                                @Override
                                                public void call(Buddy buddy) throws IOException {
                                                    viewModel.insertBuddy(buddy);
                                                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent1);
                                                    LoginActivity.this.finish();
                                                }
                                            });
                                        }else{
                                            Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent1);
                                            LoginActivity.this.finish();
                                        }
                                    });
                                }catch (Exception ignored){ }
                            });
                }
                break;
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgot:
                intent = new Intent(LoginActivity.this,ForgotPassActivity.class);
                startActivity(intent);
                break;
        }
    }
}