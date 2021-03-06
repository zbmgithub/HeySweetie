package com.heysweetie.android.ui.admin.main;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.admin.adminmanage.AdminManageActivity;
import com.heysweetie.android.ui.admin.goodsmanage.GoodsManageActivity;
import com.heysweetie.android.ui.admin.msgmanage.MsgManageActivity;
import com.heysweetie.android.ui.admin.ordermanage.OrderManageActivity;
import com.heysweetie.android.ui.admin.statistic.StatisticActivity;
import com.heysweetie.android.ui.common.BaseActivity;
import com.heysweetie.android.ui.common.GoodsAdapter;
import com.heysweetie.android.ui.common.ShopCartGoodsAdapter;
import com.heysweetie.android.ui.login.LoginActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.heysweetie.android.HeySweetieApplication.context;

//??????????????????????????????????????????????????????????????????+????????????
public class AdminMainActivity extends BaseActivity implements View.OnClickListener {

    private User user;
    //??????????????????
    private NavigationView navView;//?????????????????????????????????????????????????????????
    private DrawerLayout drawerLayout;//????????????????????????????????????????????????????????????????????????
    private TextView phoneText;//?????????????????????????????????????????????????????????????????????????????????????????????navHeader??????
    private TextView nickName;//???????????????
    private Button quitBtn;//????????????
    private ImageView headShot_Image;//????????????

