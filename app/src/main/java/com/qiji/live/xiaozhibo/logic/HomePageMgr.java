package com.qiji.live.xiaozhibo.logic;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.HomePageJson;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/30.
 * 用户个人主页页面
 */

public class HomePageMgr {
    private static final String TAG = HomePageMgr.class.getSimpleName();


    private static class HomePageMgrHolder{
        private static HomePageMgr instance = new HomePageMgr();
    }

    public static HomePageMgr getInstance(){
        return HomePageMgrHolder.instance;
    }

    private HomePageCallback mHomePageCallback;


    /*
    * 设置请求信息回调接口
    * */
    public void setHomePageCallback(HomePageCallback homePageCallback) {
        mHomePageCallback = homePageCallback;
    }

    /*
    *
    * 请求获取个人主页信息
    * */

    public void getHomePageInfo(String uid,String touid){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<HomePageJson>> call = apiStores.requestGetHomePageInfo(uid,touid);

        call.enqueue(new Callback<ResponseJson<HomePageJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<HomePageJson>> call, Response<ResponseJson<HomePageJson>> response) {
                if(AppClient.checkResult(response)){

                    if(mHomePageCallback == null)return;
                    mHomePageCallback.onSuccess((HomePageJson) response.body().getData().getInfo().get(0));

                }
            }

            @Override
            public void onFailure(Call<ResponseJson<HomePageJson>> call, Throwable t) {
                if(mHomePageCallback == null)return;
                mHomePageCallback.onFail("获取信息失败");
            }
        });
    }

    /*
    * 拉黑
    * @param uid 当前用户id
    * @param touid 拉黑用户id
    * */
    public void requestBlack(String uid, String touid, final SimpleActionListener listener){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<HomePageJson>> call = apiStores.requestAddBlackUser(uid,touid);

        call.enqueue(new Callback<ResponseJson<HomePageJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<HomePageJson>> call, Response<ResponseJson<HomePageJson>> response) {
                if(AppClient.checkResult(response)){

                    if(listener == null)return;
                    listener.onSuccess();

                }
            }

            @Override
            public void onFailure(Call<ResponseJson<HomePageJson>> call, Throwable t) {
                if(listener == null)return;
                listener.onFail(0,"获取信息失败");
            }
        });
    }


    /*
    * 请求当前页面信息接口回调
    *
    * */
    public interface HomePageCallback{

        /*
        * 请求获取个人信息成功
        * */
        public void onSuccess(HomePageJson homePageJson);

        /*
       * 请求获取个人信息失败
       * */
        public void onFail(String msg);
    }
}
