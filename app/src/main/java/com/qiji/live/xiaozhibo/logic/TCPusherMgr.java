package com.qiji.live.xiaozhibo.logic;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.base.TCHttpEngine;
import com.qiji.live.xiaozhibo.bean.PushUrl;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TCPusherMgr {

    public static final int TCLiveStatus_Online                = 1;
    public static final int TCLiveStatus_Offline               = 0;

    private static final String TAG = TCPusherMgr.class.getSimpleName();
    public PusherListener mPusherListener;

    private TCPusherMgr() {

    }

    private static class TCPusherMgrHolder {
        private static TCPusherMgr instance = new TCPusherMgr();
    }

    public static TCPusherMgr getInstance() {
        return TCPusherMgrHolder.instance;
    }

    public void setPusherListener(PusherListener pusherListener) {
        mPusherListener = pusherListener;
    }


    /**
     * 更改直播状态
     * @param userId 主播ID
     * @param status 状态 TCLiveStatus_Online = 0; TCLiveStatus_Offline = 1;

     */
    public void changeLiveStatus(String userId,String token, int status) {

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestChangState(userId,token, String.valueOf(status));
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)){
                    if (null != mPusherListener) {
                        mPusherListener.onChangeLiveStatus(1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if (null != mPusherListener) {
                    mPusherListener.onChangeLiveStatus(-1);
                }
            }
        });



    }

    /**
     * @dw 发送弹幕
     * @param uid 用户Id
     * @param token
     * @param liveuid 主播id
     * @param groupId 房间号码
     * */
    public void requestSendDanmu(String uid, String token, String liveuid, String groupId, String s, String s1, final TCPlayerMgr.OnSendDanmuListener listener) {

        AppClient.ApiStores apiStores =  AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestDanmu(uid,token,liveuid,s,s1,groupId);

        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {
                if(AppClient.checkResult(response)){
                    if(listener != null){
                        listener.onRequestCallback(0, (LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {
                if(listener != null){
                    listener.onRequestCallback(-1,null);
                }
            }
        });

    }

    /**
     * 获取推流url
     * @param userId 主播ID
     * @param groupId 群组ID
     * @param title 直播标题
     * @param coverPic 直播封面
     * @param nickName 主播昵称
     * @param headPic 主播头像
     * @param location 主播地理位置
     */
    public void getPusherUrl(final String userId,final String token, final String groupId, final String title,
                                     final String coverPic, final String nickName, final String headPic, final String location,String thumb) {
        try {


            AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
            Call<ResponseJson<PushUrl>> call = apiStores.requestCreateRoom(userId,token,nickName,coverPic,title,groupId,location,"","",thumb);
            call.enqueue(new Callback<ResponseJson<PushUrl>>() {
                @Override
                public void onResponse(Call<ResponseJson<PushUrl>> call, Response<ResponseJson<PushUrl>> response) {
                    if(AppClient.checkResult(response)){

                        if (null != mPusherListener)
                            mPusherListener.onGetPusherUrl(0, groupId, ((PushUrl)response.body().getData().getInfo().get(0)).push_url);
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson<PushUrl>> call, Throwable t) {
                    if(mPusherListener != null){
                        mPusherListener.onGetPusherUrl(1, groupId,"获取推流地址失败");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (null != mPusherListener)
                mPusherListener.onGetPusherUrl(-1, groupId, null);
        }


    }



    public interface PusherListener {

        /**
        *   创建群组失败时，groupId，pusherUrl为null；
        *   创建群组成功，拉取推流URL失败时，groupId为正常值，pusherUrl为null或空string；
        **/
        void onGetPusherUrl(int errCode, String groupId, String pusherUrl);

        void onChangeLiveStatus(int errCode);
    }

}
