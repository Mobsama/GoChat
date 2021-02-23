package com.mob.gochat.model;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

public class PostRequest {
    @Getter
    @Setter
    @Expose()
    private int status;
    @Getter
    @Setter
    @Expose()
    private String message;
}
