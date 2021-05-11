package com.heysweetie.android.ui.admin.statistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.GoodsOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class StatisticActivity extends AppCompatActivity implements View.OnClickListener {
    private int flag;//0代表查看日，1代表月，2代表年，3代表总的
    private int totalCount = 0;//总销售量
    private double totalPrice = 0.0;//总销售额

    private Calendar calendar;
    private Map<Goods, Integer> goodsCountSale;//存放商品，以及销量,先将数据库中所有商品添加到这里
    private Map<Goods, Double> goodsPriceSale;//存放商品，以及总销售额

    private MaterialToolbar toolBar;
    private Button dayBtn;
    private Button monthBtn;
    private Button yearBtn;
    private Button allBtn;
    private TextView totalPrice_Text;
    private TextView totalCount_Text;
    private RecyclerView goodsSaleCount_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        calendar = Calendar.getInstance();

        initControlUnit();
        initView();
        initClick();

        goodsCountSale = new TreeMap<>();
        goodsPriceSale = new HashMap<>();
        BmobQuery<Goods> goodsBmobQuery = new BmobQuery<>();
        goodsBmobQuery.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if (e == null) {
                    for (Goods goods : object) {
                        goodsCountSale.put(goods, new Integer(0));
                        goodsPriceSale.put(goods, new Double(0.0));
                    }
                } else {
                }
            }
        });
    }

    private void initClick() {
        dayBtn.setOnClickListener(this);
        monthBtn.setOnClickListener(this);
        yearBtn.setOnClickListener(this);
        allBtn.setOnClickListener(this);
    }

    private void initControlUnit() {
        toolBar = findViewById(R.id.toolBar);
        dayBtn = findViewById(R.id.dayBtn);
        monthBtn = findViewById(R.id.monthBtn);
        yearBtn = findViewById(R.id.yearBtn);
        allBtn = findViewById(R.id.allBtn);
        totalPrice_Text = findViewById(R.id.totalPrice);
        totalCount_Text = findViewById(R.id.totalCount);
        goodsSaleCount_recycler = findViewById(R.id.goodsSaleCount_recycler);
    }

    private void initView() {
        //设置标题栏
        toolBar.setTitle("统计数据");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        goodsSaleCount_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    void refresh() {
        //刷新前先清空
        Set<Goods> keys = goodsPriceSale.keySet();
        for (Goods key : keys) {
            goodsCountSale.put(key, new Integer(0));
            goodsPriceSale.put(key, new Double(0.0));
        }
        totalCount = 0;
        totalPrice = 0.0;

        String dateBegin = getDateBegin();
        String dateEnd = getDateEnd();

        try {
            findOrder(dateBegin, dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDateEnd() {//设置结束时间
        String dateEnd;
        if (flag == 0) {
            dateEnd = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                    + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " 23:59:59";
        } else if (flag == 1) {
            dateEnd = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                    + "-" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " 23:59:59";
        } else if (flag == 2) {
            dateEnd = calendar.get(Calendar.YEAR) + "-12-31 23:59:59";
        } else {
            dateEnd = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                    + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " 23:59:59";
        }
        return dateEnd;
    }

    private String getDateBegin() {//设置开始时间
        String dateBegin;
        if (flag == 0) {
            dateBegin = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                    + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
        } else if (flag == 1) {
            dateBegin = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                    + "-1 00:00:00";
        } else if (flag == 2) {
            dateBegin = calendar.get(Calendar.YEAR) + "-1-1 00:00:00";
        } else {
            dateBegin = "1970-1-1 00:00:00";
        }
        return dateBegin;
    }

    private void findOrder(String dateBegin, String dateEnd) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BmobDate bmobDateStart = new BmobDate(sdf.parse(dateBegin));
        BmobDate bmobDateEnd = new BmobDate(sdf.parse(dateEnd));

        BmobQuery<GoodsOrder> categoryBmobQueryStart = new BmobQuery<>();
        categoryBmobQueryStart.addWhereGreaterThanOrEqualTo("createdAt", bmobDateStart);

        BmobQuery<GoodsOrder> categoryBmobQueryEnd = new BmobQuery<>();
        categoryBmobQueryEnd.addWhereLessThanOrEqualTo("createdAt", bmobDateEnd);


        List<BmobQuery<GoodsOrder>> queries = new ArrayList<>();
        queries.add(categoryBmobQueryStart);
        queries.add(categoryBmobQueryEnd);

        BmobQuery<GoodsOrder> goodsOrderBmobQuery = new BmobQuery<>();
        goodsOrderBmobQuery.and(queries);

        goodsOrderBmobQuery.addWhereEqualTo("orderState", 2).findObjects(new FindListener<GoodsOrder>() {
            @Override
            public void done(List<GoodsOrder> goodsOrderList, BmobException e) {
                if (e == null) {
                    //遍历订单 核心代码
                    for (GoodsOrder goodsOrder : goodsOrderList) {
                        for (int i = 0; i < goodsOrder.getGoodsList().size(); i++) {
                            int count = goodsOrder.getCountList().get(i);
                            Goods goods = goodsOrder.getGoodsList().get(i);
                            totalCount += count;
                            totalPrice += count * goods.getSale() * goods.getPrice();

                            if (!goodsCountSale.containsKey(goods)) {
                                goodsCountSale.put(goods, new Integer(0));
                                goodsPriceSale.put(goods, new Double(0.0));
                            }

                            double tempPrice = goodsPriceSale.get(goods);
                            goodsPriceSale.put(goods, tempPrice + count * goods.getSale() * goods.getPrice());

                            int tempCount = goodsCountSale.get(goods);
                            goodsCountSale.put(goods, tempCount + count);
                        }
                    }

                    List<Goods> goodsList = new ArrayList<>();
                    List<Integer> countList = new ArrayList<>();
                    //这里将map.entrySet()转换成list
                    List<Map.Entry<Goods, Integer>> list = new ArrayList<Map.Entry<Goods, Integer>>(goodsCountSale.entrySet());
                    //然后通过比较器来实现排序
                    Collections.sort(list, new Comparator<Map.Entry<Goods, Integer>>() {
                        //降序排序
                        @Override
                        public int compare(Map.Entry<Goods, Integer> o1, Map.Entry<Goods, Integer> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }

                    });

                    for (Map.Entry<Goods, Integer> mapping : list) {
                        if (mapping.getValue() > 0) {//获取所有数量大于0的商品
                            goodsList.add(mapping.getKey());
                            countList.add(mapping.getValue());
                        }
                    }

                    goodsSaleCount_recycler.setAdapter(new StatisticAdapter(StatisticActivity.this, goodsList, countList, goodsPriceSale));
                    totalPrice_Text.setText("总销售额￥" + Double.parseDouble(String.format("%.2f", totalPrice)));
                    totalCount_Text.setText("总销量 " + totalCount);
                } else {
                    Toast.makeText(StatisticActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDatePicker() {//弹出日历
        calendar = Calendar.getInstance();
        View datePickerView = LayoutInflater.from(StatisticActivity.this).inflate(R.layout.date_picker_dialog, null);
        DatePicker datePicker = datePickerView.findViewById(R.id.date_picker);
        datePicker.setMaxDate(calendar.getTimeInMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
            }
        });
        final AlertDialog dialog = new AlertDialog.Builder(StatisticActivity.this).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setView(datePickerView);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                refresh();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dayBtn) {
            flag = 0;
            showDatePicker();
        } else if (id == R.id.monthBtn) {
            flag = 1;
            showDatePicker();
        } else if (id == R.id.yearBtn) {
            flag = 2;
            showDatePicker();
        } else if (id == R.id.allBtn) {
            flag = 3;
            refresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}