package com.heysweetie.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.MainActivity;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.ui.admin.AdminMainActivity;
import com.heysweetie.android.ui.client.ClientMainActivity;
import com.heysweetie.android.ui.client.GoodsDetailActivity;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private Context context;

    public GoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
            goodsName = view.findViewById(R.id.goodsName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra("goods_data", goods);
                context.startActivity(intent);
                if (context.getClass() == GoodsDetailActivity.class)//已经在商品详情页，关闭当前页，
                    ((GoodsDetailActivity) context).finish();
                //关闭当前页面的购物车详细信息
                if ((Activity) context instanceof GoodsDetailActivity)
                    GoodsDetailActivity.closeShopCarListView();
                if ((Activity) context instanceof MainActivity)
                    AdminMainActivity.closeShopCarListView();
                if ((Activity) context instanceof ClientMainActivity) {
                    ClientMainActivity.closeShopCarListView();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.goodsName.setText(goods.getGoodsName());

        Glide.with(context).load(goods.getImageId()).into(holder.goodsImage);
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

}
