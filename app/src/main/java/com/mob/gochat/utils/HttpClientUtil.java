package com.mob.gochat.utils;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientUtil {
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
}
