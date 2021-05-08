package com.heysweetie.android.logic.model;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class Msg extends BmobObject {
    private Date msgDate;
    private String userPhone;
    private int userImage;
    private List<String> NickName;//每条消息的发言人名字
    private List<String> msgContent;//消息内容
    private List<Integer> msgType;//消息类型，0代表管理员，1代表用户

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

    public List<String> getNickName() {
        return NickName;
    }

    public void setNickName(List<String> nickName) {
        NickName = nickName;
    }

    public Date getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(Date msgDate) {
        this.msgDate = msgDate;
    }
}
