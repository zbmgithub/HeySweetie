package com.heysweetie.android.ui.admin.goodsmanage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GoodsManageActivity extends BaseActivity {
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
                refreshGoods();
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
        //???????????????
        toolbar.setTitle("????????????");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //??????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //????????????????????????
        refreshGoods();//??????????????????
        allGoods_RecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        swipeRefresh.setColorSchemeResources(R.color.purple_500);
    }

    //???????????????????????????
    private void refreshGoods() {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//?????????????????????????????????
        query.order("-createdAt").findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    goodsList.addAll(object);//?????????????????????????????????
                    allGoods_RecycleView.setAdapter(new GoodsManageAdapter(GoodsManageActivity.this, goodsList));
                    //????????????
                    swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

    }

    @Override
    protected void onResume() {
        refreshGoods();
        super.onResume();
    }

    //????????????home??????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}