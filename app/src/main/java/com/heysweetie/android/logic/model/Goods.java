package com.heysweetie.android.logic.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Goods extends BmobObject {
    //父类默认有唯一标志 String objectId; 作为商品编号
    private int goodsState = 0;//商品状态 0代表上架不折扣，1代表上架折扣，2代表下架
    private String goodsSortName = "";//商品类别
    private int goodsSortId = 0;//商品类别id,
    private String goodsName = "";//商品名字
    private String goodsSimpleName = "";//商品简称,默认与商品名一致
    private String goodsProfile = "";//商品简介
    private double price = 0;//商品价格
    private double sale = 1;//折扣力度
    private Date saleBeginTime = null;//折扣开始时间
    private Date saleEndTime = null;//折扣结束时间
    private int imageId = 0;//商品图片id

    public int getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(int goodsState) {
        this.goodsState = goodsState;
    }

    public String getGoodsSortName() {
        return goodsSortName;
    }

    public void setGoodsSortName(String goodsSortName) {
        this.goodsSortName = goodsSortName;
    }

    public int getGoodsSortId() {
        return goodsSortId;
    }

    public void setGoodsSortId(int goodsSortId) {
        this.goodsSortId = goodsSortId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSimpleName() {
        return goodsSimpleName;
    }

    public void setGoodsSimpleName(String goodsSimpleName) {
        this.goodsSimpleName = goodsSimpleName;
    }

    public String getGoodsProfile() {
        return goodsProfile;
    }

    public void setGoodsProfile(String goodsProfile) {
        this.goodsProfile = goodsProfile;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    public Date getSaleBeginTime() {
        return saleBeginTime;
    }

    public void setSaleBeginTime(Date saleBeginTime) {
        this.saleBeginTime = saleBeginTime;
    }

    public Date getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(Date saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

}
