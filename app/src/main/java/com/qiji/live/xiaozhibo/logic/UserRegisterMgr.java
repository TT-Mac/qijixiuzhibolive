package com.qiji.live.xiaozhibo.logic;

import com.google.gson.Gson;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by weipeng on 16/11/16.
 */

public class UserRegisterMgr {
    public static final String TAG = TCLoginMgr.class.getSimpleName();


    private UserRegisterMgr() {
    }

    private static class UserRegisterMgrHolder {
        private static UserRegisterMgr instance = new UserRegisterMgr();
    }

    public static UserRegisterMgr getInstance() {
        return UserRegisterMgrHolder.instance;
    }
    private UserRegisterCallback mUserRegisterCallback;
    private UserSmsRegCallback mUserSmsRegCallback;

    public void setUserRegisterCallback(UserRegisterCallback tcRegisterCallback) {
        this.mUserRegisterCallback = tcRegisterCallback;
    }

    public void removeTCRegisterCallback() {
        this.mUserRegisterCallback = null;
        this.mUserSmsRegCallback = null;
    }

    /**
     * tlssms验证成功后调用完成注册流程
     * @param smsRegListener 注册结果监听
     */
    public void smsRegCommit(TLSSmsRegListener smsRegListener) {

    }

    /**
     * tlssmd登录 获取验证码
     * @param mobile 手机号（默认中国+86）
     */
    public void smsRegAskCode(String mobile, final UserSmsRegCallback ucSmsCallback) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<String>> call = apiStores.requestGetCode(mobile);
        call.enqueue(new Callback<ResponseJson<String>>() {
            @Override
            public void onResponse(Call<ResponseJson<String>> call, Response<ResponseJson<String>> response) {

                if(AppClient.checkResult(response)){
                    ucSmsCallback.onGetVerifyCode(30,60);
                }else{

                }
            }

            @Override
            public void onFailure(Call<ResponseJson<String>> call, Throwable t) {

            }
        });
    }

    /**
     * tls用户名注册
     * @param username 用户名
     * @param password 密码
     */
    public void pwdRegist(final String username, final String password,final String code) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<UserInfoBean>> call = apiStores.requestRegister(username,password,password,code);
        call.enqueue(new Callback<ResponseJson<UserInfoBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<UserInfoBean>> call, Response<ResponseJson<UserInfoBean>> response) {
                if(AppClient.checkResult(response)){
                    UserInfoBean u = (UserInfoBean) response.body().getData().getInfo().get(0);
                    TCApplication.getInstance().saveUserInfo(u);
                    mUserRegisterCallback.onSuccess(username);
                }else{
                    mUserRegisterCallback.onFailure(1,"注册失败");
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<UserInfoBean>> call, Throwable t) {
                mUserRegisterCallback.onFailure(400,t.getMessage());
            }
        });
    }

    /**
     * 注册回调，封装IM与TLS
     */
    public interface UserRegisterCallback {

        /**
         * 注册成功
         */
        void onSuccess(String username);

        /**
         * 注册失败
         * @param code 错误码
         * @param msg 错误信息
         */
        void onFailure(int code, String msg);

    }

    /**
     * 获取验证码回调（失败将调用TCRegisterCallback#onFailure）
     */
    public interface UserSmsRegCallback {

        void onGetVerifyCode(int reaskDuration, int expireDuration);
    }
}
