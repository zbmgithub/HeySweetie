package com.heysweetie.android.ui.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.ui.GoodsAdapter;
import com.heysweetie.android.ui.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.admin.AdminMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GoodsDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView goodsImage;
    private MaterialToolbar toolbar;
    private TextView goodsProfile;
    private TextView salePrice;
    private Button minusCountBtn;
    private static TextView countText;
    private Button addCountBtn;
    private static Goods goods;
    private static TextView shopCartTotalPrice;
    private TextView goToDeal;
    private RecyclerView goods_recyclerView;
    private static TextView shopCartTotalCount;
    private LinearLayout shor_cart;
    private RecyclerView shopCartRecyclerView;
    private static MaterialCardView shopCartView;
    private TextView cleanShopCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        //获取传递过来的goods对象
        Intent intent = getIntent();
        goods = (Goods) intent.getSerializableExtra("goods_data");
        //初始化控件
        initControlUnit();
        //设置显示界面
        {
            Glide.with(this).load(goods.getImageId()).into(goodsImage);//显示商品图片为传递过来的商品图片
            toolbar.setTitle(goods.getGoodsName());//设置标题栏文字为商品名
            setSupportActionBar(toolbar);//显示标题栏为自定义标题栏
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置home按钮
            goodsProfile.setText(goods.getGoodsProfile());//显示商品简介
            double currenPrice = goods.getPrice() * goods.getSale();//显示当前商品价格
            salePrice.setText("售价：" + currenPrice + "元");
            int count = HeySweetieApplication.shopCartMap.get(goods) == null ? 0 : HeySweetieApplication.shopCartMap.get(goods);
            countText.setText(count + "");//设置当前商品数量为购物车中的数量
            refreshShopCar();//刷新购物车栏
            //在当前商品页面同时显示其他商品的广告图，设置goode_recycler布局方式
            elseGoods(goods_recyclerView);//获取其他商品界面
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);//设置其他商品显示布局
            goods_recyclerView.setLayoutManager(layoutManager);
        }

        //设置按钮监听事件
        {
            minusCountBtn.setOnClickListener(this);
            addCountBtn.setOnClickListener(this);
            goToDeal.setOnClickListener(this);
            shor_cart.setOnClickListener(this);//查看购物车详情
            cleanShopCart.setOnClickListener(this);//清空购物车
        }
    }

    //当前商品界面同时显示其他商品，设置适配器
    private void elseGoods(RecyclerView recyclerView) {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).setSkip(1).order("-createdAt").addWhereNotEqualTo("goodsState", 2).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsAdapter adapter = new GoodsAdapter(GoodsDetailActivity.this, goodsList);//所有商品添加到适配器
                    goods_recyclerView.setAdapter(adapter);//为recyclerView设置适配器
                } else {
                }
            }
        });

    }

    @Override//设置标题栏home按钮功能
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化控件
    private void initControlUnit() {
        goodsImage = findViewById(R.id.goodsImage);
        toolbar = findViewById(R.id.toolBar);
        goodsProfile = findViewById(R.id.profie);
        salePrice = findViewById(R.id.salePrice);
        minusCountBtn = findViewById(R.id.minusCountBtn);
        countText = findViewById(R.id.count);
        addCountBtn = findViewById(R.id.addCountBtn);
        shopCartTotalPrice = findViewById(R.id.shopCartTotalPrice);
        goToDeal = findViewById(R.id.goToDeal);
        goods_recyclerView = findViewById(R.id.goods_recyclerView);
        shopCartTotalCount = findViewById(R.id.shopCartTotalCount);
        shor_cart = findViewById(R.id.shop_cart);
        shopCartRecyclerView = findViewById(R.id.shopCartRecyclerView);
        shopCartView = findViewById(R.id.shopCartView);
        cleanShopCart = findViewById(R.id.cleanShopCart);
    }

    @Override//点击事件处理
    public void onClick(View v) {
        int id = v.getId();
        int count = HeySweetieApplication.shopCartMap.get(goods) == null ? 0 : HeySweetieApplication.shopCartMap.get(goods);

        if (id == R.id.minusCountBtn) {
            if (count > 0) {//减少数量
                count--;
                HeySweetieApplication.shopCartMap.put(goods, count);//将减少后的数量放进购物车
                countText.setText(count + "");//设置数量显示
            }
        } else if (id == R.id.addCountBtn) {//添加数量
            count++;
            HeySweetieApplication.shopCartMap.put(goods, count);
            countText.setText(count + "");
        } else if (id == R.id.goToDeal) {//去结算
            Toast.makeText(GoodsDetailActivity.this, "结算成功", Toast.LENGTH_SHORT).show();

            addCountBtn.setEnabled(true);//这样设置显示是为了处理某些数据不同步可能产生的bug
            minusCountBtn.setEnabled(true);
            countText.setText("0");
            HeySweetieApplication.shopCartMap.clear();
            shopCartView.setVisibility(View.GONE);
        } else if (id == R.id.shop_cart) {//点击购物车
            if (shopCartView.getVisibility() == View.GONE) {//显示购物车详细信息
                //设置可见性 这样设置显示是为了处理count数据不同步可能产生的bug
                addCountBtn.setEnabled(false);
                minusCountBtn.setEnabled(false);
                shopCartView.setVisibility(View.VISIBLE);
                //构造购物车详细信息界面
                List<Goods> goodsList = new ArrayList<>();
                Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
                for (Goods key : keys) {
                    if (HeySweetieApplication.shopCartMap.get(key) > 0) {
                        goodsList.add(key);//获取所有数量大于0的商品
                    }
                }
                ShopCartGoodsAdapter adapter = new ShopCartGoodsAdapter(GoodsDetailActivity.this, goodsList);//所有商品添加到适配器
                shopCartRecyclerView.setAdapter(adapter);//为recyclerView设置适配器
                shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            } else {
                //设置可见性 再次点击购物车栏关闭购物车详细信息
                addCountBtn.setEnabled(true);
                minusCountBtn.setEnabled(true);
                shopCartView.setVisibility(View.GONE);
            }
        } else if (id == R.id.cleanShopCart) {//清空购物车
            addCountBtn.setEnabled(true);
            minusCountBtn.setEnabled(true);
            countText.setText("0");
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
        }
        refreshShopCar();//每次点击后都刷新一次购物车栏，这样它可以实现动态变化
    }

    //刷新购物栏（最下方一行）
    public static void refreshShopCar() {
        double price = 0;//购物车总价
        int count = 0;//购物商品数量
        //获取购物车所有的商品
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int tempCount = HeySweetieApplication.shopCartMap.get(key);
            count += tempCount;
            //当前购物车总价=当前商品价格*折扣*数量+其余商品...
            price += key.getPrice() * key.getSale() * tempCount;
        }
        //设置为两位小数
        double priceOutput = Double.parseDouble(String.format("%.2f", price));
        shopCartTotalPrice.setText(priceOutput + "");
        //设置购物车数量显示
        shopCartTotalCount.setText(count + "");

        int i = HeySweetieApplication.shopCartMap.get(goods) == null ? 0 : HeySweetieApplication.shopCartMap.get(goods);
        countText.setText(i + "");//当前页面的商品数量
    }

    //关闭购物车详细信息
    public static void closeShopCarListView() {
        shopCartView.setVisibility(View.GONE);
    }

}