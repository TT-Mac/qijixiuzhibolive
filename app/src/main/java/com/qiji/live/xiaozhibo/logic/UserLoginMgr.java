package com.qiji.live.xiaozhibo.logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.tencent.rtmp.TXLog;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by weipeng on 16/11/17.
 */

public class UserLoginMgr {
    public static final String TAG = UserLoginMgr.class.getSimpleName();

    public UserInfoBean getLastUserInfo() {
        return UserInfoMgr.getInstance().getUserInfoBean();
    }


    private static class UserLoginMgrHolder {
        private static UserLoginMgr instance = new UserLoginMgr();
    }

    public static UserLoginMgr getInstance() {
        //return UserLoginMgr.UserLoginMgrHolder.instance;
        return new UserLoginMgr();
    }

    //登录回调
    private UserLoginCallback mUserLoginCallback;

    /**
     * Login回调
     */
    public interface UserLoginCallback {

        /**
         * 登录成功
         */
        void onSuccess();

        /**
         * 登录失败
         * @param code 错误码
         * @param msg 错误信息
         */
        void onFailure(int code, String msg);

    }

    public void setUserLoginCallback(@NonNull UserLoginCallback tcLoginCallback) {
        this.mUserLoginCallback = tcLoginCallback;
    }

    public void removeTCLoginCallback() {
        this.mUserLoginCallback = null;
    }

    /**
     * tls用户名登录
     * @param username 用户名
     * @param password 密码
     */
    public void pwdLogin(String username, String password) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<UserInfoBean>> call = apiStores.requestLogin(username,password);
        call.enqueue(new Callback<ResponseJson<UserInfoBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<UserInfoBean>> call, Response<ResponseJson<UserInfoBean>> response) {
                if(AppClient.checkResult(response)){
                    UserInfoBean u = (UserInfoBean) response.body().getData().getInfo().get(0);
                    TCApplication.getInstance().saveUserInfo(u);
                    UserInfoMgr.getInstance().updateUserInfo();
                    imLogin(u.getId(),u.getToken());

                }
            }

            @Override
            public void onFailure(Call<ResponseJson<UserInfoBean>> call, Throwable t) {
                mUserLoginCallback.onFailure(400,"登录失败，请稍后再试");
            }
        });

    }

    //调用三方登录客户端
    public void otherLogin(Context context, String type, PlatformActionListener listener) {
        ShareSDK.initSDK(context);
        Platform weibo = ShareSDK.getPlatform(type);
        weibo.SSOSetting(false);  //设置false表示使用SSO授权方式
        weibo.setPlatformActionListener(listener); // 设置分享事件回调

        //weibo.authorize();//单独授权
        weibo.showUser(null);//授权并获取用户信息
        //移除授权
        weibo.removeAccount(true);
    }

    //发送三方登录数据到服务端
    public void otherLoginRequestService(String openid,String type,String nicename,String avatar){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<UserInfoBean>> call = apiStores.requestOtherLogin(openid,type,nicename,avatar);
        call.enqueue(new Callback<ResponseJson<UserInfoBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<UserInfoBean>> call, Response<ResponseJson<UserInfoBean>> response) {
                if(AppClient.checkResult(response)){
                    UserInfoBean u = (UserInfoBean) response.body().getData().getInfo().get(0);
                    TCApplication.getInstance().saveUserInfo(u);
                    UserInfoMgr.getInstance().updateUserInfo();
                    imLogin(u.getId(),u.getToken());

                }
            }

            @Override
            public void onFailure(Call<ResponseJson<UserInfoBean>> call, Throwable t) {
                mUserLoginCallback.onFailure(0,"登录失败，请稍后再试");
            }
        });

    }

    /**
     * imsdk登录接口，与tls登录验证成功后调用
     * @param identify 用户id
     * @param userSig 用户签名（托管模式下由TLSSDK生成 独立模式下由开发者在IMSDK云通信后台确定加密秘钥）
     */
    private void imLogin(@NonNull String identify,@NonNull String userSig) {
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(TCConstants.IMSDK_ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(TCConstants.IMSDK_APPID));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(TCConstants.IMSDK_APPID, user, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if(null != mUserLoginCallback)
                    mUserLoginCallback.onFailure(i, s);
            }

            @Override
            public void onSuccess() {
                if(null != mUserLoginCallback)
                    mUserLoginCallback.onSuccess();
            }
        });
    }
    /**
     * 检查是否存在缓存，若存在则登录，反之回调onFailure
     */
    public boolean checkCacheAndLogin() {
        if (needLogin()) {
            return false;
        } else {
            imLogin(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken());
        }
        return true;
    }

    /**
     * 检测是否需要执行登录操作
     * @return false不需要登录/true需要登录
     */
    public boolean needLogin() {

        return !TCApplication.getInstance().isLogin();
    }
    /**
     * imsdk登出
     */
    private void imLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                TXLog.e(TAG, "IMLogout fail ：" + i + " msg " + s);
            }

            @Override
            public void onSuccess() {
                //sendBroadcast(new Intent(TCConstants.EXIT_APP));
                Log.i(TAG, "IMLogout succ !");
            }
        });

    }

    /**
     * 登出
     */
    public void logout() {

        imLogout();
        TCApplication.getInstance().cleanLoginInfo();
    }
}
