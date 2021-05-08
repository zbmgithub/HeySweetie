package com.heysweetie.android.ui.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Msg;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.MemosActivity;
import com.heysweetie.android.ui.common.MemosAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MsgManageActivity extends AppCompatActivity {
    private User user;
    private MaterialToolbar toolBar;
    private RecyclerView memosManage_recycler;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_manage);
        user = (User) getIntent().getSerializableExtra("user_data");
        initControlUnit();
        initView();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initView() {
        //设置标题栏
        toolBar.setTitle("查看留言");
        setSupportActionBar(toolBar);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        memosManage_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refresh();
    }

    private void refresh() {
        BmobQuery<Msg> query = new BmobQuery<>();
        query.order("-msgDate").findObjects(new FindListener<Msg>() {
            @Override
            public void done(List<Msg> msgs, BmobException e) {
                if (e == null) {
                    if (msgs.size() > 0) {
                        memosManage_recycler.setAdapter(new MsgManageAdapter(MsgManageActivity.this, msgs, user));
                    }
                } else {
                    Toast.makeText(MsgManageActivity.this, "查询Msg失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initControlUnit() {
        toolBar = findViewById(R.id.toolBar);
        memosManage_recycler = findViewById(R.id.memosManage_recycler);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }
}