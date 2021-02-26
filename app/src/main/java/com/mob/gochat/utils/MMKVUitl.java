package com.mob.gochat.utils;

import android.content.Context;
import android.os.Parcelable;

import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.Set;

public class MMKVUitl {
    private static volatile MMKV instance;

    synchronized public static MMKV getInstance() {
        if(instance == null){
            synchronized (MMKVUitl.class){
                instance = MMKV.defaultMMKV();
            }
        }
        return instance;
    }

    public static void initialize(Context context){
        MMKV.initialize(context);
    }

    public static void save(String key, String value){
        getInstance().encode(key,value);
    }

    public static void save(String key, int value){
        getInstance().encode(key,value);
    }

    public static void save(String key, boolean value){
        getInstance().encode(key,value);
    }

    public static void save(String key, Long value){
        getInstance().encode(key,value);
    }

    public static void save(String key, Set<String> value){
        getInstance().encode(key,value);
    }

    public static void save(String key, Float value){
        getInstance().encode(key,value);
    }

    public static void save(String key, Parcelable value){
        getInstance().encode(key,value);
    }

    public static void save(String key, byte[] value){
        getInstance().encode(key,value);
    }

    public static String getString(String key){
        return getInstance().decodeString(key);
    }

    public static int getInt(String key){
        return getInstance().decodeInt(key);
    }

    public static Float getFloat(String key){
        return getInstance().decodeFloat(key);
    }

    public static Long getLong(String key){
        return getInstance().decodeLong(key);
    }

    public static boolean getBool(String key){
        return getInstance().decodeBool(key);
    }

    public static boolean getBool(String key, boolean def){
        return getInstance().decodeBool(key, def);
    }

    public static Set<String> getStringSet(String key){
        return getInstance().decodeStringSet(key,new HashSet<>());
    }

    public static<T extends Parcelable> T getParcelable(String key, Class<T> clz){
        return getInstance().decodeParcelable(key,clz);
    }

    public static void clear(String key){
        getInstance().remove(key);
    }

}
