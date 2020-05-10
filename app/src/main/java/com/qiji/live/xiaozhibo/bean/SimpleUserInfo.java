package com.qiji.live.xiaozhibo.bean;

/**
 * Created by weipeng on 16/11/22.
 */

public class SimpleUserInfo {
    public String uid;
    public String user_nicename;
    public String avatar;
    public String level;

    public SimpleUserInfo(String uid, String user_nicename, String avatar,String level) {
        this.uid = uid;
        this.user_nicename = user_nicename;
        this.avatar = avatar;
        this.level = level;
    }
}
