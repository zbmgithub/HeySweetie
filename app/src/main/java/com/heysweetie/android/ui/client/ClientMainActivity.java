package com.heysweetie.android.ui.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.login.LoginActivity;

import org.w3c.dom.Text;

import cn.bmob.v3.BmobUser;

public class ClientMainActivity extends AppCompatActivity implements View.OnClickListener {

    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private TextView phoneText;
    private TextView nickName;
    private FloatingActionButton navBtn;
    private Button quitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        phoneText = (TextView) navView.getHeaderView(0).findViewById(R.id.phoneText);//获取navView的header!!!!
        nickName = (TextView) navView.getHeaderView(0).findViewById(R.id.nickNameText);
        navBtn = findViewById(R.id.navBtn);
        quitBtn = (Button) navView.getHeaderView(0).findViewById(R.id.quitBtn);

        User user = (User) getIntent().getSerializableExtra("user_data");
        //Log.d("User", user.getUserNickName() + user.getMobilePhoneNumber());
        phoneText.setText(user.getMobilePhoneNumber());
        nickName.setText(user.getUserNickName());


        navView.setCheckedItem(R.id.goods_info);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });


        navBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.navBtn) {
            drawerLayout.openDrawer(GravityCompat.START);
        }else if(getId == R.id.quitBtn){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

}