package com.mob.gochat.url;

import rxhttp.wrapper.annotation.DefaultDomain;

public class URL {
    public static final String login = "/login";
    public static final String register = "/register";
    public static final String forgot = "/reset";
    public static final String token = "/token";
    public static final String code = "/code";
    public static final String user = "/user";
    public static final String isBuddy = "/is_buddy";
    public static final String add = "/add";
    public static final String delete = "/delete";
    public static final String remark = "/remark";
    @DefaultDomain
    public static final String baseURL = "http://mobsan.top:3000";

}
