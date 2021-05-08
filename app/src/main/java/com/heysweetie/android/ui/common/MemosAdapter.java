package com.heysweetie.android.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Msg;
import com.heysweetie.android.logic.model.User;

import java.util.List;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.ViewHolder> {
    private Msg msg;
    private Context context;

    public MemosAdapter(Context context, Msg msg) {
        this.context = context;
        this.msg = msg;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg_Text;
        ImageView headShot_Image;

        public ViewHolder(@NonNull View view) {
            super(view);
            msg_Text = view.findViewById(R.id.msg_Text);
            headShot_Image = view.findViewById(R.id.headShot_Image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.memos_left_item, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.memos_right_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.msg_Text.setText(msg.getMsgContent().get(position));
        if (msg.getMsgType().get(position) == 1) {
            Glide.with(context).load(msg.getUserImage()).into(holder.headShot_Image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return msg.getMsgType().get(position);
    }

    @Override
    public int getItemCount() {
        return msg.getMsgType().size();
    }
}
