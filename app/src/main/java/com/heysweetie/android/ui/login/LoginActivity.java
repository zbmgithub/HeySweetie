package com.heysweetie.android.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.admin.main.AdminMainActivity;
import com.heysweetie.android.ui.client.ClientMainActivity;
import com.heysweetie.android.ui.common.BaseActivity;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private Button registerBtn;
    private CheckBox rememberPass;
    private LinearLayout registerPassLt;
    private LinearLayout registerVtCodeLt;
    private LinearLayout registerBtnLt;
    private LinearLayout loginBtnLt;
    private TextView forgetPass;

    private EditText passwordEdit2;
    private EditText vtCodeEdit;
    private Button vtCodeBtn;
    private Button okBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化控件
        initControlUnit();
        //初始化界面
        initView();
        //设置按钮监听
        initClick();

    }

    private void initControlUnit() {
        accountEdit = findViewById(R.id.accountEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        rememberPass = findViewById(R.id.rememberPassCheckBox);
        forgetPass = findViewById(R.id.forgetPass);
        //注册时使用
        registerPassLt = findViewById(R.id.registerPassLayout);
        registerVtCodeLt = findViewById(R.id.registerVtCodeLayout);
        registerBtnLt = findViewById(R.id.registerBtnLayout);
        loginBtnLt = findViewById(R.id.loginBtnLayout);
        passwordEdit2 = findViewById(R.id.passwordEdit2);
        vtCodeEdit = findViewById(R.id.vtCodeEdit);
        vtCodeBtn = findViewById(R.id.vtCodeBtn);
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //初始化登录账号和密码框
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("isRemember", false);
        if (isRemember) {
            accountEdit.setText(sharedPreferences.getString("account", ""));
            passwordEdit.setText(sharedPreferences.getString("password", ""));
            rememberPass.setChecked(true);
        }
    }

    private void initClick() {
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        vtCodeBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        rememberPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePass();//保存密码，根据是否勾选动态保存密码
            }
        });
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.loginBtn) {//登录
            login(v);
            savePass();//保存密码，根据是否勾选动态保存密码
        } else if (getId == R.id.registerBtn) {//注册
            register();
        } else if (getId == R.id.vtCodeBtn) {//发送验证码
            sendVtCode();
        } else if (getId == R.id.okBtn) {//验证验证码并注册
            ok(v);
        } else if (getId == R.id.cancelBtn) {//返回登陆界面
            //调整界面
            cancel();
        } else if (getId == R.id.forgetPass) {//忘记密码界面
            Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
            startActivity(intent);
        }
    }

    private void cancel() {
        //调整界面
        registerPassLt.setVisibility(View.GONE);
        registerVtCodeLt.setVisibility(View.GONE);
        registerBtnLt.setVisibility(View.GONE);
        loginBtnLt.setVisibility(View.VISIBLE);
        forgetPass.setVisibility(View.VISIBLE);

        accountEdit.setEnabled(true);
        passwordEdit.setEnabled(true);
        passwordEdit2.setEnabled(true);
        okBtn.setEnabled(false);

    }

    private void ok(View v) {
        String phone = accountEdit.getText().toString();//获取输入的手机号
        String code = vtCodeEdit.getText().toString();//获取用户输入的验证码
        String password = passwordEdit.getText().toString();//获取用户输入的密码
        sign_up(phone, code, password);//进行注册
        cancel();
    }

    // 注册
    private void sign_up(String phone, String code, String password) {
        final User user = new User();//创建一个新的User对象
        user.setMobilePhoneNumber(phone);//设置手机号码（必填）
        user.setUsername(phone);//设置用户名（登录账号）默认为手机号码
        user.setPassword(password);//设置用户密码
        user.setUserPassword(password);
        user.setUserNickName("HeySweetie新用户");//默认的名字
        user.setUserImageId(R.drawable.headshot_1);//默认头像
        user.setAdmin(0);//该界面注册的默认为新用户

        user.signOrLogin(code, new SaveListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "短信注册成功：" + user.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                    intent.putExtra("user_data", user);
                    startActivity(intent);
                    finish();//跳转到用户界面，结束登录界面
                } else {
                    Toast.makeText(LoginActivity.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVtCode() {
        String phoneNumber = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (!isValidPhoneNumber(phoneNumber))
            Toast.makeText(this, "无效的手机号", Toast.LENGTH_SHORT).show();
        else if (!password.equals(passwordEdit2.getText().toString()))
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
        else if (!isValidPassword(password))
            Toast.makeText(this, "密码不规范，长度在6-20位，仅包含数字、字母、以及部分特殊字符，请重新输入", Toast.LENGTH_SHORT).show();
        else {
            okBtn.setEnabled(true);
            vtCodeBtn.setEnabled(false);
            accountEdit.setEnabled(false);
            passwordEdit.setEnabled(false);
            passwordEdit2.setEnabled(false);

            //发送验证码
            BmobSMS.requestSMSCode(phoneNumber, "", new QueryListener<Integer>() {
                @Override
                public void done(Integer smsId, BmobException e) {
                    if (e == null) {
                        Toast.makeText(LoginActivity.this, "发送验证码成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //验证码倒计时功能
            long t = 60 * 1000; //定义总时长 60s
            CountDownTimer countDownTimer = new CountDownTimer(t, 300) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long second = millisUntilFinished / 1000 % 60;
                    vtCodeBtn.setText("倒计时" + second + " 秒");
                }

                @Override
                public void onFinish() {
                    vtCodeBtn.setText("发送验证码");
                    vtCodeBtn.setEnabled(true);
                    accountEdit.setEnabled(true);
                    passwordEdit.setEnabled(true);
                    passwordEdit2.setEnabled(true);
                    okBtn.setEnabled(false);
                }
            }.start();
        }
    }

    public static boolean isValidPhoneNumber(String str) {
        //根据运营商发布信息，利用正则判断手机号是否有效
        return Pattern.matches(
                "^1(3[0-9]|5[0-3,5-9]|7[1-3,5-8]|8[0-9])\\d{8}$",
                str
        );
    }

    public static boolean isValidPassword(String str) {
        //密码的正则规则 6到20位  包含数字 字母 特殊字符!@.,()
        return str.matches("^[\\w!@#$%^&*`~()-+=]{6,20}$");
    }

    private void register() {
        //调整界面
        registerPassLt.setVisibility(View.VISIBLE);
        registerVtCodeLt.setVisibility(View.VISIBLE);
        registerBtnLt.setVisibility(View.VISIBLE);
        loginBtnLt.setVisibility(View.GONE);
        forgetPass.setVisibility(View.GONE);
    }

    private void login(View v) { //登录功能
        loginBtn.setEnabled(false);//调整登录按钮
        progressBar.setVisibility(View.VISIBLE);

        final User user = new User();
        user.setUsername(accountEdit.getText().toString());
        user.setPassword(passwordEdit.getText().toString());
        user.login(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if (e == null) {
                    User user = bmobUser;
                    Intent intent;
                    if (user.getAdmin() == 0) {
                        intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                        intent.putExtra("user_data", user);
                        startActivity(intent);//跳转到用户界面，
                    } else if (user.getAdmin() == 1 || user.getAdmin() == 2) {
                        intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                        intent.putExtra("user_data", user);
                        startActivity(intent);//跳转到管理员界面
                    }
                    loginBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    finish();//结束登录界面
                } else {
                    loginBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(v, "登录失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void savePass() {
        //实现记住密码功能-part2-如果勾选记住，存储数据到本地
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        if (rememberPass.isChecked()) {
            editor.putString("account", accountEdit.getText().toString());
            editor.putString("password", passwordEdit.getText().toString());
            editor.putBoolean("isRemember", true);
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }
    }

}