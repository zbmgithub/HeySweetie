package com.heysweetie.android;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;

public class HeySweetieApplication extends Application {
    @SuppressLint("StatisticFieldLeak")
    public static Context context;//全局获取Context
    public static final String ApplicationID = "7bc8330964e0ceca10d1a6934d6679f7";//Bmob接口id
    public static Map<Goods, Integer> shopCartMap = new HashMap<>();//存储购物车信息，goods代表商品，integer存储的数量
    public static List<Integer> imageId = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Bmob.initialize(this, ApplicationID);//初始化BombSDK
        addImage();
    }

    void addImage() {
        imageId.add(R.drawable.apple);
        imageId.add(R.drawable.banana);
        imageId.add(R.drawable.cherry);
        imageId.add(R.drawable.grape);
        imageId.add(R.drawable.mango);
        imageId.add(R.drawable.orange);
        imageId.add(R.drawable.pineapple);
        imageId.add(R.drawable.pear);
        imageId.add(R.drawable.strawberry);
        imageId.add(R.drawable.watermelon);
    }
}
