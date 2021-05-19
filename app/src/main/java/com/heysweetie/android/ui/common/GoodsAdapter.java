package com.heysweetie.android.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.admin.main.AdminMainActivity;
import com.heysweetie.android.ui.client.ClientMainActivity;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private Context context;
    private User user;

    public GoodsAdapter(Context context, List<Goods> goodsList, User user) {
        this.context = context;
        this.goodsList = goodsList;
        this.user = user;
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
                intent.putExtra("user_data", user);
                context.startActivity(intent);

                if (context.getClass() == GoodsDetailActivity.class)//已经在商品详情页，关闭当前页，
                    ((GoodsDetailActivity) context).finish();
                //关闭当前页面的购物车详细信息
                if ((Activity) context instanceof GoodsDetailActivity)
                    GoodsDetailActivity.closeShopCarListView();
                if ((Activity) context instanceof AdminMainActivity)
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
        if (goods.getGoodsImage() != null) {//显示商品图片为传递过来的商品图片
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), goods.getGoodsImageName() + ".jpg");
            if (file.exists())
                Glide.with(context).load(file).into(holder.goodsImage);
            else {
                Glide.with(context).load(goods.getGoodsImage().getUrl()).into(holder.goodsImage);
                //解决图片未下载完成时，图片显示空白
            }
        }
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

}
