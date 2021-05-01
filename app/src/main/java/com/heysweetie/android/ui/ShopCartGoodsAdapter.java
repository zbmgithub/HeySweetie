package com.heysweetie.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.HeySweetieApplication;
import com.heysweetie.android.MainActivity;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.ui.admin.AdminMainActivity;
import com.heysweetie.android.ui.client.ClientMainActivity;
import com.heysweetie.android.ui.client.GoodsDetailActivity;
import com.heysweetie.android.ui.client.ShopCartActivity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopCartGoodsAdapter extends RecyclerView.Adapter<ShopCartGoodsAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private Context context;

    public ShopCartGoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView goodsPrice;
        TextView goodsCount;
        TextView minusText;
        TextView addText;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
            goodsName = view.findViewById(R.id.goodsName);
            goodsPrice = view.findViewById(R.id.goodsPrice);
            goodsCount = view.findViewById(R.id.goodsCount);
            minusText = view.findViewById(R.id.minusText);
            addText = view.findViewById(R.id.addText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopcart_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.minusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);
                //int count = Integer.valueOf(holder.goodsCount.getText().toString());
                int count = HeySweetieApplication.shopCartMap.get(goods) == null ? 0 : HeySweetieApplication.shopCartMap.get(goods);
                if (count > 0) {
                    count--;
                    HeySweetieApplication.shopCartMap.put(goods, count);
                    holder.goodsCount.setText(count + "");

                    double price = goods.getPrice() * goods.getSale() * count;
                    double priceOutput = Double.parseDouble(String.format("%.2f", price));
                    holder.goodsPrice.setText("¥ " + priceOutput + "");
                    //刷新购物栏
                    if ((Activity) context instanceof GoodsDetailActivity)
                        GoodsDetailActivity.refreshShopCar();
                    if ((Activity) context instanceof MainActivity)
                        AdminMainActivity.refreshShopCar();
                    if ((Activity) context instanceof ClientMainActivity)
                        ClientMainActivity.refreshShopCar();
                    if ((Activity) context instanceof ShopCartActivity)
                        ShopCartActivity.refreshShopCar();
                }
            }
        });
        holder.addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);

                //int count = Integer.valueOf(holder.goodsCount.getText().toString());
                int count = HeySweetieApplication.shopCartMap.get(goods) == null ? 0 : HeySweetieApplication.shopCartMap.get(goods);
                count++;
                HeySweetieApplication.shopCartMap.put(goods, count);
                holder.goodsCount.setText(count + "");

                double price = goods.getPrice() * goods.getSale() * count;
                double priceOutput = Double.parseDouble(String.format("%.2f", price));
                holder.goodsPrice.setText("¥ " + priceOutput + "");
                //刷新购物栏
                if ((Activity) context instanceof GoodsDetailActivity)
                    GoodsDetailActivity.refreshShopCar();
                if ((Activity) context instanceof MainActivity)
                    AdminMainActivity.refreshShopCar();
                if ((Activity) context instanceof ClientMainActivity)
                    ClientMainActivity.refreshShopCar();
                if ((Activity) context instanceof ShopCartActivity)
                    ShopCartActivity.refreshShopCar();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.goodsName.setText(goods.getGoodsName());
        Glide.with(context).load(goods.getImageId()).into(holder.goodsImage);
        int count = HeySweetieApplication.shopCartMap.get(goods);
        double price = goods.getPrice() * goods.getSale() * count;
        double priceOutput = Double.parseDouble(String.format("%.2f", price));
        holder.goodsPrice.setText("¥ " + priceOutput + "");
        holder.goodsCount.setText(count + "");
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

}
