package com.heysweetie.android.ui.client;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.BaseActivity;
import com.heysweetie.android.ui.common.ShopCartGoodsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ShopCartActivity extends BaseActivity implements View.OnClickListener {
    private User user;

    private RecyclerView shopCartRecyclerView;
    private TextView cleanShopCart;
    private static TextView shopCartTotalPrice;
    private static TextView shopCartTotalCount;
    private LinearLayout shop_cart;
    private TextView goToDeal;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart);

        user = (User) getIntent().getSerializableExtra("user_data");

        initControlUnit();
        initView();
        refreshShopCar();

        shop_cart.setOnClickListener(this);
        cleanShopCart.setOnClickListener(this);
        goToDeal.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void initControlUnit() {
        shopCartRecyclerView = findViewById(R.id.shopCartRecyclerView);
        cleanShopCart = findViewById(R.id.cleanShopCart);
        shopCartTotalPrice = findViewById(R.id.shopCartTotalPrice);
        shopCartTotalCount = findViewById(R.id.shopCartTotalCount);
        shop_cart = findViewById(R.id.shop_cart);
        cleanShopCart = findViewById(R.id.cleanShopCart);
        goToDeal = findViewById(R.id.goToDeal);
        back = findViewById(R.id.back);
    }

    private void initView() {
        //??????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //?????????????????????????????????
        initShopCart();
    }

    //?????????????????????????????????
    private void initShopCart() {
        List<Goods> goodsList = new ArrayList<>();
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            if (HeySweetieApplication.shopCartMap.get(key) > 0) {
                goodsList.add(key);//????????????????????????0?????????
            } else {
                HeySweetieApplication.shopCartMap.remove(key);//?????????????????????0?????????
            }
        }
        ShopCartGoodsAdapter adapter = new ShopCartGoodsAdapter(ShopCartActivity.this, goodsList);//??????????????????????????????
        shopCartRecyclerView.setAdapter(adapter);//???recyclerView???????????????
        shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    //???????????????
    public static void refreshShopCar() {
        double price = 0;//???????????????
        int count = 0;//??????????????????
        //??????????????????????????????
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int tempCount = HeySweetieApplication.shopCartMap.get(key);
            count += tempCount;
            //?????????????????????=??????????????????*??????*??????+????????????...
            price += key.getPrice() * key.getSale() * tempCount;
        }
        //?????????????????????
        double priceOutput = Double.parseDouble(String.format("%.2f", price));
        shopCartTotalPrice.setText(priceOutput + "");

        //???????????????????????????
        shopCartTotalCount.setText(count + "");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.shop_cart) {
            initShopCart();
        } else if (id == R.id.cleanShopCart) {
            HeySweetieApplication.shopCartMap.clear();
            initShopCart();
            refreshShopCar();
        } else if (id == R.id.goToDeal) {//????????????
            goToDeal();
        } else if (id == R.id.back) {
            finish();
        }
    }

    private void goToDeal() {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setUsername(user.getUsername());
        //??????????????????????????????,???????????????
        List<Goods> goodsList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int count = HeySweetieApplication.shopCartMap.get(key);
            if (count > 0) {//?????????????????????????????????????????????list
                goodsList.add(key);
                countList.add(count);
            }
        }
        if (goodsList.size() > 0) {
            goodsOrder.setGoodsList(goodsList);
            goodsOrder.setCountList(countList);
            goodsOrder.setOrderDate(new Date());
            goodsOrder.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        HeySweetieApplication.shopCartMap.clear();
                        initShopCart();
                        refreshShopCar();
                        Toast.makeText(ShopCartActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShopCartActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}