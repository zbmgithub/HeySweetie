package com.heysweetie.android.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.GoodsAdapter;
import com.heysweetie.android.ui.common.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {

    private User user;
    //滑动菜单组件
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private TextView phoneText;
    private TextView nickName;
    private Button quitBtn;

    //主界面组件
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        //初始化控件
        initControlUnit();
        //初始化显示界面
        initView();
        //初始化点击事件
        initClick();

        //设置下拉刷新显示商品界面
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(goods_recyclerView);
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
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //设置自定义标题栏
        toolbar.setTitle("所有上架商品");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化滑动菜单的header部分，将用户的昵称，手机号添加到header里
        user = (User) getIntent().getSerializableExtra("user_data");
        phoneText.setText(user.getMobilePhoneNumber());
        nickName.setText(user.getUserNickName());

        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);

        //显示当前所有上架商品
        refresh(goods_recyclerView);//刷新商品界面
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        goods_recyclerView.setLayoutManager(layoutManager);

        //设置下拉刷新时，刷新等待圈颜色
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);
    }

    //初始化点击事件
    private void initClick() {
        quitBtn.setOnClickListener(this);//滑动菜单的右上角，有个垃圾箱标志，退出登录功能
        goToDeal.setOnClickListener(this);//去结算点击事件
        shop_cart.setOnClickListener(this);//购物车栏点击事件
        cleanShopCart.setOnClickListener(this);//清空购物车

        //设置滑动菜单的点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.goods_onSale)
                    drawerLayout.closeDrawers();//默认购物界面为主界面，所以关闭就好
                else if (id == R.id.goods_manage) {
                    Intent intent = new Intent(AdminMainActivity.this, GoodsManageActivity.class);
                    if (user.getAdmin() > 0) {
                        startActivity(intent);
                    }
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(AdminMainActivity.this, "跳转到其他界面", Toast.LENGTH_SHORT).show();
                }
                closeShopCarListView();
                return true;
            }
        });
    }

    //刷新当前显示的商品
    private void refresh(RecyclerView recyclerView) {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).order("-createdAt").addWhereNotEqualTo("goodsState", 1).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsAdapter adapter = new GoodsAdapter(AdminMainActivity.this, goodsList, user);//所有商品添加到适配器
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
                    //解决当某个商品已经下架了，仍然有在购物车的bug
                    Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
                    for (Goods key : keys) {//遍历购物车
                        for (Goods goods : goodsList) {//遍历所有未下架商品
                            if (key.equals(goods)) break;
                            if (goods.equals(goodsList.get(goodsList.size() - 1))) {//说明到末尾了
                                if (!key.equals(goods)) {//此时还没找到，说明这个商品下架了，那么从购物车里把它清除掉
                                    HeySweetieApplication.shopCartMap.remove(key);
                                }
                            }
                        }
                    }
                    refreshShopCar();
                    //结束刷新
                    swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

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


    @Override
    protected void onResume() {
        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);
        refresh(goods_recyclerView);//每次重新获取到显示界面，刷新购物车栏
        super.onResume();
        //Toast.makeText(this, "Resume activate", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AdminMainActivity.this, "结算成功", Toast.LENGTH_SHORT).show();
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
            refreshShopCar();
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
                    } else {
                        HeySweetieApplication.shopCartMap.remove(key);//清除所有数量为0的商品
                    }
                }
                ShopCartGoodsAdapter adapter = new ShopCartGoodsAdapter(AdminMainActivity.this, goodsList);//所有商品添加到适配器
                shopCartRecyclerView.setAdapter(adapter);//为recyclerView设置适配器
                shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            } else {
                //设置可见性 再次点击购物车栏关闭购物车详细信息
                closeShopCarListView();
            }
        } else if (getId == R.id.cleanShopCart) {//清空购物车
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
            refreshShopCar();
        }
    }

    //关闭购物车详细信息
    public static void closeShopCarListView() {
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