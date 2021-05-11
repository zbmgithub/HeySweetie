package com.heysweetie.android.ui.admin.adminmanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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
import com.heysweetie.android.ui.admin.goodsmanage.GoodsManageDetailActivity;
import com.heysweetie.android.ui.common.ActivityCollector;
import com.heysweetie.android.ui.common.BaseActivity;
import com.heysweetie.android.ui.common.HeadShotsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static java.lang.Thread.sleep;

public class AdminManageDetailActivity extends BaseActivity implements View.OnClickListener {
    private static User user;
    private boolean isAddAdmin;

    private MaterialToolbar toolBar;
    private static ImageView headShot_Image;
    private EditText loginAccount;
    private EditText nickName;
    private EditText password;
    private EditText adminPower;
    private Button updateBtn;
    private Button cancelBtn;
    private Button deleteBtn;
    private RecyclerView allHeadShotsRecylcerView;
    private static int userImageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_detail);

        user = (User) getIntent().getSerializableExtra("user_data");
        isAddAdmin = getIntent().getBooleanExtra("isAddAdmin", false);

        initControlUnit();
        initView();
        if (isAddAdmin) {
            deleteBtn.setVisibility(View.GONE);
            loginAccount.setEnabled(true);
        }
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void initControlUnit() {
        toolBar = findViewById(R.id.toolBar);
        headShot_Image = findViewById(R.id.headShot_Image);
        loginAccount = findViewById(R.id.loginAccount);
        nickName = findViewById(R.id.nickName);
        password = findViewById(R.id.password);
        adminPower = findViewById(R.id.adminPower);
        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        allHeadShotsRecylcerView = findViewById(R.id.allHeadShotsRecylcerView);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //自定义标题栏
        toolBar.setTitle("系统用户管理");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(AdminManageDetailActivity.this).load(user.getUserImageId()).into(headShot_Image);
        userImageId = user.getUserImageId();
        loginAccount.setText(user.getUsername());
        nickName.setText(user.getUserNickName());
        adminPower.setText(user.getAdmin() + "");
        allHeadShotsRecylcerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        allHeadShotsRecylcerView.setAdapter(new HeadShotsAdapter(AdminManageDetailActivity.this, HeySweetieApplication.userImageId));
    }

    public static void changeImage(Context context, int imageId) {
        user.setUserImageId(imageId);
        Glide.with(context).load(imageId).into(headShot_Image);
        userImageId = imageId;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.updateBtn) {
            new AlertDialog.Builder(this)
                    .setTitle("核对操作")
                    .setMessage("确认继续你现在的操作吗？")
                    .setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            update();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if (id == R.id.cancelBtn) {
            new AlertDialog.Builder(this)
                    .setTitle("核对操作")
                    .setMessage("确认继续你现在的操作吗？")
                    .setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if (id == R.id.deleteBtn) {
            new AlertDialog.Builder(this)
                    .setTitle("核对操作")
                    .setMessage("确认继续你现在的操作吗？")
                    .setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.setPassword(user.getUserPassword());
                            user.login(new SaveListener<User>() {//需要先登录才能操作
                                @Override
                                public void done(User user, BmobException e) {
                                    if (e == null) {
                                        user.delete(user.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    Toast.makeText(AdminManageDetailActivity.this, "删除成功，请重新登录", Toast.LENGTH_SHORT).show();
                                                    ActivityCollector.finishAll();
                                                    Intent intent = new Intent(AdminManageDetailActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(AdminManageDetailActivity.this, "删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AdminManageDetailActivity.this, "登录失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void update() {
        boolean validPass = false;
        //验证密码是否有效
        if (!password.getText().toString().equals("")) {
            validPass = LoginActivity.isValidPassword(password.getText().toString());
        }
        if (isAddAdmin) {//添加新管理员
            if (!validPass) {
                Toast.makeText(this, "密码无效，重新输入", Toast.LENGTH_SHORT).show();
            } else {
                user.setUserNickName(nickName.getText().toString().equals("") == true ? "默认用户名" : nickName.getText().toString());
                user.setAdmin(Integer.parseInt(adminPower.getText().toString()) == 2 ? 2 : 1);//防止输错权限数字，只要不等于2默认权限是1
                user.setUsername(loginAccount.getText().toString());
                user.setPassword(password.getText().toString());
                user.setUserPassword(password.getText().toString());
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            Toast.makeText(AdminManageDetailActivity.this, "添加成功,请重新登录", Toast.LENGTH_SHORT).show();
                            ActivityCollector.finishAll();
                            Intent intent = new Intent(AdminManageDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AdminManageDetailActivity.this, "添加失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {//修改当前管理员
            if (!validPass && !password.getText().toString().equals("")) {
                Toast.makeText(this, "密码无效，重新输入", Toast.LENGTH_SHORT).show();
            } else {
                user.setPassword(user.getUserPassword());
                boolean finalValidPass = validPass;
                user.login(new SaveListener<User>() {//需要先登录当前用户，才能修改,BmobUser接口设置
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            if (finalValidPass) {
                                user.setPassword(password.getText().toString());
                                user.setUserPassword(password.getText().toString());
                            }
                            user.setUserNickName(nickName.getText().toString().equals("") == true ? "默认用户名" : nickName.getText().toString());
                            user.setAdmin(Integer.parseInt(adminPower.getText().toString()) == 2 ? 2 : 1);//防止输错权限数字，只要不等于2默认权限是1
                            user.setUserImageId(userImageId);
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(AdminManageDetailActivity.this, "更新成功，请重新登录", Toast.LENGTH_SHORT).show();
                                        ActivityCollector.finishAll();
                                        Intent intent = new Intent(AdminManageDetailActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(AdminManageDetailActivity.this, "更新失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new AlertDialog.Builder(this)
                    .setTitle("核对操作")
                    .setMessage("确认继续你现在的操作吗？")
                    .setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
