package com.heysweetie.android.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.ui.GoodsAdapter;
import com.heysweetie.android.ui.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.client.ShopCartActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GoodsManageActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView allGoods_RecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manage);
        initControlUnit();
        initView();
    }

    private void initControlUnit() {
        toolbar = findViewById(R.id.toolBar);
        allGoods_RecycleView = findViewById(R.id.allGoods_RecycleView);
    }

    private void initView() {
        //设置标题栏
        toolbar.setTitle("商品管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //显示当前所有上架商品
        refreshGoods(allGoods_RecycleView);//刷新商品界面
        allGoods_RecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    //刷新当前显示的商品
    private void refreshGoods(RecyclerView recyclerView) {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).setSkip(1).order("-createdAt").findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsManageAdapter adapter = new GoodsManageAdapter(GoodsManageActivity.this, goodsList);//所有商品添加到适配器
                    allGoods_RecycleView.setAdapter(adapter);//为recyclerView设置适配器
                    //结束刷新
                    //swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

    }

    //标题栏的home按键
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}