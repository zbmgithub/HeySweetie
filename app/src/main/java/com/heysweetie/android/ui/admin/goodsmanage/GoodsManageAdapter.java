package com.heysweetie.android.ui.admin.goodsmanage;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

import static com.heysweetie.android.HeySweetieApplication.context;

public class GoodsManageAdapter extends RecyclerView.Adapter<GoodsManageAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private Context context;

    public GoodsManageAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView goodsPrice;
        LinearLayout setGoods;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
            goodsName = view.findViewById(R.id.goodsName);
            goodsPrice = view.findViewById(R.id.goodsPrice);
            setGoods = view.findViewById(R.id.setGoods);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_manage_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);
                Intent intent = new Intent(context, GoodsManageDetailActivity.class);
                intent.putExtra("goods_data", goods);
                context.startActivity(intent);
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
                BmobFile bmobFile = goods.getGoodsImage();
                bmobFile.download(file, new DownloadFileListener() {
                    @Override
                    public void onProgress(Integer value, long total) {

                    }

                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                        } else {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        double price = goods.getPrice() * goods.getSale();
        double priceOutput = Double.parseDouble(String.format("%.2f", price));
        holder.goodsPrice.setText("¥ " + priceOutput);
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

}
