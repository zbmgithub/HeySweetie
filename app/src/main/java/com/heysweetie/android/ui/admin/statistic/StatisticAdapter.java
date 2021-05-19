package com.heysweetie.android.ui.admin.statistic;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {
    private Context context;
    private List<Goods> goodsList;
    private List<Integer> countList;
    private Map<Goods, Double> goodsPriceSale;

    public StatisticAdapter(Context context, List<Goods> goodsList, List<Integer> countList, Map<Goods, Double> goodsPriceSale) {
        this.context = context;
        this.goodsList = goodsList;
        this.countList = countList;
        this.goodsPriceSale = goodsPriceSale;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView goodsImage;
        private TextView goodsName;
        private TextView goodsPrice;
        private TextView totalPrice;
        private TextView saleCount;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
            goodsName = view.findViewById(R.id.goodsName);
            goodsPrice = view.findViewById(R.id.goodsPrice);
            totalPrice = view.findViewById(R.id.totalPrice);
            saleCount = view.findViewById(R.id.saleCount);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.statistic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        if (goods.getGoodsImage() != null)
            Glide.with(context).load(new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg")).into(holder.goodsImage);
        holder.goodsName.setText(goods.getGoodsName());
        holder.totalPrice.setText("¥ " + Double.parseDouble(String.format("%.2f", goodsPriceSale.get(goods))));
        holder.saleCount.setText("销量 " + countList.get(position));
        holder.goodsPrice.setText("¥ " + Double.parseDouble(String.format("%.2f", goodsPriceSale.get(goods) / countList.get(position))));
        //平均售价等于销售额除以销售量，加了格式转换取小数点后两位，以及数量为零是的3目运算表达式处理可能发生的除零异常
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }
}
