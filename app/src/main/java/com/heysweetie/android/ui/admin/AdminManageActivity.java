package com.heysweetie.android.ui.admin;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AdminManageActivity extends BaseActivity {
    private MaterialToolbar toolbar;
    private RecyclerView allAdmin_RecycleView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView addNewAdmin_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);
        initControlUnit();
        initView();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        addNewAdmin_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setAdmin(1);
                boolean isAddAdmin = true;
                Intent intent = new Intent(AdminManageActivity.this, AdminManageDetailActivity.class);
                intent.putExtra("user_data", user);
                intent.putExtra("isAddAdmin", isAddAdmin);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        toolbar.setTitle("系统用户管理");
        setSupportActionBar(toolbar);
        allAdmin_RecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refresh();
    }

    private void refresh() {
        List<User> userList = new ArrayList<>();
        BmobQuery<User> query = new BmobQuery<>();//查询所有操作权限不是用户的user
        query.addWhereNotEqualTo("admin", 0).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    userList.addAll(object);
                    allAdmin_RecycleView.setAdapter(new AdminManageAdapter(AdminManageActivity.this, userList));
                } else {
                }
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void initControlUnit() {
        toolbar = findViewById(R.id.toolBar);
        allAdmin_RecycleView = findViewById(R.id.allAdmin_RecycleView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        addNewAdmin_TextView = findViewById(R.id.addNewAdmin_TextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}