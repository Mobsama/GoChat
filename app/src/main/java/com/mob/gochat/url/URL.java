package com.mob.gochat.url;

public class URL {
    public static final String serverURL = "http://10.0.2.2:8182";
    public static final String login = serverURL + "/login";
    public static final String register = serverURL + "/reg";
    public static final String forgot = serverURL + "/reset";
    public static final String token = serverURL + "/token";

    public static String getCode(String mail){
        return serverURL + "/getCode?mail=" + mail;
    }
}
