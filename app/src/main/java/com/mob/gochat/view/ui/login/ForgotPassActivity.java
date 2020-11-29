package com.mob.gochat.view.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mob.gochat.R;
import com.mob.gochat.url.URL;
import com.mob.gochat.utils.EmailUtil;
import com.mob.gochat.utils.HttpClientUtil;
import com.mob.gochat.utils.Sha256Util;
import com.mob.gochat.view.ui.customizeview.TimingTextView;

import java.util.HashMap;

public class ForgotPassActivity extends AppCompatActivity {

    private TimingTextView mTTVSendCode;
    private EditText mETMail,mETCode,mETPass,mETCPass;
    private Button mBtnResetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        findViewById();
        OnClick onClick = new OnClick();
        mBtnResetPass.setOnClickListener(onClick);
        mTTVSendCode.setOnClickListener(onClick);
    }

    private void findViewById(){
        mBtnResetPass = findViewById(R.id.btn_reset);
        mTTVSendCode = findViewById(R.id.tv_send_code);
        mETMail = findViewById(R.id.et_mail);
        mETCode = findViewById(R.id.et_code);
        mETPass = findViewById(R.id.et_password);
        mETCPass = findViewById(R.id.et_password_confirm);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_reset:
                    if(TextUtils.isEmpty(mETMail.getText())
                            || TextUtils.isEmpty(mETCode.getText())
                            || TextUtils.isEmpty(mETPass.getText())
                            || TextUtils.isEmpty(mETCPass.getText())){
                        Toast.makeText(ForgotPassActivity.this,"请输入全部信息",Toast.LENGTH_LONG).show();
                    }else{
                        new Thread(() -> {
                            HashMap<String,String> paramsMap = new HashMap<>();
                            paramsMap.put("mail",mETMail.getText().toString());
                            paramsMap.put("password", Sha256Util.getSHA256(mETPass.getText().toString()));
                            paramsMap.put("code",mETCode.getText().toString());
                            String result = HttpClientUtil.HttpPost(URL.forgot,null
                                    ,paramsMap);
                            JSONObject resultJson = (JSONObject) JSON.parse(result);
                            ThreadToast(resultJson.getString("msg"));
                            if(resultJson.getInteger("code")==1){
                                finish();
                            }
                        }).start();
                    }
                    break;
                case R.id.tv_send_code:
                    TimingTextView ttv = (TimingTextView) v;
                    if(!ttv.isRunning()){
                        if(EmailUtil.isEmailValid(mETMail.getText().toString())){
                            ttv.start();
                            new Thread(() -> {
                                String result = HttpClientUtil.HttpGet(URL.getCode(mETMail.getText().toString()));
                                JSONObject resultJson = (JSONObject) JSON.parse(result);
                                if(resultJson.getInteger("code") == -1
                                        || resultJson.getInteger("code") == 0){
                                    ttv.stop();
                                    ThreadToast(resultJson.getString("msg"));
                                }
                            }).start();
                        }else{
                            Toast.makeText(ForgotPassActivity.this,"请输入正确的邮箱地址",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }

    private void ThreadToast(String msg){
        Looper.prepare();
        Toast.makeText(ForgotPassActivity.this,msg,Toast.LENGTH_LONG).show();
        Looper.loop();
    }
}