package com.heysweetie.android.ui.client;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ClientGoodsOrderActivity extends BaseActivity {
    private User user;
    private List<GoodsOrder> goodsOrderList;
    private GoodsOrder orderTest;

    private RecyclerView goodsOrder_RecycleView;
    private TextView back_Text;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_goods_order);

        user = (User) getIntent().getSerializableExtra("user_data");
        //addOrder();
        getOrder();

        initControlUnit();
        initView();

        back_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrder();
            }
        });
    }

    private void initControlUnit() {
        goodsOrder_RecycleView = findViewById(R.id.goodsOrder_RecycleView);
        back_Text = findViewById(R.id.back_Text);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        goodsOrder_RecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void addOrder() {
        List<Goods> goodsList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            if (HeySweetieApplication.shopCartMap.get(key) > 0) {
                goodsList.add(key);//获取所有数量大于0的商品
                countList.add(HeySweetieApplication.shopCartMap.get(key));
            } else {
                HeySweetieApplication.shopCartMap.remove(key);//清除所有数量为0的商品
            }
        }
        orderTest = new GoodsOrder();
        orderTest.setOrderState(0);
        orderTest.setUsername("18782417068");
        orderTest.setOrderDate(new Date());
        orderTest.setGoodsList(goodsList);
        orderTest.setCountList(countList);
        orderTest.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(ClientGoodsOrderActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        });
    }

    private void getOrder() {
        goodsOrderList = new ArrayList<>();
        BmobQuery<GoodsOrder> query = new BmobQuery<>();//从数据库中获取所有当前User的订单
        query.setLimit(500).order("-createdAt").addWhereEqualTo("username", user.getUsername()).findObjects(new FindListener<GoodsOrder>() {
            @Override
            public void done(List<GoodsOrder> object, BmobException e) {
                if (e == null) {
                    goodsOrderList.addAll(object);
                    ClientGoodsOrderAdapter adapter = new ClientGoodsOrderAdapter(ClientGoodsOrderActivity.this, goodsOrderList);
                    goodsOrder_RecycleView.setAdapter(adapter);
                } else {

                }
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}