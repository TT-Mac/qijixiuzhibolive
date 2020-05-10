package com.qiji.live.xiaozhibo.logic;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/18.
 */

public class UserRetrievePasswordMgr {

    public static final String TAG = UserRetrievePasswordMgr.class.getSimpleName();

    public UserRetrievePasswordMgr() {
    }

    private static class UserRetrievePasswordMgrHolder{
        private static UserRetrievePasswordMgr instance = new UserRetrievePasswordMgr();

    }

    public static UserRetrievePasswordMgr getInstance(){

        return UserRetrievePasswordMgrHolder.instance;
    }

    private UserRetrievePasswordCallback mUserRetrievePasswordCallback;
    private UserSmsRPCallback mUserSmsRPCallback;

    public void setUserRetrievePasswordCallback(UserRetrievePasswordCallback userRetrievePasswordCallback) {
        mUserRetrievePasswordCallback = userRetrievePasswordCallback;
    }

    public void setUserSmsRPCallback(UserSmsRPCallback userSmsRPCallback) {
        mUserSmsRPCallback = userSmsRPCallback;
    }

    public void removeTCRPCallback() {
        this.mUserRetrievePasswordCallback = null;
        this.mUserSmsRPCallback = null;
    }

    /**
     * tlssmd登录 获取验证码
     * @param mobile 手机号（默认中国+86）
     */
    public void smsRegAskCode(String mobile, final UserRegisterMgr.UserSmsRegCallback ucSmsCallback) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<String>> call = apiStores.requestGetCodeRP(mobile);
        call.enqueue(new Callback<ResponseJson<String>>() {
            @Override
            public void onResponse(Call<ResponseJson<String>> call, Response<ResponseJson<String>> response) {

                if(AppClient.checkResult(response)){
                    ucSmsCallback.onGetVerifyCode(30,60);
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<String>> call, Throwable t) {

            }
        });
    }

    /**
     * 用户找回密码
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     */

    public void pwdRetrieve(final String username, String password, String password2,String code){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestChangePass(username,password,password2,code);
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)){
                    mUserRetrievePasswordCallback.onSuccess(username);
                }else{
                    mUserRetrievePasswordCallback.onFailure(0,"");
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                mUserRetrievePasswordCallback.onFailure(1,t.getMessage());
            }
        });
    }




    /**
     * 找回密码回调，封装IM与TLS
     */
    public interface UserRetrievePasswordCallback {

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
    public interface UserSmsRPCallback {

        void onGetVerifyCode(int reaskDuration, int expireDuration);
    }
}
