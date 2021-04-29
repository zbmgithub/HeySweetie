package com.heysweetie.android.logic.model;

import cn.bmob.v3.BmobObject;

public class GoodsSort extends BmobObject {
    private String goodsSortName;//商品类别名
    private int goodsSortId;//商品类别Id

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
}
