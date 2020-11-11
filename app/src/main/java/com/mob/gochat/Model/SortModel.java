package com.mob.gochat.Model;

import com.github.promeg.pinyinhelper.Pinyin;

public class SortModel{
    private String name;
    private String letters;

    public String getName(){ return name; }

    public void setName(String name) {
        char c;
        if(Pinyin.isChinese(name.charAt(0))){
            c = Pinyin.toPinyin(name.charAt(0)).toUpperCase().charAt(0);
        }else{
            c = name.toUpperCase().charAt(0);
            if(!(c >= 'A' && c <= 'Z')){
                c = '#';
            }
        }
        this.name = name;
        this.letters = c + "";
    }

    public String getLetters() {
        return letters;
    }
}