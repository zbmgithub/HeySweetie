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
    public static List<Integer> userImageId = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Bmob.initialize(this, ApplicationID);//初始化BombSDK
        addImage();
        addUserImage();
    }

    private void addUserImage() {
        userImageId.add(R.drawable.headshot_1);
        userImageId.add(R.drawable.headshot_2);
        userImageId.add(R.drawable.headshot_3);
        userImageId.add(R.drawable.headshot_4);
        userImageId.add(R.drawable.headshot_5);
        userImageId.add(R.drawable.headshot_6);
        userImageId.add(R.drawable.headshot_7);
        userImageId.add(R.drawable.headshot_8);
        userImageId.add(R.drawable.headshot_9);
        userImageId.add(R.drawable.headshot_10);
        userImageId.add(R.drawable.headshot_11);
        userImageId.add(R.drawable.headshot_12);
        userImageId.add(R.drawable.headshot_13);
        userImageId.add(R.drawable.headshot_14);
        userImageId.add(R.drawable.headshot_15);
        userImageId.add(R.drawable.headshot_16);
        userImageId.add(R.drawable.headshot_17);
        userImageId.add(R.drawable.headshot_18);
        userImageId.add(R.drawable.headshot_19);
        userImageId.add(R.drawable.headshot_20);
        userImageId.add(R.drawable.headshot_21);
        userImageId.add(R.drawable.headshot_22);
        userImageId.add(R.drawable.headshot_23);
        userImageId.add(R.drawable.headshot_24);
        userImageId.add(R.drawable.headshot_25);
        userImageId.add(R.drawable.headshot_26);
        userImageId.add(R.drawable.headshot_27);
        userImageId.add(R.drawable.headshot_28);
        userImageId.add(R.drawable.headshot_29);
        userImageId.add(R.drawable.headshot_30);
        userImageId.add(R.drawable.headshot_31);
        userImageId.add(R.drawable.headshot_32);
        userImageId.add(R.drawable.headshot_33);
        userImageId.add(R.drawable.headshot_34);
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
        imageId.add(R.drawable.goods1);
        imageId.add(R.drawable.goods2);
        imageId.add(R.drawable.goods3);
        imageId.add(R.drawable.goods4);
        imageId.add(R.drawable.goods5);
        imageId.add(R.drawable.goods6);
        imageId.add(R.drawable.goods7);
        imageId.add(R.drawable.goods8);
        imageId.add(R.drawable.goods9);
    }
}
