package com.heysweetie.android.logic.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;

public class Goods extends BmobObject {
    //父类默认有唯一标志 String objectId; 作为商品编号
    private int goodsState;//商品状态 0代表上架不折扣，1代表上架折扣，2代表下架
    private String goodsSort;//商品类别
    private String goodsName;//商品名字
    private String goodsSimpleName;//商品简称,默认与商品名一致
    private double price;//商品价格
    private double sale;//折扣力度
    private Date saleBeginTime;//折扣开始时间
    private Date saleEndTime;//折扣结束时间
}
