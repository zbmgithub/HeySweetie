package com.heysweetie.android.ui.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.GoodsAdapter;
import com.heysweetie.android.ui.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.admin.AdminMainActivity;
import com.heysweetie.android.ui.login.LoginActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ClientMainActivity extends AppCompatActivity implements View.OnClickListener {

    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private TextView phoneText;
    private TextView nickName;
    private Button quitBtn;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView goods_recyclerView;
    private MaterialToolbar toolbar;
    private static TextView shopCartTotalPrice;
    private static TextView shopCartTotalCount;

    private TextView goToDeal;
    private LinearLayout shop_cart;
    private RecyclerView shopCartRecyclerView;
    private static MaterialCardView shopCartView;
    private TextView cleanShopCart;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //初始化控件
        initControlUnit();
        //设置显示界面
        initView();

        //设置点击事件
        {
            quitBtn.setOnClickListener(this);//滑动菜单的右上角，有个垃圾箱标志，退出登录功能
            goToDeal.setOnClickListener(this);//去结算点击事件
            shop_cart.setOnClickListener(this);//购物车栏点击事件
            cleanShopCart.setOnClickListener(this);//清空购物车


            //设置滑动菜单的点击事件
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.goods_onSale)
                        drawerLayout.closeDrawers();//默认购物界面为主界面，所以关闭就好
                    else if(item.getItemId() == R.id.shop_cart){
                        Intent intent = new Intent(ClientMainActivity.this, ShopCartActivity.class);
                        intent.putExtra("user_data", user);
                        startActivity(intent);//跳转到用户界面，
                    }
                    else {
                        drawerLayout.closeDrawers();
                        Toast.makeText(ClientMainActivity.this, "跳转到其他界面", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
        //设置下拉刷新显示商品界面
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGoods(goods_recyclerView);
            }
        });
    }

    //初始化控件
    private void initControlUnit() {
        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        phoneText = (TextView) navView.getHeaderView(0).findViewById(R.id.phoneText);//获取navView的header!!!!
        nickName = (TextView) navView.getHeaderView(0).findViewById(R.id.nickNameText);
        quitBtn = (Button) navView.getHeaderView(0).findViewById(R.id.quitBtn);
        toolbar = findViewById(R.id.toolBar);
        goods_recyclerView = findViewById(R.id.goods_recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shopCartTotalPrice = findViewById(R.id.shopCartTotalPrice);
        shopCartTotalCount = findViewById(R.id.shopCartTotalCount);
        goToDeal = findViewById(R.id.goToDeal);
        shop_cart = findViewById(R.id.shop_cart);
        shopCartRecyclerView = findViewById(R.id.shopCartRecyclerView);
        shopCartView = findViewById(R.id.shopCartView);
        cleanShopCart = findViewById(R.id.cleanShopCart);
    }

    //初始化显示界面
    private void initView() {
        //设置自定义标题栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //初始化滑动菜单的header部分，将用户的昵称，手机号添加到header里
        user = (User) getIntent().getSerializableExtra("user_data");
        phoneText.setText(user.getMobilePhoneNumber());
        nickName.setText(user.getUserNickName());
        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);
        //显示所有上架商品
        refreshGoods(goods_recyclerView);//刷新商品界面
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        goods_recyclerView.setLayoutManager(layoutManager);
        //设置下拉刷新栏颜色
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);

    }

    //刷新购物栏
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

    }

    //刷新当前显示的商品
    private void refreshGoods(RecyclerView recyclerView) {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).setSkip(1).order("-createdAt").addWhereNotEqualTo("goodsState", 2).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsAdapter adapter = new GoodsAdapter(ClientMainActivity.this, goodsList);//所有商品添加到适配器
                    goods_recyclerView.setAdapter(adapter);//为recyclerView设置适配器

                    //每次刷新商品时，同时刷新购物车中的商品数据，防止数据显示不统一的bug
                    for (Goods goods : goodsList) {
                        if (HeySweetieApplication.shopCartMap.containsKey(goods)) {
                            //由于重写了equal()和hashcode方法，所以即使goods信息已经改变，也会匹配上
                            int count = HeySweetieApplication.shopCartMap.get(goods);//获取之前的数量
                            HeySweetieApplication.shopCartMap.remove(goods);//删除key
                            HeySweetieApplication.shopCartMap.put(goods, count);//放入新的key
                        }
                    }
                    //结束刷新
                    swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshShopCar();//每次重新获取到显示界面，刷新购物车栏
        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.quitBtn) {//退出登录，滑动菜单里右上角
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (getId == R.id.goToDeal) {//去结算去
            Toast.makeText(ClientMainActivity.this, "结算成功", Toast.LENGTH_SHORT).show();
            HeySweetieApplication.shopCartMap.clear();
        } else if (getId == R.id.shop_cart) {
            if (shopCartView.getVisibility() == View.GONE) {//显示购物车详细信息
                //设置可见性 这样设置显示是为了处理count数据不同步可能产生的bug
                shopCartView.setVisibility(View.VISIBLE);
                //构造购物车详细信息界面
                List<Goods> goodsList = new ArrayList<>();
                Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
                for (Goods key : keys) {
                    if (HeySweetieApplication.shopCartMap.get(key) > 0) {
                        goodsList.add(key);//获取所有数量大于0的商品
                    }
                }
                ShopCartGoodsAdapter adapter = new ShopCartGoodsAdapter(ClientMainActivity.this, goodsList);//所有商品添加到适配器
                shopCartRecyclerView.setAdapter(adapter);//为recyclerView设置适配器
                shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            } else {
                //设置可见性 再次点击购物车栏关闭购物车详细信息
                closeShopCarListView();
            }
        } else if (getId == R.id.cleanShopCart) {
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
        }
        refreshShopCar();
    }

    //关闭购物车详细信息
    public static void closeShopCarListView(){
        shopCartView.setVisibility(View.GONE);
    }

    //打开菜单栏
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}