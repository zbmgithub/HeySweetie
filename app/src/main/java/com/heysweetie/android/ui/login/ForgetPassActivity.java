package com.heysweetie.android.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.heysweetie.android.ui.login.LoginActivity.isValidPassword;

public class ForgetPassActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText vtCodeEdit;
    private Button vtCodeBtn;
    private Button okBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        initControlUnit();
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        vtCodeBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void initControlUnit() {
        accountEdit = findViewById(R.id.accountEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        vtCodeEdit = findViewById(R.id.vtCodeEdit);
        vtCodeBtn = findViewById(R.id.vtCodeBtn);
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vtCodeBtn) {
            String phoneNumber = accountEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if (!LoginActivity.isValidPhoneNumber(phoneNumber))
                Toast.makeText(this, "无效的手机号", Toast.LENGTH_SHORT).show();
            else if (!isValidPassword(password))
                Toast.makeText(this, "密码不规范，长度在6-20位，仅包含数字、字母、以及部分特殊字符，请重新输入", Toast.LENGTH_SHORT).show();
            else {
                okBtn.setEnabled(true);
                vtCodeBtn.setEnabled(false);
                accountEdit.setEnabled(false);
                passwordEdit.setEnabled(false);
                //发送验证码
                BmobSMS.requestSMSCode(phoneNumber, "", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer smsId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(ForgetPassActivity.this, "发送验证码成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgetPassActivity.this, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "请稍后再试", Toast.LENGTH_SHORT).show();
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
                        okBtn.setEnabled(false);
                    }
                }.start();
            }
        } else if (v.getId() == R.id.okBtn) {
            accountEdit.setEnabled(true);
            passwordEdit.setEnabled(true);
            okBtn.setEnabled(false);

            BmobUser.loginBySMSCode(accountEdit.getText().toString(), vtCodeEdit.getText().toString(), new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        user.setPassword(passwordEdit.getText().toString());
                        user.setUserPassword(passwordEdit.getText().toString());
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(ForgetPassActivity.this, "重置密码成功，请重新登录", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgetPassActivity.this, "重置密码失败", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(ForgetPassActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (v.getId() == R.id.cancelBtn) {
            finish();
        }
    }
}