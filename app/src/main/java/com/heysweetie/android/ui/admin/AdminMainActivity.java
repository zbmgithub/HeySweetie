package com.heysweetie.android.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.GoodsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;


public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {

    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private TextView phoneText;
    private TextView nickName;
    private FloatingActionButton navBtn;
    private Button quitBtn;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView goods_recyclerView;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        //获取控件
        {
            navView = findViewById(R.id.navView);
            drawerLayout = findViewById(R.id.drawerLayout);
            phoneText = (TextView) navView.getHeaderView(0).findViewById(R.id.phoneText);//获取navView的header!!!!
            nickName = (TextView) navView.getHeaderView(0).findViewById(R.id.nickNameText);
            navBtn = (FloatingActionButton) findViewById(R.id.navBtn);
            quitBtn = (Button) navView.getHeaderView(0).findViewById(R.id.quitBtn);
            toolbar = findViewById(R.id.toolBar);
            goods_recyclerView = findViewById(R.id.goods_recyclerView);
            swipeRefresh = findViewById(R.id.swipeRefresh);
        }
        //设置自定义标题栏
        toolbar.setTitle("所有上架商品");
        setSupportActionBar(toolbar);
        //初始化滑动菜单的header部分，将用户的昵称，手机号添加到header里
        User user = (User) getIntent().getSerializableExtra("user_data");
        phoneText.setText(user.getMobilePhoneNumber());
        nickName.setText(user.getUserNickName());

        //滑动菜单默认选中子项
        navView.setCheckedItem(R.id.goods_onSale);
        //设置滑动菜单的点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.goods_onSale)
                    drawerLayout.closeDrawers();//默认购物界面为主界面，所以关闭就好
                else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(AdminMainActivity.this, "跳转到其他界面", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        navBtn.setOnClickListener(this);//悬浮按钮设置监听事件
        quitBtn.setOnClickListener(this);//滑动菜单的右上角，有个垃圾箱标志，退出登录功能


        //设置recycler布局方式
        refreshGoods(goods_recyclerView);//刷新商品界面
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        goods_recyclerView.setLayoutManager(layoutManager);

        //设置下拉刷新显示商品界面
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);//刷新颜色
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGoods(goods_recyclerView);
            }
        });
    }

    private void refreshGoods(RecyclerView recyclerView) {
        //设置recyclerview,浏览商品界面
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).setSkip(1).order("-createdAt").addWhereNotEqualTo("goodsState", 2).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsAdapter adapter = new GoodsAdapter(AdminMainActivity.this, goodsList);//所有商品添加到适配器
                    goods_recyclerView.setAdapter(adapter);//为recyclerView设置适配器
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(AdminMainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });
    }

    private void save() {
        List<BmobObject> goodses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Goods goods1 = new Goods();
            goods1.setGoodsName("苹果汁");
            goods1.setImageId(R.drawable.apple);
            Goods goods2 = new Goods();
            goods2.setGoodsName("梨汁");
            goods2.setImageId(R.drawable.pear);
            Goods goods3 = new Goods();
            goods3.setGoodsName("香蕉牛奶");
            goods3.setImageId(R.drawable.banana);
            Goods goods4 = new Goods();
            goods4.setGoodsName("樱桃汁");
            goods4.setImageId(R.drawable.cherry);
            Goods goods5 = new Goods();
            goods5.setGoodsName("葡萄汁");
            goods5.setImageId(R.drawable.grape);
            Goods goods6 = new Goods();
            goods6.setGoodsName("芒果汁");
            goods6.setImageId(R.drawable.mango);
            Goods goods7 = new Goods();
            goods7.setGoodsName("橙汁");
            goods7.setImageId(R.drawable.orange);
            Goods goods8 = new Goods();
            goods8.setGoodsName("凤梨汁");
            goods8.setImageId(R.drawable.pineapple);
            Goods goods9 = new Goods();
            goods9.setGoodsName("草莓汁");
            goods9.setImageId(R.drawable.strawberry);

            goodses.add(goods1);
            goodses.add(goods2);
            goodses.add(goods3);
            goodses.add(goods4);
            goodses.add(goods5);
            goodses.add(goods6);
            goodses.add(goods7);
            goodses.add(goods8);
            goodses.add(goods9);
        }
        new BmobBatch().insertBatch(goodses).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> results, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < results.size(); i++) {
                        BatchResult result = results.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            Snackbar.make(goods_recyclerView, "第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(goods_recyclerView, "第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                } else {
                    Snackbar.make(goods_recyclerView, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.navBtn) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (getId == R.id.quitBtn) {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

}