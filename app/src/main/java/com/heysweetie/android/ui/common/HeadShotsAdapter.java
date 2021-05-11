package com.heysweetie.android.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.ui.admin.adminmanage.AdminManageDetailActivity;
import com.heysweetie.android.ui.client.ClientInfoManageActivity;

import java.util.List;

public class HeadShotsAdapter extends RecyclerView.Adapter<HeadShotsAdapter.ViewHolder> {
    private List<Integer> imageIdList;
    private Context context;

    public HeadShotsAdapter(Context context, List<Integer> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView headShot_Image;

        public ViewHolder(@NonNull View view) {
            super(view);
            headShot_Image = view.findViewById(R.id.headShot_Image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.headshot_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.headShot_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int imageId = imageIdList.get(position);
                if (context instanceof AdminManageDetailActivity)
                    AdminManageDetailActivity.changeImage(context, imageId);
                else if (context instanceof ClientInfoManageActivity)
                    ClientInfoManageActivity.changeImage(context, imageId);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageId = imageIdList.get(position);
        Glide.with(context).load(imageId).into(holder.headShot_Image);
    }

    @Override
    public int getItemCount() {
        return imageIdList.size();
    }

}
