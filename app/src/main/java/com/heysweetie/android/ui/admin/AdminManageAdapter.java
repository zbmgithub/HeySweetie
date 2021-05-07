package com.heysweetie.android.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.User;

import java.util.List;

public class AdminManageAdapter extends RecyclerView.Adapter<AdminManageAdapter.ViewHolder> {
    private List<User> userList;
    private Context context;

    public AdminManageAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_adminmanage_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.edit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminManageDetailActivity.class);
                intent.putExtra("user_data", userList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.adminName_Text.setText(user.getUserNickName());
        if (user.getAdmin() == 1) {
            holder.adminPower_Text.setText("普通管理员");
        } else if (user.getAdmin() == 2) {
            holder.adminPower_Text.setText("超级管理员");
        } else {
            holder.adminPower_Text.setText("操作权限有误尽快修改");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView adminName_Text;
        TextView adminPower_Text;
        Button edit_Btn;

        public ViewHolder(View v) {
            super(v);
            adminName_Text = v.findViewById(R.id.adminName_Text);
            adminPower_Text = v.findViewById(R.id.adminPower_Text);
            edit_Btn = v.findViewById(R.id.edit_Btn);
        }
    }
}
