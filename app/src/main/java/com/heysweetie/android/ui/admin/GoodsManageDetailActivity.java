package com.heysweetie.android.ui.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static java.lang.Thread.sleep;


public class GoodsManageDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static Goods goods;
    private boolean add_new_goods_flag;

    private MaterialToolbar toolbar;
    private static ImageView goodsImage;
    private RecyclerView allGodosImageRecylcerView;
    private TextView goodsId;
    private EditText goodsName;
    private EditText goodsPrice;
    private EditText goodsSale;
    private EditText goodsState;
    private EditText goodsProfile;
    private Button updateBtn;
    private Button cancelBtn;
    private Button deleteBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manage_detail);
        //获取传递过来的goods对象
        Intent intent = getIntent();
        goods = (Goods) intent.getSerializableExtra("goods_data");
        add_new_goods_flag = intent.getBooleanExtra("add_new_goods_flag", false);

        initControlUnit();
        initView();

        updateBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        if (add_new_goods_flag) {
            initAddGoodsView();
        }
    }

    private void initControlUnit() {
        toolbar = findViewById(R.id.toolBar);
        goodsImage = findViewById(R.id.goodsImage);
        allGodosImageRecylcerView = findViewById(R.id.allGodosImageRecylcerView);
        goodsId = findViewById(R.id.goodsId);
        goodsName = findViewById(R.id.goodsName);
        goodsPrice = findViewById(R.id.goodsPrice);
        goodsSale = findViewById(R.id.goodsSale);
        goodsState = findViewById(R.id.goodsState);
        goodsProfile = findViewById(R.id.godosProfile);
        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //设置标题栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("商品管理");
        //设置显示界面
        Glide.with(this).load(goods.getImageId()).into(goodsImage);

        //设置recycleView显示所有照片
        GoodsImageAdapter adapter = new GoodsImageAdapter(GoodsManageDetailActivity.this, HeySweetieApplication.imageId);//所有商品添加到适配器
        allGodosImageRecylcerView.setAdapter(adapter);
        allGodosImageRecylcerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置其他商品信息
        goodsId.setText(goods.getObjectId());
        goodsName.setText(goods.getGoodsName());
        goodsPrice.setText(goods.getPrice() + "");
        goodsSale.setText(goods.getSale() + "");
        goodsState.setText(goods.getGoodsState() + "");
        goodsProfile.setText(goods.getGoodsProfile());
    }

    private void initAddGoodsView() {
        deleteBtn.setVisibility(View.GONE);//如果是添加商品，那么删除按钮不显示
    }

    public static void changeImage(Context context, int imageId) {
        goods.setImageId(imageId);
        Glide.with(context).load(imageId).into(goodsImage);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.updateBtn) {
            operate(0);//代表更新
        } else if (id == R.id.cancelBtn) {
            operate(1);//代表取消
        } else if (id == R.id.deleteBtn) {
            operate(2);//代表删除
        }
    }

    public void operate(int i) {
        new AlertDialog.Builder(this)
                .setTitle("核对操作")
                .setMessage("确认继续你现在的操作吗？")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (i == 0) {
                            goods.setGoodsName(goodsName.getText().toString());
                            goods.setPrice(Double.valueOf(goodsPrice.getText().toString()));
                            goods.setSale(Double.valueOf(goodsSale.getText().toString()));
                            goods.setGoodsState(Integer.valueOf(goodsState.getText().toString()));
                            goods.setGoodsProfile(goodsProfile.getText().toString());
                            if (!add_new_goods_flag) {
                                goods.update(goods.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(GoodsManageDetailActivity.this, "更新成功:" + goods.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(GoodsManageDetailActivity.this, "更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                            } else if (add_new_goods_flag) {
                                goods.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(GoodsManageDetailActivity.this, "添加成功: objectId为：" + objectId, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(GoodsManageDetailActivity.this, "添加失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else if (i == 1) {
                        } else if (i == 2) {
                            goods.delete(new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(GoodsManageDetailActivity.this, "删除成功:" + goods.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(GoodsManageDetailActivity.this, "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        }
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
}