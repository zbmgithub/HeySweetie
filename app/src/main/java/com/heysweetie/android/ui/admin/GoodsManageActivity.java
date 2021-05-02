package com.heysweetie.android.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GoodsManageActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private static RecyclerView allGoods_RecycleView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView addNewGoods_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manage);

        initControlUnit();
        initView();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGoods(allGoods_RecycleView);
            }
        });
        addNewGoods_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goods goods = new Goods();
                Intent intent = new Intent(GoodsManageActivity.this, GoodsManageDetailActivity.class);
                intent.putExtra("goods_data", goods);
                intent.putExtra("add_new_goods_flag", true);
                startActivity(intent);
            }
        });
    }

    private void initControlUnit() {
        toolbar = findViewById(R.id.toolBar);
        allGoods_RecycleView = findViewById(R.id.allGoods_RecycleView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        addNewGoods_TextView = findViewById(R.id.addNewGoods_TextView);
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

        swipeRefresh.setColorSchemeResources(R.color.purple_500);
    }

    //刷新当前显示的商品
    private void refreshGoods(RecyclerView recyclerView) {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//从数据库中获取所有商品状态不为下架的商品
        query.setLimit(500).order("-createdAt").findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//将获取的商品添加到列表
                    GoodsManageAdapter adapter = new GoodsManageAdapter(GoodsManageActivity.this, goodsList);//所有商品添加到适配器
                    allGoods_RecycleView.setAdapter(adapter);//为recyclerView设置适配器
                    Toast.makeText(GoodsManageActivity.this, "刷新中", Toast.LENGTH_SHORT).show();
                    //结束刷新
                    swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

    }

    @Override
    protected void onResume() {
        Toast.makeText(this, "Resume Activate", Toast.LENGTH_SHORT).show();
        refreshGoods(allGoods_RecycleView);
        super.onResume();
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