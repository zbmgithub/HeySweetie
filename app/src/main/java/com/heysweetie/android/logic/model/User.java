package com.heysweetie.android.logic.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class User extends BmobUser implements Serializable {
    //BombUser  父类默认属性
    //username	用户名/账号/用户唯一标志，可以是邮箱、手机号码、第三方平台的用户唯一标志
    //password	用户密码
    //email	用户邮箱
    //emailVerified	用户邮箱认证状态
    //mobilePhoneNumber	用户手机号码
    //mobilePhoneNumberVerified	用户手机号码认证状态

    private String userNickName;//用户昵称
    private int admin;//0代表普通用户，1代表普通后台管理，2代表超级管理员
    private int userImageId;//用户头像
    private String userPassword;//用户密码

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }


    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public int getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(int userImageId) {
        this.userImageId = userImageId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
