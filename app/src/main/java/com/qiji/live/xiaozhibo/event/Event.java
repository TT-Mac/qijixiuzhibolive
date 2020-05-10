package com.qiji.live.xiaozhibo.event;

/**
 * Created by weipeng on 16/11/29.
 */

public class Event {
    /*
    * 关注弹窗状态修改事件
    * */
    public static class DialogFollow{
        public String uid;
        public int action;
    }

    /*
    *
    * 发送礼物请求结果回调事件
    * */
    public static class SendGift{
        public int result;
    }


    /*
    *
    * 用户金额发生改变回调事件
    * */
    public static class CoinChange{
        public String coin;
    }

    /*
    *
    * 发言回调
    *
    * */
    public static class SendMsg{
        public int errCode;
        public int action;
        public Object msg;
    }

    /*
    *
    * 系统消息
    * */
    public static class System{
        public int action;
    }

    /*
    *
    * 直播间弹窗状态改变
    * */
    public static class InputDialogStatusChange{
        public int status;
        public int y;
    }

    /*
    *
    * 微信支付结果回调
    * */
    public static class WXPayRep{
        public int code;
    }
}
