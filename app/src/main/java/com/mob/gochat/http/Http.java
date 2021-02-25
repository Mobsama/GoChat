package com.mob.gochat.http;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mob.gochat.MainApp;
import com.mob.gochat.model.Buddy;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
import com.mob.gochat.url.URL;
import com.mob.gochat.utils.ToastUtil;
import com.mob.gochat.view.base.Callable;
import com.rxjava.rxlife.RxLife;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rxhttp.RxHttp;

public class Http {
    static OkHttpClient httpClient = new OkHttpClient();
    public static String HttpGet(String url) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }catch (Exception e){
            return "{'code':'-1','msg':'连接服务器失败'}";
        }
    }

    public static String HttpPost(String url, String token, HashMap<String,String> paramsMap) {
        FormBody.Builder builder = new FormBody.Builder();
        for(String key : paramsMap.keySet()){
            builder.add(key,paramsMap.get(key));
        }
        RequestBody body = builder.build();
        Request request;
        if(token==null){
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .addHeader("token",token)
                    .post(body)
                    .build();
        }

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }catch (Exception e){
            return "{'code':'-1','msg':'连接服务器失败'}";
        }
    }

    public static void sendFile(int type, @NonNull String path, @NonNull Callable<String> callable){
        String getType;
        if(type == Msg.PIC){
            getType = "/pic/";
        }else{
            getType = "/audio/";
        }
        File file = new File(MainApp.getInstance().getBaseContext().getFilesDir().getAbsolutePath() + getType + path);
        RxHttp.postForm(getType + "upload")
                .addFile("file", file.getName(), file)
                .asString()
                .subscribe(s -> {
                    Log.d("FILE", "ERROR");
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    if(request.getStatus() == 200){
                        Log.d("FILE", file.getName());
                        callable.call(file.getName());
                    }
                }, throwable -> {
                    Log.d("FILE", "ERROR");
                });
    }

    public static void sendText(Msg msg, String[] msgJson, Callable<String> callable){
        MainApp.getInstance().getSocket().emit("message", msgJson, (args) -> {
            JSONObject object = (JSONObject) args[0];
            try {
                if(object.getString("status").equals("ok")){
                    callable.call("success");
                }else{
                    callable.call("error");
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getFile(Context context, int type, String name, Callable<String> callable) {
        String getType;
        if(type == Msg.PIC){
            getType = "/pic/";
        }else{
            getType = "/audio/";
        }
        RxHttp.get(getType + name)
                .asDownload(context.getFilesDir().getAbsolutePath() + getType + name)
                .subscribe(callable::call, throwable -> {
                    Log.d("GET_FILE", "ERROR");
                });
    }

    public static void postLogin(Context context, String number, String pass, Callable<String> callable){
        RxHttp.postForm(URL.login)
                .add("number", number)
                .add("password", pass)
                .asString()
                .subscribe(s -> {
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    if(request.getStatus() == 200){
                        callable.call(request.getMessage());
                    }else {
                        ThreadToast(context, request.getMessage());
                    }
                }, throwable -> {
                    Log.d("LOGIN", "ERROR");
                    ThreadToast(context,"连接服务器失败");
                });
    }

    public static void postRegister(Context context, String mail, String password, String code, String name, Callable<String> callable){
        RxHttp.postForm(URL.register)
                .add("mail", mail)
                .add("password", password)
                .add("code", code)
                .add("name", name)
                .asString()
                .subscribe(s -> {
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    Log.d("request", request.getMessage());
                    if(request.getStatus() == 200){
                        callable.call(request.getMessage());
                    }else{
                        ThreadToast(context, request.getMessage());
                    }
                }, throwable -> {
                    Log.d("request", "ERROR");
                    ThreadToast(context, "连接服务器失败");
                });
    }

    public static void postForgot(Context context, String mail, String password, String code, Callable<String> callable){
        RxHttp.postForm(URL.forgot)
                .add("mail", mail)
                .add("password", password)
                .add("code", code)
                .asString()
                .subscribe(s -> {
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    if(request.getStatus() == 200){
                        callable.call(request.getMessage());
                    }else{
                        ThreadToast(context, request.getMessage());
                    }
                }, throwable -> {
                    Log.d("Forgot", "ERROR");
                    ThreadToast(context, "连接服务器失败");
                });
    }

    public static void getCode(Context context, String mail, Callable<Integer> callable){
        RxHttp.get(URL.code)
                .add("mail", mail)
                .asString()
                .subscribe(s -> {
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    callable.call(request.getStatus());
                }, throwable -> {
                    Log.d("code", "ERROR");
                    ThreadToast(context, "连接服务器失败");
                });
    }

    public static void getUser(Context context, String id, String userId, Callable<String> callable){
        RxHttp.get(URL.user)
                .add("number",id)
                .add("userId", userId)
                .asString()
                .subscribe(s -> callable.call(s), throwable -> {
                    Log.d("get_user", "ERROR");
                    ThreadToast(context, "连接服务器失败");
                });
    }

    public static void postUser(Context context, Buddy buddy, Callable<Integer> callable){
        RxHttp.postForm(URL.user)
                .add("number",buddy.getId())
                .add("name", buddy.getName())
                .add("gender", buddy.getGender() + "")
                .add("birth", buddy.getBirth())
                .add("address", buddy.getAddress())
                .add("avatar", buddy.getAvatar())
                .asString()
                .subscribe(s -> {
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    callable.call(request.getStatus());
                }, throwable -> {
                    Log.d("post_user", "ERROR");
                    ThreadToast(context, "连接服务器失败");
                });
    }

    private static void ThreadToast(Context context, String msg) {
        Looper.prepare();
        ToastUtil.showMsg(context, msg);
        Looper.loop();
    }
}