    //???????????????
    private SwipeRefreshLayout swipeRefresh;//????????????
    private RecyclerView goods_recyclerView;//????????????
    private MaterialToolbar toolbar;//??????????????????
    private static TextView shopCartTotalPrice;//?????????????????????????????????
    private static TextView shopCartTotalCount;//?????????????????????????????????
    private TextView goToDeal;//?????????
    private LinearLayout shop_cart;//????????????
    private RecyclerView shopCartRecyclerView;//???????????????????????????
    private static MaterialCardView shopCartView;
    private TextView cleanShopCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        user = (User) getIntent().getSerializableExtra("user_data");
        //???????????????
        initControlUnit();
        //?????????????????????
        initView();
        //?????????????????????
        initClick();

    }

    //???????????????
    private void initControlUnit() {
        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        phoneText = (TextView) navView.getHeaderView(0).findViewById(R.id.phoneText);//??????navView???header!!!!
        nickName = (TextView) navView.getHeaderView(0).findViewById(R.id.nickNameText);
        quitBtn = (Button) navView.getHeaderView(0).findViewById(R.id.quitBtn);
        headShot_Image = navView.getHeaderView(0).findViewById(R.id.headShot_Image);
        toolbar = findViewById(R.id.toolBar);
        goods_recyclerView = findViewById(R.id.goods_recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shopCartTotalPrice = findViewById(R.id.shopCartTotalPrice);
        shopCartTotalCount = findViewById(R.id.shopCartTotalCount);
        goToDeal = findViewById(R.id.goToDeal);
        shop_cart = findViewById(R.id.shop_cart);
        shopCartRecyclerView = findViewById(R.id.shopCartRecyclerView);
        shopCartView = findViewById(R.id.shopCartView);
        cleanShopCart = findViewById(R.id.cleanShopCart);
    }

    //?????????????????????
    private void initView() {
        //??????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //????????????????????????
        toolbar.setTitle("??????????????????");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//?????????Home???

        //????????????????????????header??????
        Glide.with(navView).load(user.getUserImageId()).into(headShot_Image);
        phoneText.setText(user.getAdmin() == 1 ? "???????????????" : "???????????????");
        nickName.setText(user.getUserNickName());
        //??????????????????????????????
        navView.setCheckedItem(R.id.goods_onSale);

        //??????????????????????????????
        goods_recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        refresh();//??????????????????

        //?????????????????????????????????????????????
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);
        //???????????????????????????
        shopCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    //?????????????????????
    private void initClick() {
        quitBtn.setOnClickListener(this);//?????????????????????????????????????????????????????????????????????
        goToDeal.setOnClickListener(this);//?????????????????????
        shop_cart.setOnClickListener(this);//????????????????????????
        cleanShopCart.setOnClickListener(this);//???????????????
        //????????????????????????????????????
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        //?????????????????????????????????
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.goods_onSale)
                    drawerLayout.closeDrawers();//???????????????????????????????????????????????????????????????
                else if (id == R.id.goods_manage) {//????????????
                    Intent intent = new Intent(AdminMainActivity.this, GoodsManageActivity.class);
                    if (user.getAdmin() > 1) {//??????????????????????????????
                        startActivity(intent);
                    }
                } else if (id == R.id.order_manage) {//????????????
                    Intent intent = new Intent(AdminMainActivity.this, OrderManageActivity.class);
                    if (user.getAdmin() > 0) {
                        startActivity(intent);
                    }
                } else if (id == R.id.admin_manage) {//???????????????
                    Intent intent = new Intent(AdminMainActivity.this, AdminManageActivity.class);
                    if (user.getAdmin() > 1) {//??????????????????????????????
                        startActivity(intent);
                    }
                } else if (id == R.id.msg_manage) {//????????????
                    Intent intent = new Intent(AdminMainActivity.this, MsgManageActivity.class);
                    if (user.getAdmin() > 0) {
                        intent.putExtra("user_data", user);
                        startActivity(intent);
                    }
                } else if (id == R.id.statistic) {//????????????
                    Intent intent = new Intent(AdminMainActivity.this, StatisticActivity.class);
                    if (user.getAdmin() > 1) {
                        startActivity(intent);
                    }
                } else {
                    drawerLayout.closeDrawers();
                }
                closeShopCarListView();//????????????????????????????????????????????????????????????
                return true;
            }
        });
    }

    //???????????????????????????
    private void refresh() {
        List<Goods> goodsList = new ArrayList<>();
        BmobQuery<Goods> query = new BmobQuery<>();//????????????????????????????????????????????????????????????
        //???????????????????????????????????????????????????1????????????
        query.order("-createdAt").addWhereNotEqualTo("goodsState", 1).findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    //??????????????????
                    SharedPreferences preferences = context.getSharedPreferences("imageUpdateTime", MODE_PRIVATE);
                    for (Goods goods : object) {
                        BmobFile goodsImage = goods.getGoodsImage();
                        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg");
                        if (goodsImage != null)
                            if (!file.exists() || !preferences.getString(goods.getObjectId(), "").equals(goods.getUpdatedAt())) {
                                goodsImage.download(file, new DownloadFileListener() {
                                    @Override
                                    public void done(String savePath, BmobException e) {
                                        if (e == null) {
                                        } else {
                                            Toast.makeText(AdminMainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onProgress(Integer value, long total) {

                                    }
                                });
                            }
                        preferences.edit().putString(goods.getObjectId(), goods.getUpdatedAt()).apply();
                    }


                    goodsList.addAll(object);//?????????????????????????????????
                    goods_recyclerView.setAdapter(new GoodsAdapter(AdminMainActivity.this, goodsList, user));
                    //????????????????????????????????????recyclerView???????????????

                    //????????????????????????????????????????????????????????????????????????????????????????????????bug
                    for (Goods goods : goodsList) {
                        if (HeySweetieApplication.shopCartMap.containsKey(goods)) {
                            //???????????????equal()???hashcode?????????????????????goods????????????????????????????????????
                            int count = HeySweetieApplication.shopCartMap.get(goods);//?????????????????????
                            HeySweetieApplication.shopCartMap.remove(goods);//??????key
                            HeySweetieApplication.shopCartMap.put(goods, count);//????????????key
                        }
                    }
                    //???????????????????????????????????????????????????????????????bug
                    Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
                    for (Goods key : keys) {//???????????????
                        for (Goods goods : goodsList) {//???????????????????????????
                            if (key.equals(goods)) break;
                            if (goods.equals(goodsList.get(goodsList.size() - 1))) {//??????????????????
                                if (!key.equals(goods)) {//???????????????????????????????????????????????????????????????????????????????????????
                                    HeySweetieApplication.shopCartMap.remove(key);
                                }
                            }
                        }
                    }
                    refreshShopCar();//??????????????????
                    //?????????????????????
                    swipeRefresh.setRefreshing(false);
                } else {
                }
            }
        });

    }

    //???????????????
    public static void refreshShopCar() {
        double price = 0;//???????????????
        int count = 0;//??????????????????
        //??????????????????????????????
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int tempCount = HeySweetieApplication.shopCartMap.get(key);
            count += tempCount;
            //?????????????????????=??????????????????*??????*??????+????????????...
            price += key.getPrice() * key.getSale() * tempCount;
        }
        //???????????????????????? double???????????????
        shopCartTotalPrice.setText(Double.parseDouble(String.format("%.2f", price)) + "");
        //???????????????????????????
        shopCartTotalCount.setText(count + "");
    }

    @Override
    protected void onResume() {
        //??????????????????????????????
        navView.setCheckedItem(R.id.goods_onSale);
        refresh();//???????????????????????????????????????????????????
        super.onResume();
    }


    //??????????????????
    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.quitBtn) {//???????????????????????????????????????
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (getId == R.id.goToDeal) {//????????????
            goToDeal();
        } else if (getId == R.id.shop_cart) {
            if (shopCartView.getVisibility() == View.GONE) {//???????????????????????????
                shopCartView.setVisibility(View.VISIBLE);
                //?????????????????????????????????
                List<Goods> goodsList = new ArrayList<>();
                Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
                for (Goods key : keys) {
                    if (HeySweetieApplication.shopCartMap.get(key) > 0) {
                        goodsList.add(key);//????????????????????????0?????????
                    } else {
                        HeySweetieApplication.shopCartMap.remove(key);//?????????????????????0?????????
                    }
                }
                //???????????????shopCartRecyclerView
                shopCartRecyclerView.setAdapter(new ShopCartGoodsAdapter(AdminMainActivity.this, goodsList));
            } else {
                //???????????????????????????????????????????????????
                closeShopCarListView();
            }
        } else if (getId == R.id.cleanShopCart) {//???????????????
            HeySweetieApplication.shopCartMap.clear();
            closeShopCarListView();
            refreshShopCar();
        }
    }

    private void goToDeal() {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setUsername(user.getUsername());
        //??????????????????????????????,???????????????
        List<Goods> goodsList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        Set<Goods> keys = HeySweetieApplication.shopCartMap.keySet();
        for (Goods key : keys) {
            int count = HeySweetieApplication.shopCartMap.get(key);
            if (count > 0) {//???????????????????????????0???????????????list
                goodsList.add(key);
                countList.add(count);
            }
        }
        if (goodsList.size() > 0) {
            goodsOrder.setGoodsList(goodsList);
            goodsOrder.setCountList(countList);
            goodsOrder.setOrderDate(new Date());
            goodsOrder.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        HeySweetieApplication.shopCartMap.clear();
                        refreshShopCar();
                        closeShopCarListView();
                        Toast.makeText(AdminMainActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminMainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //???????????????????????????
    public static void closeShopCarListView() {
        shopCartView.setVisibility(View.GONE);
    }

    //???????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}