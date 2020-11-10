package com.mob.gochat.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mob.gochat.R;
import com.mob.gochat.Util.EmailUtil;
import com.mob.gochat.Util.HttpClientUtil;
import com.mob.gochat.Util.Sha256Util;
import com.mob.gochat.CustomizeView.TimingTextView;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextView mTVLogin;
    private TimingTextView mTTVSendCode;
    private Button mBtnRegister;
    private EditText mETMail,mETCode,mETName,mETPass,mETCPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById();
        OnClick onClick = new OnClick();
        mTVLogin.setOnClickListener(onClick);
        mBtnRegister.setOnClickListener(onClick);
        mTTVSendCode.setOnClickListener(onClick);
    }

    private void findViewById(){
        mTVLogin = findViewById(R.id.tv_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mTTVSendCode = findViewById(R.id.tv_send_code);
        mETMail = findViewById(R.id.et_mail);
        mETCode = findViewById(R.id.et_code);
        mETName = findViewById(R.id.et_username);
        mETPass = findViewById(R.id.et_password);
        mETCPass = findViewById(R.id.et_password_confirm);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_register:
                    if(TextUtils.isEmpty(mETMail.getText())
                            || TextUtils.isEmpty(mETCode.getText())
                            || TextUtils.isEmpty(mETName.getText())
                            || TextUtils.isEmpty(mETPass.getText())
                            || TextUtils.isEmpty(mETCPass.getText())){
                        Toast.makeText(RegisterActivity.this,"请输入全部信息",Toast.LENGTH_LONG).show();
                    }else if(!mETPass.getText().toString().equals(mETCPass.getText().toString())){
                        Toast.makeText(RegisterActivity.this,"两次密码输入不同",Toast.LENGTH_LONG).show();
                    } else{
                        new Thread(() -> {
                            HashMap<String,String> paramsMap = new HashMap<>();
                            paramsMap.put("mail",mETMail.getText().toString());
                            paramsMap.put("password", Sha256Util.getSHA256(mETPass.getText().toString()));
                            paramsMap.put("code",mETCode.getText().toString());
                            paramsMap.put("name",mETName.getText().toString());
                            String result = HttpClientUtil.HttpPost("http://10.0.2.2:8182/reg",null
                                    ,paramsMap);
                            JSONObject resultJson = (JSONObject) JSON.parse(result);
                            ThreadToast(resultJson.getString("msg"));
                            if(resultJson.getInteger("code")==1){
                                finish();
                            }
                        }).start();
                    }
                    break;
                case R.id.tv_login:
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_send_code:
                    TimingTextView ttv = (TimingTextView) v;
                    if(!ttv.isRunning()){
                        if(EmailUtil.isEmailValid(mETMail.getText().toString())){
                            ttv.start();
                            new Thread(() -> {
                                String result = HttpClientUtil.HttpGet("http://10.0.2.2:8182/getCode?mail="
                                        + mETMail.getText().toString());
                                JSONObject resultJson = (JSONObject) JSON.parse(result);
                                if(resultJson.getInteger("code") == -1
                                        || resultJson.getInteger("code") == 0){
                                    ThreadToast(resultJson.getString("msg"));
                                    ttv.stop();
                                }
                            }).start();
                        }else{
                            Toast.makeText(RegisterActivity.this,"请输入正确的邮箱地址",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }

    private void ThreadToast(String msg){
        Looper.prepare();
        Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_LONG).show();
        Looper.loop();
    }

}