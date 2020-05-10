package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserDialogInfoJson;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 直播间用户弹窗
 */

public class UserInfoDialogMgr {
    private String TAG = UserInfoDialogMgr.class.getSimpleName();

    private static class UserInfoDialogMgrHolder{
        private static UserInfoDialogMgr instance = new UserInfoDialogMgr();
    }

    public static UserInfoDialogMgr getInstance(){

        return UserInfoDialogMgrHolder.instance;
    }

    private UserInfoDialogCallback mUserInfoDialogCallback;


    public void setUserInfoDialogCallback(@NonNull UserInfoDialogCallback userInfoDialogCallback) {
        mUserInfoDialogCallback = userInfoDialogCallback;
    }


    /*
    *
    * 获取弹窗用户id
    * */
    public void requestGetUserInfo(String uid,String touid,String liveid){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<UserDialogInfoJson>> call = apiStores.requestGetDialogInfo(uid,touid,liveid);
        call.enqueue(new Callback<ResponseJson<UserDialogInfoJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<UserDialogInfoJson>> call, Response<ResponseJson<UserDialogInfoJson>> response) {
                if(AppClient.checkResult(response)){
                    if(mUserInfoDialogCallback == null)return;

                    mUserInfoDialogCallback.onSuccess((UserDialogInfoJson) response.body().getData().getInfo().get(0));
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<UserDialogInfoJson>> call, Throwable t) {

            }
        });
    }

    /**
    * @dw 举报
    * @param uid 用户id
    * @param touid 举报人id
    * @param token
    * @param content 举报内容
    * */
    public void requestReport(String uid, String touid,String token, String content, final SimpleActionListener listener){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<Object>> call = apiStores.requestReport(uid,touid,token,content);
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {

                if(AppClient.checkResult(response)){
                    if(listener == null)return;
                    listener.onSuccess();
                }

            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if(listener == null)return;
                listener.onFail(1,"举报失败");
            }
        });

    }


    /**
     * @dw 设置管理员
     * @param uid
     * @param token
     * @param liveuid 主播id
     * @param touid 设置为管理员的用户
     * */
    public void requestSetManage(String uid,String token,String liveuid,String touid,final SimpleActionListener listener){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<Object>> call = apiStores.requestSetManage(uid,token,liveuid,touid);

        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)){
                    if(listener == null)return;
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {

                if(listener == null)return;
                listener.onFail(1,"设置管理员失败");
            }
        });


    }

    /*
    *
    * 获取弹窗用户信息回调
    * */

    public interface UserInfoDialogCallback{

        void onSuccess(UserDialogInfoJson userInfo);

        void onFail(int error, String msg);
    }



}
