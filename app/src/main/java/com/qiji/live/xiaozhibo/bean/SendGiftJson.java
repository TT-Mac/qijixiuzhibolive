package com.qiji.live.xiaozhibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/3/30.
 */
public class SendGiftJson implements Parcelable {
    private String type;
    private String action;
    private int uid;
    private int touid;
    private int giftid;
    private int giftcount;
    private int totalcoin;
    private String showid;
    private String addtime;
    private String giftname;
    private String gifticon;
    private String evensend;
    private long sendTime;
    private String avatar;
    private String nicename;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getEvensend() {
        return evensend;
    }

    public void setEvensend(String eventsend) {
        this.evensend = eventsend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGiftid() {
        return giftid;
    }

    public void setGiftid(int giftid) {
        this.giftid = giftid;
    }

    public int getTouid() {
        return touid;
    }

    public void setTouid(int touid) {
        this.touid = touid;
    }

    public int getGiftcount() {
        return giftcount;
    }

    public void setGiftcount(int giftcount) {
        this.giftcount = giftcount;
    }

    public int getTotalcoin() {
        return totalcoin;
    }

    public void setTotalcoin(int totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.action);
        dest.writeInt(this.uid);
        dest.writeInt(this.touid);
        dest.writeInt(this.giftid);
        dest.writeInt(this.giftcount);
        dest.writeInt(this.totalcoin);
        dest.writeString(this.showid);
        dest.writeString(this.addtime);
        dest.writeString(this.giftname);
        dest.writeString(this.gifticon);
        dest.writeString(this.evensend);
        dest.writeLong(this.sendTime);
        dest.writeString(this.avatar);
        dest.writeString(this.nicename);
    }

    public SendGiftJson() {
    }

    protected SendGiftJson(Parcel in) {
        this.type = in.readString();
        this.action = in.readString();
        this.uid = in.readInt();
        this.touid = in.readInt();
        this.giftid = in.readInt();
        this.giftcount = in.readInt();
        this.totalcoin = in.readInt();
        this.showid = in.readString();
        this.addtime = in.readString();
        this.giftname = in.readString();
        this.gifticon = in.readString();
        this.evensend = in.readString();
        this.sendTime = in.readLong();
        this.avatar = in.readString();
        this.nicename = in.readString();
    }

    public static final Creator<SendGiftJson> CREATOR = new Creator<SendGiftJson>() {
        @Override
        public SendGiftJson createFromParcel(Parcel source) {
            return new SendGiftJson(source);
        }

        @Override
        public SendGiftJson[] newArray(int size) {
            return new SendGiftJson[size];
        }
    };
}
