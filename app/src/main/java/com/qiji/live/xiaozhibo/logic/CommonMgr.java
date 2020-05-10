package com.qiji.live.xiaozhibo.logic;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 2016/12/9.
 * 公共常用接口
 */

public class CommonMgr {

    private static final String TAG = CommonMgr.class.getSimpleName();

    private static class ConfigMgrHolder{
        private static CommonMgr instance = new CommonMgr();
    }

    public static CommonMgr getInstance(){

        return ConfigMgrHolder.instance;
    }

    private ConfigMgrCallback mConfigMgrCallback;


    public void setConfigMgrCallback(ConfigMgrCallback configMgrCallback) {
        mConfigMgrCallback = configMgrCallback;
    }


    //请求获取服务单配置信息
    public void requestGetConfig(){


        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LinkedTreeMap<String, String>>> call = apiStores.requestGetConfig();

        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {

                if(AppClient.checkResult(response)){

                    if(mConfigMgrCallback == null)return;

                    mConfigMgrCallback.onSuccess((LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0));
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {

                if(mConfigMgrCallback == null)return;

                mConfigMgrCallback.onFail(1,"获取失败");
            }
        });
    }

    //获取房间基本信息
    public void requestGetRoomSimpleInfo(String uid, String liveuid, final SimpleActionListener listener){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LinkedTreeMap<String, String>>> call = apiStores.requestGetRoomBaseInfo(uid,liveuid);

        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {

                if(AppClient.checkResult(response)){

                    if(listener != null){
                        listener.onSuccess();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {
                if(listener != null){
                    listener.onFail(10,"未知错误,进入房间失败");
                }
            }
        });
    }


    /*
    * 获取服务端配置信息
    *
    * */

    public interface ConfigMgrCallback{

        //成功
        void onSuccess(LinkedTreeMap<String,String> config);


        //失败
        void onFail(int code,String msg);
    }
}
