package com.qiji.live.xiaozhibo.logic;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/28.
 */

public class ProfitMgr {

    private static final String TAG = ProfitMgr.class.getSimpleName();

    private static class ProfitMgrHolder{

        private static ProfitMgr instance = new ProfitMgr();
    }

    public static ProfitMgr getInstance(){

        return ProfitMgrHolder.instance;
    }

    private ProfitCallback mProfitCallback;

    public void setProfitCallback(ProfitCallback profitCallback) {
        mProfitCallback = profitCallback;
    }

    public void removeCallback(){
        mProfitCallback = null;
    }

    /**
    * @dw 获取收益
    * @param uid 用户id
    * @param token
    * */
    public void requestGetProfitData(String uid,String token){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestGetProfit(uid,
                token);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {
                if(AppClient.checkResult(response)){
                    if(mProfitCallback == null)return;
                    mProfitCallback.success((LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0));
                }

            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {
                if(mProfitCallback == null)return;
                mProfitCallback.fail();
            }
        });
    }
    /**
    * @dw 提现
    * @param uid 用户id
    * @param token
    * */
    public void requestSubmitProfit(String uid,String token,final ProfitSubmitCallback callback){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestProfit(uid,
                token);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {
                if(AppClient.checkResult(response)){
                    if(callback == null)return;
                    callback.success();
                }

            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {
                if(callback == null)return;
                callback.fail();
            }
        });
    }

    /**
    * @dw 提现
    *
    * */

    public interface ProfitSubmitCallback{

        void success();

        void fail();
    }

    /**
    * @dw 获取提现数据接口
    *
    *
    * */
    public interface ProfitCallback{

        void success(LinkedTreeMap<String, String> linkedTreeMap);

        void fail();
    }

}
