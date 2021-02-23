package com.mob.gochat.view.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.gochat.R;
import com.mob.gochat.url.URL;
import com.mob.gochat.http.Http;
import com.mob.gochat.utils.Sha256Util;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText mETUser,mETPassword;
    private Button mBtnLogin;
    private TextView mTVSign,mTVForgot;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById();
        OnClick onClick = new OnClick();
        mBtnLogin.setOnClickListener(onClick);
        mTVSign.setOnClickListener(onClick);
        mTVForgot.setOnClickListener(onClick);
    }

    private void findViewById(){
        mETUser = findViewById(R.id.et_mail);
        mETPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mTVSign = findViewById(R.id.tv_register);
        mTVForgot = findViewById(R.id.tv_forgot);
    }



    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.btn_login:
                    if(TextUtils.isEmpty(mETUser.getText())
                            || TextUtils.isEmpty(mETPassword.getText())){
                        Toast.makeText(LoginActivity.this,"请输入全部信息",Toast.LENGTH_LONG).show();
                    }else{
                        new Thread(() -> {
                            HashMap<String,String> paramsMap = new HashMap<>();
                            paramsMap.put("number",mETUser.getText().toString());
                            paramsMap.put("password", Sha256Util.getSHA256(mETPassword.getText().toString()));
                            String result = Http.HttpPost(URL.login,null, paramsMap);
//                            JSONObject resultJson = (JSONObject) JSON.parse(result);
//                            switch (resultJson.getInteger("code")){
//                                case 0:
//                                case -1:
//                                    ThreadToast(resultJson.getString("msg"));
//                                    break;
//                                case 1:
//                                    mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
//                                    mEditor = mSharedPreferences.edit();
//                                    mEditor.putString("token",resultJson.getString("msg"));
//                                    mEditor.apply();
//                                    finish();
//                                    break;
//                                default:
//                                    break;
//                            }
                        }).start();
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

    private void ThreadToast(String msg){
        Looper.prepare();
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show();
        Looper.loop();
    }

}