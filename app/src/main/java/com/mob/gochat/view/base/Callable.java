package com.mob.gochat.view.base;


import java.io.IOException;

public interface Callable<T>{
    void call(T obj) throws IOException;
}
