package com.heysweetie.android.ui.admin.ordermanage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OrderManageActivity extends BaseActivity implements View.OnClickListener {
    private List<GoodsOrder> goodsOrderList;
    private RecyclerView goodsOrder_RecycleView;
    private SwipeRefreshLayout swipeRefresh;
    private Button find_Btn;
    private EditText userPhoneNum_Edit;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manage);
        initControUnit();
        initView();
        initClick();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrder();
            }
        });
    }

    private void initControUnit() {
        goodsOrder_RecycleView = findViewById(R.id.goodsOrder_RecycleView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        find_Btn = findViewById(R.id.find_Btn);
        userPhoneNum_Edit = findViewById(R.id.userPhoneNum_Edit);
        toolbar = findViewById(R.id.toolbar);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //标题栏设置
        toolbar.setTitle("订单管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goodsOrder_RecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getOrder();
    }

    private void initClick() {
        find_Btn.setOnClickListener(this);
    }

    //获取两天内的订单数据
    private void getOrder() {
        goodsOrderList = new ArrayList<>();
        BmobQuery<GoodsOrder> query = new BmobQuery<>();
        //获取当前时间两天前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date date = calendar.getTime();
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));

        query.order("-createdAt").findObjects(new FindListener<GoodsOrder>() {
            @Override
            public void done(List<GoodsOrder> object, BmobException e) {
                if (e == null) {
                    goodsOrderList.addAll(object);
                    OrderManageAdapter adapter = new OrderManageAdapter(OrderManageActivity.this, goodsOrderList);
                    goodsOrder_RecycleView.setAdapter(adapter);
                } else {

                }
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_Text) {
            finish();
        } else if (id == R.id.find_Btn) {
            getUserOrder(userPhoneNum_Edit.getText().toString());//获取该手机号的订单信息
        }
    }

    private void getUserOrder(String phoneNum) {
        goodsOrderList = new ArrayList<>();
        BmobQuery<GoodsOrder> query = new BmobQuery<>();
        query.order("-createdAt").addWhereEqualTo("username", phoneNum).findObjects(new FindListener<GoodsOrder>() {
            @Override
            public void done(List<GoodsOrder> object, BmobException e) {
                if (e == null) {
                    goodsOrderList.addAll(object);
                    OrderManageAdapter adapter = new OrderManageAdapter(OrderManageActivity.this, goodsOrderList);
                    goodsOrder_RecycleView.setAdapter(adapter);
                } else {
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}