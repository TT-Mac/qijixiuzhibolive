package com.qiji.live.xiaozhibo.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.logic.CommonMgr;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ShareUtils {


    public static void share(Context context,int id,UserInfoBean user){

        String content = "云豹直播";
        switch (id){
            case R.id.ll_live_shar_qq:
                share(context,QQ.NAME,content,user,null);
                break;
            case R.id.ll_live_shar_pyq:
                share(context,WechatMoments.NAME,content,user,null);
                break;
            case R.id.ll_live_shar_qqzone:
                share(context,QZone.NAME,content,user,null);
                break;
            case R.id.ll_live_shar_sinna:
                share(context,SinaWeibo.NAME,content,user,null);
                break;
            case R.id.ll_live_shar_wechat:
                share(context,Wechat.NAME,content,user,null);
                break;
        }
    }

    /**
    * @dw 分享
    * @param context 上下文
    * @param name 分享平台 名称
    * @param user
    * */
    public static void share2(Context context,String name,UserInfoBean user,PlatformActionListener listener){

        String content = "云豹直播";
        share(context,name,content,user,listener);
    }

    public static void share(final Context context,final String name,final String content,final UserInfoBean user,final PlatformActionListener listener) {


        CommonMgr configMgr = CommonMgr.getInstance();


        //请求获取分享连接
        configMgr.setConfigMgrCallback(new CommonMgr.ConfigMgrCallback() {
            @Override
            public void onSuccess(LinkedTreeMap<String, String> config) {

                ShareSDK.initSDK(context);
                OnekeyShare oks = new OnekeyShare();
                oks.setSilent(true);
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                oks.setPlatform(name);
                // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
                //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(config.get("share_title"));
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用

                // text是分享文本，所有平台都需要这个字段
                oks.setText(name + config.get("share_des"));

                oks.setImageUrl(user.getAvatar_thumb());

                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                if (name.equals(Wechat.NAME) || name.equals(WechatMoments.NAME)) {
                    oks.setUrl(TCConstants.SVR_POST_URL + user.getId());
                    oks.setSiteUrl(TCConstants.SVR_POST_URL  + user.getId());
                    oks.setTitleUrl(TCConstants.SVR_POST_URL  + user.getId());
                } else {
                    oks.setUrl(config.get("app_android"));
                    oks.setSiteUrl(config.get("app_android"));
                    oks.setTitleUrl(config.get("app_android"));
                }

                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                //oks.setComment(context.getString(R.string.shartitle));
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(config.get("site_name"));
                oks.setCallback(listener);
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用

                // 启动分享GUI
                oks.show(context);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });

        configMgr.requestGetConfig();


    }
    //分享pop弹窗
    public static void showSharePopWindow(Context context,View v) {

        View view = LayoutInflater.from(context).inflate(R.layout.pop_view_share,null);
        PopupWindow p = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        p.setBackgroundDrawable(new BitmapDrawable());
        p.setOutsideTouchable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        p.showAtLocation(v, Gravity.NO_GRAVITY,location[0] + v.getWidth()/2 - view.getMeasuredWidth()/2, (int) (TDevice.getScreenHeight() - view.getMeasuredHeight()));

    }
}
