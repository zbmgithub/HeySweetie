package com.heysweetie.android.ui.admin.goodsmanage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
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
import com.heysweetie.android.ui.common.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.heysweetie.android.HeySweetieApplication.context;
import static java.lang.Thread.sleep;


public class GoodsManageDetailActivity extends BaseActivity implements View.OnClickListener {
    private static Goods goods;
    private boolean add_new_goods_flag;

    private MaterialToolbar toolbar;
    private static ImageView goodsImage;
    private TextView goodsId;
    private EditText goodsName;
    private EditText goodsPrice;
    private EditText goodsSale;
    private EditText goodsState;
    private EditText goodsProfile;
    private Button updateBtn;
    private Button cancelBtn;
    private Button deleteBtn;
    private Button localImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manage_detail);
        //获取传递过来的goods对象,以及是否是点击添加新商品，修改商品与添加新商品共用一个activity
        Intent intent = getIntent();
        goods = (Goods) intent.getSerializableExtra("goods_data");
        add_new_goods_flag = intent.getBooleanExtra("add_new_goods_flag", false);

        initControlUnit();//初始化控件
        initView();

        updateBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        //获取本地图片按钮
        localImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        if (add_new_goods_flag) {
            initAddGoodsView();
        }
    }

    private void initControlUnit() {
        toolbar = findViewById(R.id.toolBar);
        goodsImage = findViewById(R.id.goodsImage);
        goodsId = findViewById(R.id.goodsId);
        goodsName = findViewById(R.id.goodsName);
        goodsPrice = findViewById(R.id.goodsPrice);
        goodsSale = findViewById(R.id.goodsSale);
        goodsState = findViewById(R.id.goodsState);
        goodsProfile = findViewById(R.id.godosProfile);
        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        localImageBtn = findViewById(R.id.localImageBtn);
    }

    private void initView() {
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //设置标题栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("商品管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置商品图片显示
        if (goods.getGoodsImage() != null)
            Glide.with(this).load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg")).into(goodsImage);
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

    public void operate(int i) {//0代表更新按钮，1代表取消按钮，2代表删除按钮
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
                                //先上传文件，再更新goods
                                BmobFile file = new BmobFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg"));
                                file.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            goods.setGoodsImage(file);
                                            goods.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null)
                                                        Toast.makeText(GoodsManageDetailActivity.this, "成功修改，下拉刷新", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(GoodsManageDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(GoodsManageDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else if (add_new_goods_flag) {
                                BmobFile file = new BmobFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg"));
                                file.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            goods.setGoodsImage(file);
                                            goods.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String objectId, BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(GoodsManageDetailActivity.this, "添加成功: 商品objectId为：" + objectId, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(GoodsManageDetailActivity.this, "添加失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(GoodsManageDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else if (i == 1) {//点击取消不做任何操作
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
                            sleep(500);//解决返回界面显示数据不同步，阻塞主进程，异步处理转同步，仍有风险
                        } catch (
                                InterruptedException e) {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //获取本地图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                Uri uri = data.getData();
                try {
                    //将url转bitmap
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());

                    //将bitmap转file

                    //filename根据时间命名
                    Calendar now = new GregorianCalendar();
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String fileName = simpleDate.format(now.getTime());
                    //获取外部存储路径 /sdcard/Android/data/com.heysweetie.android/files/Pictures
                    File filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    //创建file对象
                    File file = new File(filesDir, fileName + ".jpg");//将要保存图片的路径
                    //bitmap转file
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();

                    goods.setGoodsImageName(fileName);
                    //加载图片到imageView
                    Glide.with(GoodsManageDetailActivity.this).load(file).into(goodsImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}