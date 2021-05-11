package com.heysweetie.android.ui.client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.ActivityCollector;
import com.heysweetie.android.ui.common.BaseActivity;
import com.heysweetie.android.ui.common.HeadShotsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ClientInfoManageActivity extends BaseActivity {
    private static User user;

    private MaterialToolbar toolBar;
    private static ImageView headShot_Image;
    private EditText loginAccount;
    private EditText nickName;
    private EditText newPassword;
    private Button updateBtn;
    private Button cancelBtn;
    private RecyclerView allHeadShotsRecylcerView;
    private static int userImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info_manage);
        user = (User) getIntent().getSerializableExtra("user_data");

        initControlUnit();
        initView();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUserNickName(nickName.getText().toString().equals("") ? "默认用户昵称" : nickName.getText().toString());
                String newPass = newPassword.getText().toString();
                if (!newPass.equals("") && LoginActivity.isValidPassword(newPass)) {
                    user.setPassword(newPass);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(ClientInfoManageActivity.this, "更新成功,请重新登录", Toast.LENGTH_SHORT).show();
                                ActivityCollector.finishAll();
                                Intent intent = new Intent(ClientInfoManageActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ClientInfoManageActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (newPass.equals("")) {
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(ClientInfoManageActivity.this, "更新成功,请重新登录", Toast.LENGTH_SHORT).show();
                                ActivityCollector.finishAll();
                                Intent intent = new Intent(ClientInfoManageActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ClientInfoManageActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (!newPass.equals("") && !LoginActivity.isValidPassword(newPass)) {
                    Toast.makeText(ClientInfoManageActivity.this, "密码无效，重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        toolBar.setTitle("个人信息");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Glide.with(ClientInfoManageActivity.this).load(user.getUserImageId()).into(headShot_Image);
        userImageId = user.getUserImageId();
        loginAccount.setText(user.getUsername());
        nickName.setText(user.getUserNickName());
        allHeadShotsRecylcerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        allHeadShotsRecylcerView.setAdapter(new HeadShotsAdapter(ClientInfoManageActivity.this, HeySweetieApplication.userImageId));

    }

    private void initControlUnit() {
        toolBar = findViewById(R.id.toolBar);
        headShot_Image = findViewById(R.id.headShot_Image);
        loginAccount = findViewById(R.id.loginAccount);
        nickName = findViewById(R.id.nickName);
        newPassword = findViewById(R.id.newPassword);
        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        allHeadShotsRecylcerView = findViewById(R.id.allHeadShotsRecylcerView);
    }

    public static void changeImage(Context context, int imageId) {
        user.setUserImageId(imageId);
        Glide.with(context).load(imageId).into(headShot_Image);
        userImageId = imageId;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}