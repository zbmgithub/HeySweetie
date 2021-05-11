package com.heysweetie.android.logic.model;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class Goods extends BmobObject implements Serializable, Comparable<Goods> {
    //父类默认有唯一标志 String objectId; 作为商品编号
    private int goodsState = 0;//商品状态 0代表上架不折扣，1代表下架
    private String goodsSortName = "";//商品类别 暂时未用到，可以根据这个属性实现按类别浏览等等
    private int goodsSortId = 0;//商品类别id,
    private String goodsName = "";//商品名字
    private String goodsSimpleName = "";//商品简称,默认与商品名一致 暂时未用到
    private String goodsProfile = "";//商品简介
    private double price = 0;//商品价格 商品实时售价 = 商品价格*商品折扣
    private double sale = 1;//商品折扣 0~1 0.6表示6折
    private Date saleBeginTime = null;//折扣开始时间 暂时未用到
    private Date saleEndTime = null;//折扣结束时间 暂时未用到
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

    //重写hashcode方法和equals方法是为了使用hashmap实现购物车功能：把goods作为key，数量作为value
    //BmobObject的objectId可以保证商品唯一，所以可以借助这个方便的实现重写
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Goods p = (Goods) obj;
        if (!this.getObjectId().equals(p.getObjectId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getObjectId().hashCode();
    }

    //重写compareTo是为了treeMap，同样是将goods作为key值
    @Override
    public int compareTo(Goods o) {
        return getObjectId().compareTo(o.getObjectId());
    }
}
