package com.heysweetie.android.ui.admin.goodsmanage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;

import java.util.List;

public class GoodsImageAdapter extends RecyclerView.Adapter<GoodsImageAdapter.ViewHolder> {
    private List<Integer> imageIdList;
    private Context context;

    public GoodsImageAdapter(Context context, List<Integer> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        public ViewHolder(@NonNull View view) {
            super(view);
            goodsImage = view.findViewById(R.id.goodsImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_image_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.goodsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int imageId = imageIdList.get(position);
                GoodsManageDetailActivity.changeImage(context,imageId);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageId = imageIdList.get(position);
        Glide.with(context).load(imageId).into(holder.goodsImage);
    }

    @Override
    public int getItemCount() {
        return imageIdList.size();
    }

}
