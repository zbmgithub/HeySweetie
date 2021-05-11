package com.heysweetie.android.ui.admin.main;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.admin.adminmanage.AdminManageActivity;
import com.heysweetie.android.ui.admin.goodsmanage.GoodsManageActivity;
import com.heysweetie.android.ui.admin.msgmanage.MsgManageActivity;
import com.heysweetie.android.ui.admin.ordermanage.OrderManageActivity;
import com.heysweetie.android.ui.admin.statistic.StatisticActivity;
import com.heysweetie.android.ui.common.BaseActivity;
import com.heysweetie.android.ui.common.GoodsAdapter;
import com.heysweetie.android.ui.common.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

//管理员界面的主页面，为所有上架商品的浏览界面+滑动菜单
public class AdminMainActivity extends BaseActivity implements View.OnClickListener {

    private User user;
    //滑动菜单组件
    private NavigationView navView;//滑动菜单布局，用来寻找滑动菜单上的控件
    private DrawerLayout drawerLayout;//滑动菜单父布局，这个属性用来展示滑动菜单或者关闭
    private TextView phoneText;//是用户的话这里显示用户手机号，管理员则显示权限，由于共用的一个navHeader布局
    private TextView nickName;//管理员昵称
    private Button quitBtn;//退出登录
    private ImageView headShot_Image;//显示头像

    //主界面组件
    private SwipeRefreshLayout swipeRefresh;//下拉刷新
    private RecyclerView goods_recyclerView;//显示商品
    private MaterialToolbar toolbar;//自定义标题栏
    private static TextView shopCartTotalPrice;//购物车栏显示商品总金额
    private static TextView shopCartTotalCount;//购物车栏显示商品总数量
    private TextView goToDeal;//去结算
    private LinearLayout shop_cart;//购物车栏
    private RecyclerView shopCartRecyclerView;//购物车详细商品信息
    private static MaterialCardView shopCartView;
    private TextView cleanShopCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        user = (User) getIntent().getSerializableExtra("user_data");
        //初始化控件
        initControlUnit();
        //初始化显示界面
        initView();
        //初始化点击事件
        initClick();
    }

    //初始化控件
    private void initControlUnit() {
        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        phoneText = (TextView) navView.getHeaderView(0).findViewById(R.id.phoneText);//获取navView的header!!!!
        nickName = (TextView) navView.getHeaderView(0).findViewById(R.id.nickNameText);
        quitBtn = (Button) navView.getHeaderView(0).findViewById(R.id.quitBtn);
        headShot_Image = navView.getHeaderView(0).findViewById(R.id.headShot_Image);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//标题栏Home键

        //初始化滑动菜单的header部分
        Glide.with(navView).load(user.getUserImageId()).into(headShot_Image);
        phoneText.setText(user.getAdmin() == 1 ? "普通管理员" : "超级管理员");
        nickName.setText(user.getUserNickName());
        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);

        //显示当前所有上架商品
        goods_recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        refresh();//刷新商品界面

        //设置下拉刷新时，刷新等待圈颜色
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);
        //购物车详细信息布局
        shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    //初始化点击事件
    private void initClick() {
        quitBtn.setOnClickListener(this);//滑动菜单的右上角，有个垃圾箱标志，退出登录功能
        goToDeal.setOnClickListener(this);//去结算点击事件
        shop_cart.setOnClickListener(this);//购物车栏点击事件
        cleanShopCart.setOnClickListener(this);//清空购物车
        //设置下拉刷新显示商品界面
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        //设置滑动菜单的点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.goods_onSale)
                    drawerLayout.closeDrawers();//默认购物界面为主界面，所以关闭滑动菜单就好
                else if (id == R.id.goods_manage) {//商品管理
                    Intent intent = new Intent(AdminMainActivity.this, GoodsManageActivity.class);
                    if (user.getAdmin() > 1) {//超级管理员才可以打开
                        startActivity(intent);
                    }
                } else if (id == R.id.order_manage) {//订单管理
                    Intent intent = new Intent(AdminMainActivity.this, OrderManageActivity.class);
                    if (user.getAdmin() > 0) {
                        startActivity(intent);
                    }
                } else if (id == R.id.admin_manage) {//管理员管理
                    Intent intent = new Intent(AdminMainActivity.this, AdminManageActivity.class);
                    if (user.getAdmin() > 1) {//超级管理员才可以打开
                        startActivity(intent);
                    }
                } else if (id == R.id.msg_manage) {//留言管理
                    Intent intent = new Intent(AdminMainActivity.this, MsgManageActivity.class);
                    if (user.getAdmin() > 0) {
                        intent.putExtra("user_data", user);
                        startActivity(intent);
                    }
                } else if (id == R.id.statistic) {//统计数据
                    Intent intent = new Intent(AdminMainActivity.this, StatisticActivity.class);
                    if (user.getAdmin() > 1) {
                        startActivity(intent);
                    }
                } else {
                    drawerLayout.closeDrawers();
                }
                closeShopCarListView();//每次跳转到其他界面都关闭购物车的详细信息
                return true;
            }
        });
    }

    //刷新当前显示的商品
    private void refresh() {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        //根据创建时间排序，且商品状态不能为1，即下架
        query.order("-createdAt").addWhereNotEqualTo("goodsState", 1).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    goods_recyclerView.setAdapter(new GoodsAdapter(AdminMainActivity.this, goodsList, user));
                    //所有商品添加到适配器，为recyclerView设置适配器

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
                    refreshShopCar();//刷新购物车栏
                    //结束刷新进度条
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
        //设置购物车总金额 double为两位小数
        shopCartTotalPrice.setText(Double.parseDouble(String.format("%.2f", price)) + "");
        //设置购物车数量显示
        shopCartTotalCount.setText(count + "");
    }

    @Override
    protected void onResume() {
        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);
        refresh();//每次重新获取到显示界面，刷新主界面
        super.onResume();
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
            goToDeal();
        } else if (getId == R.id.shop_cart) {
            if (shopCartView.getVisibility() == View.GONE) {//显示购物车详细信息
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
                //添加商品到shopCartRecyclerView
                shopCartRecyclerView.setAdapter(new ShopCartGoodsAdapter(AdminMainActivity.this, goodsList));
            } else {
                //再次点击购物车栏关闭购物车详细信息
                closeShopCarListView();
            }
        } else if (getId == R.id.cleanShopCart) {//清空购物车
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
            refreshShopCar();
        }
    }

    private void goToDeal() {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setUsername(user.getUsername());
        //获取购物车所有的商品,添加到订单
        List<Goods> goodsList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int count = HeySweetieApplication.shopCartMap.get(key);
            if (count > 0) {//将购物车中数量大于0商品的添加list
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
                        refreshShopCar();
                        closeShopCarListView();
                        Toast.makeText(AdminMainActivity.this, "结算成功，在查看订单中查询进度", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminMainActivity.this, "结算失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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