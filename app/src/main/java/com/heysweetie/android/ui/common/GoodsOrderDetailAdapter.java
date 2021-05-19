package com.heysweetie.android.ui.common;

import android.content.Context;
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

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class GoodsOrderDetailAdapter extends RecyclerView.Adapter<GoodsOrderDetailAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private List<Integer> countList;
    private Context context;

    public GoodsOrderDetailAdapter(Context context, List<Goods> goodsList, List<Integer> countList) {
        this.context = context;
        this.goodsList = goodsList;
        this.countList = countList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView goodsCount;
        TextView goodsPrice;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
            goodsName = view.findViewById(R.id.goodsName);
            goodsCount = view.findViewById(R.id.goodsCount);
            goodsPrice = view.findViewById(R.id.goodsPrice);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_order_detail_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        int count = countList.get(position);
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
        holder.goodsName.setText(goods.getGoodsName());
        holder.goodsCount.setText("数量： " + count + " 杯");
        //设置为两位小数
        double priceOutput = Double.parseDouble(String.format("%.2f", goods.getPrice() * goods.getSale()));
        holder.goodsPrice.setText("单价： ￥" + priceOutput);

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

}
