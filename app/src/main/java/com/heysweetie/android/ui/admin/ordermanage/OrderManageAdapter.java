package com.heysweetie.android.ui.admin.ordermanage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Goods;
import com.heysweetie.android.logic.model.GoodsOrder;
import com.heysweetie.android.ui.common.GoodsOrderDetailAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.ViewHolder> {
    private List<GoodsOrder> goodsOrderList;
    private Context context;

    public OrderManageAdapter(Context context, List<GoodsOrder> goodsOrderList) {
        this.context = context;
        this.goodsOrderList = goodsOrderList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView goodsOrderId;
        TextView goodsOrderState;
        RecyclerView goodsOrderDetail_RecycleView;
        TextView totalPrice;
        TextView date;
        Button cancelOrder_Btn;
        TextView userPhoneNum;
        TextView acceptOrder;
        TextView finishOrder;
        LinearLayout orderOperation;

        public ViewHolder(@NonNull View view) {
            super(view);
            goodsOrderId = view.findViewById(R.id.goodsOrderId);
            goodsOrderState = view.findViewById(R.id.goodsOrderState);
            goodsOrderDetail_RecycleView = view.findViewById(R.id.goodsOrderDetail_RecycleView);
            totalPrice = view.findViewById(R.id.totalPrice);
            date = view.findViewById(R.id.date);
            cancelOrder_Btn = view.findViewById(R.id.cancelOrder_Btn);
            acceptOrder = view.findViewById(R.id.acceptOrder);
            finishOrder = view.findViewById(R.id.finishOrder);
            orderOperation = view.findViewById(R.id.orderOperation);
            userPhoneNum = view.findViewById(R.id.userPhoneNum);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_goods_order_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        //????????????
        holder.cancelOrder_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsOrder goodsOrder = goodsOrderList.get(holder.getAdapterPosition());
                new AlertDialog.Builder(context).setTitle("????????????")
                        .setMessage("??????????????????????????????")
                        .setCancelable(false)
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goodsOrder.setOrderState(3);
                                goodsOrder.update(goodsOrder.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                                            holder.cancelOrder_Btn.setVisibility(View.GONE);
                                            holder.goodsOrderState.setText("??????????????? ?????????");
                                            holder.orderOperation.setVisibility(View.GONE);
                                        } else {
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });
        //????????????
        holder.acceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsOrder goodsOrder = goodsOrderList.get(holder.getAdapterPosition());
                //???????????????????????????????????????????????????????????????
                BmobQuery<GoodsOrder> bmobQuery = new BmobQuery<>();
                bmobQuery.getObject(goodsOrder.getObjectId(), new QueryListener<GoodsOrder>() {
                    @Override
                    public void done(GoodsOrder goodsOrder, BmobException e) {
                        if (e == null) {
                            if (goodsOrder.getOrderState() == 3) {
                                Toast.makeText(context, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                            } else {
                                //????????????
                                goodsOrder.setOrderState(1);
                                goodsOrder.update(goodsOrder.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                                            holder.goodsOrderState.setText("??????????????? ?????????");
                                            holder.acceptOrder.setVisibility(View.GONE);
                                            holder.finishOrder.setVisibility(View.VISIBLE);
                                        } else {
                                            goodsOrder.setOrderState(0);
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                });

            }
        });
        //????????????
        holder.finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsOrder goodsOrder = goodsOrderList.get(holder.getAdapterPosition());
                goodsOrder.setOrderState(2);
                goodsOrder.update(goodsOrder.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                            holder.cancelOrder_Btn.setVisibility(View.GONE);
                            holder.goodsOrderState.setText("??????????????? ?????????");
                            holder.orderOperation.setVisibility(View.GONE);
                        } else {
                            goodsOrder.setOrderState(1);
                        }
                    }
                });
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GoodsOrder goodsOrder = goodsOrderList.get(position);
        holder.goodsOrderId.setText(goodsOrder.getObjectId());
        int state = goodsOrder.getOrderState();
        if (state == 0) {
            holder.goodsOrderState.setText("??????????????? ?????????");
            holder.finishOrder.setVisibility(View.GONE);
        } else if (state == 1) {
            holder.goodsOrderState.setText("??????????????? ?????????");
            holder.acceptOrder.setVisibility(View.GONE);
        } else if (state == 2) {
            holder.goodsOrderState.setText("??????????????? ?????????");
            holder.cancelOrder_Btn.setVisibility(View.GONE);
            holder.orderOperation.setVisibility(View.GONE);
        } else if (state == 3) {
            holder.goodsOrderState.setText("??????????????? ?????????");
            holder.cancelOrder_Btn.setVisibility(View.GONE);
            holder.orderOperation.setVisibility(View.GONE);
        }

        holder.userPhoneNum.setText("??????????????? " + goodsOrder.getUsername());

        List<Goods> goodsList = goodsOrder.getGoodsList();
        List<Integer> countList = goodsOrder.getCountList();

        double price = 0;
        for (int i = 0; i < goodsList.size(); i++) {
            Goods goods = goodsList.get(i);
            price += goods.getPrice() * goods.getSale() * countList.get(i);
        }
        //?????????????????????
        double priceOutput = Double.parseDouble(String.format("%.2f", price));
        holder.totalPrice.setText("????????? ???" + priceOutput + " ???");
        //?????????????????????????????????
        GoodsOrderDetailAdapter adapter = new GoodsOrderDetailAdapter(context, goodsList, countList);
        holder.goodsOrderDetail_RecycleView.setAdapter(adapter);
        holder.goodsOrderDetail_RecycleView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        Date date = goodsOrder.getOrderDate();
        if (date == null) holder.date.setText("?????? 2021-5-4  14:35");
        else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = df.format(date);
            holder.date.setText("?????? " + str);
        }
    }

    @Override
    public int getItemCount() {
        return goodsOrderList.size();
    }

}
