package com.heysweetie.android.logic.model;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class Msg extends BmobObject {
    private Date msgDate;//客户最新留言日期
    private String userPhone;//用户手机号
    private int userImage;//用户头像
    private List<String> msgContent;//顺序存放消息内容
    private List<Integer> msgType;//顺序存放消息类型，0代表管理员，1代表用户

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public List<String> getMsgContent() {
        return msgContent;
    }

    public void setMsContent(List<String> msgContent) {
        this.msgContent = msgContent;
    }

    public List<Integer> getMsgType() {
        return msgType;
    }

    public void setMsgType(List<Integer> msgType) {
        this.msgType = msgType;
    }

    public Date getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(Date msgDate) {
        this.msgDate = msgDate;
    }
}
