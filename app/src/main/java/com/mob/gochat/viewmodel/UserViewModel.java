package com.mob.gochat.viewmodel;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mob.gochat.model.Buddy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends androidx.lifecycle.ViewModel {

    private final MutableLiveData<List<String>> mMsgData = new MutableLiveData<>();
    private final List<String> msgData = new LinkedList<>();
    private final MutableLiveData<List<Buddy>> mBuddyData = new MutableLiveData<>();
    private final List<Buddy> buddyData = new LinkedList<>();

    public MutableLiveData<List<String>> getMsgData() {
        return mMsgData;
    }

    public MutableLiveData<List<Buddy>> getBuddyData() {
        return mBuddyData;
    }

    public void removeMsgData(int position){
        msgData.remove(position);
        mMsgData.postValue(msgData);
    }

    public void initMsgData(){
        List<String> list = Arrays.asList("1","2","3","4","5","6");
        for(String s : list){
            msgData.add(s);
        }
        mMsgData.setValue(msgData);
    }

    public void initBuddyData(){
        List<String> list = Arrays.asList("胡泉源",
                "李相赫", "唐志明", "朱泽楷", "郑锐轩", "陈继恩", "李旭辉", "詹智聪", "坠毁", "李科",
                "吴彦祖", "王健成", "张伪娘", "林丰涛", "序伟", "张伪娘", "郑泽芬", "魏法峰", "%guozehong",
                "我就是大名鼎鼎的鸟人辉了", "欧阳说ok", "一斤", "冼毅贤", "欧阳说ok", "刘启炬", "英语六级小天才胡茄声",
                "钧仔", "花都嗨", "张伪娘", "男神", "张伪娘", "张伪娘", "张飞", "jcj", "刘大壮", "李素函",
                "比利王", "龟王李科", "郑泽芬", "吴彦祖", "陈铎友", "?????", "黄家欣", "陈彦儒", "李白",
                "赖杰颖", "阿桂", "邓沛锦", "斯内克", "李升辉", "鸭王钟", "关羽", "欧阳宇豪", "高中大馒头",
                "邓银生", "林小七", "uzi", "张飞", "韶关学院地头蛇", "duckingwu", "卢本伟", "高渐离婚",
                "韶院过江龙欧阳宇豪", "爱新觉罗福泉", "%guozehong", "郭仔凡");
        for(String s : list){
            Buddy model = new Buddy("000",s,"","");
            buddyData.add(model);
        }
        Collections.sort(buddyData, (o1, o2) -> {
            if(o1.getLetters().equals("#") && !o2.getLetters().equals("#")){
                return 1;
            }else if(!o1.getLetters().equals("#") && o2.getLetters().equals("#")){
                return -1;
            }else{
                if(!o1.getLetters().equals(o2.getLetters())) {
                    return o1.getLetters().compareToIgnoreCase(o2.getLetters());
                }else{
                    return Pinyin.toPinyin(o1.getName(),"")
                            .compareToIgnoreCase(Pinyin.toPinyin(o2.getName(),""));
                }
            }
        });
        mBuddyData.setValue(buddyData);
    }
}