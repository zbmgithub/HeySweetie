package com.heysweetie.android.logic.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;

public class GoodsOrder extends BmobObject {
    //父类 objectId默认为 订单编号
    private int orderState;//订单状态 0待确定,1待完成,2作废,3完成
    private String goodsNo;//商品编号
    private int amount;//商品数量
    private String phoneNumber;//顾客手机号
    private double dealPrice;//成交价格
    private String clientNote;//客户备注
    private Date orderDate;//下单日期

}
