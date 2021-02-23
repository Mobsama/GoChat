package com.mob.gochat.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.mob.gochat.MainApp;
import com.mob.gochat.model.Msg;
import com.mob.gochat.model.PostRequest;
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

    public static void sendFile(int type, @NonNull String path, @NonNull Callable callable){
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
                    PostRequest request = MainApp.getInstance().getGson().fromJson(s, PostRequest.class);
                    if(request.getStatus() == 200){
                        callable.call("success");
                    }else{
                        callable.call("error");
                    }
                }, throwable -> {
                    callable.call("error");
                });
    }

    public static void sendText(Msg msg, String[] msgJson, Callable callable){
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

    public static void getFile(int type, String name, Callable callable){
        String getType;
        if(type == Msg.PIC){
            getType = "/pic/";
        }else{
            getType = "/audio/";
        }
        RxHttp.get(getType + name)
                .asDownload(MainApp.getInstance().getFilesDir().getAbsolutePath() + getType + name)
                .subscribe(path -> {
                    callable.call(path);
                }, throwable -> {

                });
    }
}
