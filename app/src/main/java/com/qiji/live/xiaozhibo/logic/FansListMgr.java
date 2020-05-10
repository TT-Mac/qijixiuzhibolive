package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/23.
 */

public class FansListMgr {

    private final static String TAG = FansListMgr.class.getSimpleName();


    private static class FansListMgrHolder{
        static FansListMgr instance = new FansListMgr();
    }
    private FansCallback mFansCallback;


    public FansListMgr() {

    }

    public void setFansCallback(@NonNull FansCallback callback){
        this.mFansCallback = callback;
    }

    /*
    *
    * 请求获取粉丝
    * */
    public void getFans(String uid){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<GlobalUserBean>> call = apiStores.requestFansList(uid,uid,"1");
        call.enqueue(new Callback<ResponseJson<GlobalUserBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<GlobalUserBean>> call, Response<ResponseJson<GlobalUserBean>> response) {
                if(AppClient.checkResult(response)){

                    if(mFansCallback != null){
                        mFansCallback.onSuccess(response.body().getData().getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<GlobalUserBean>> call, Throwable t) {

                if(mFansCallback != null)
                    mFansCallback.onFailure(0,"获取粉丝列表失败");
            }
        });
    }


    public static FansListMgr getInstance(){
        return FansListMgrHolder.instance;
    }



    public interface FansCallback{

          /*
        * 搜索成功
        * */

        void onSuccess(List<GlobalUserBean> list);

        /*
        * 搜索失败
        * */

        void onFailure(int code, String msg);
    }
}
