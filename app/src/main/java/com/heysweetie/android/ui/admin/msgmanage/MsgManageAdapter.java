package com.heysweetie.android.ui.admin.msgmanage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Msg;
import com.heysweetie.android.logic.model.User;
import com.heysweetie.android.ui.common.MemosActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MsgManageAdapter extends RecyclerView.Adapter<MsgManageAdapter.ViewHolder> {
    private List<Msg> msgList;
    private Context context;
    private User user;

    public MsgManageAdapter(Context context, List<Msg> msgList, User user) {
        this.context = context;
        this.msgList = msgList;
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memos_manage_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MemosActivity.class);
                intent.putExtra("user_phone", msgList.get(holder.getAdapterPosition()).getUserPhone());
                intent.putExtra("user_data", user);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        Glide.with(context).load(msg.getUserImage()).into(holder.headShot_Image);
        holder.userPhoneNum.setText(msg.getUserPhone());

        //设置日期
        Date date = msg.getMsgDate();
        if (date == null) holder.msgDate.setText("0000-00-00 00:00:00");
        else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = df.format(date);
            holder.msgDate.setText(str);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView headShot_Image;
        TextView userPhoneNum;
        TextView msgDate;

        public ViewHolder(View view) {
            super(view);
            headShot_Image = view.findViewById(R.id.headShot_Image);
            userPhoneNum = view.findViewById(R.id.userPhoneNum);
            msgDate = view.findViewById(R.id.msgDate);
        }
    }
}
