package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/19.
 * 我的钻石
 */

public class UserDiamondsMgr {


    private static String TAG = UserDiamondsMgr.class.getSimpleName();

    private static class UserDiamondsMgrHolder{
        private static UserDiamondsMgr instance = new UserDiamondsMgr();
    }

    public static UserDiamondsMgr getInstance(){
        return UserDiamondsMgrHolder.instance;
    }

    private UserDiamondsCallback mUserDiamondsCallback;

    public void setUserDiamondsCallback(@NonNull UserDiamondsCallback callback){
        this.mUserDiamondsCallback = callback;
    }


    /*
    * 获取我的钻石余额
    * */
    public void getDiamondsCount(@NonNull String uid,@NonNull String token){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestCoinCount(uid,token);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String,String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Response<ResponseJson<LinkedTreeMap<String,String>>> response) {
                if(AppClient.checkResult(response)){
                    if(mUserDiamondsCallback == null)
                        return;

                    mUserDiamondsCallback.onSuccess(
                            ((LinkedTreeMap<String,String>)response.body().getData().getInfo().get(0)));
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Throwable t) {
                if(mUserDiamondsCallback == null)
                    return;
                mUserDiamondsCallback.onFailure(0,"获取金币失败");
            }
        });
    }

    /*
    * 支付宝下单
    *
    * */
    public void requestAliPayOrderId(String uid, String money, final AliPayCallback callback){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestGetAliPayOrderId(uid,money);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {

                if(AppClient.checkResult(response)){
                    if(callback == null)return;
                    LinkedTreeMap<String,String> res = (LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0);
                    callback.onSuccess(res.get("orderid"));
                }

            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {
                if(callback == null)return;
                callback.onFailure(0,"获取订单号失败");
            }
        });

    }


    /**
     * Login回调
     */
    public interface UserDiamondsCallback {

        /**
         * 获取成功
         */
        void onSuccess(LinkedTreeMap<String,String> coin);

        /**
         * 登录失败
         * @param code 错误码
         * @param msg 错误信息
         */
        void onFailure(int code, String msg);

    }

    /*
    *
    * 支付宝下单回调
    * */
    public interface AliPayCallback{
        /*
        * 成功
        * */
        void onSuccess(String orderId);

        /*
        * 获取失败
        * */
        void onFailure(int code, String msg);
    }
}
