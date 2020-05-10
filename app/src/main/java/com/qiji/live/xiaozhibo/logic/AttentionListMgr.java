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

public class AttentionListMgr {
    private final static String TAG = AttentionListMgr.class.getSimpleName();


    private static class AttentionListMgrHolder{
        static AttentionListMgr instance = new AttentionListMgr();
    }
    private AttentionCallback mAttentionCallback;


    public AttentionListMgr() {

    }

    public void setAttentionCallback(@NonNull AttentionCallback callback){
        this.mAttentionCallback = callback;
    }

    /*
    *
    * 请求获取关注列表
    * */
    public void getAttentionList(String uid){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<GlobalUserBean>> call = apiStores.requestAttentionList(uid,uid,"1");
        call.enqueue(new Callback<ResponseJson<GlobalUserBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<GlobalUserBean>> call, Response<ResponseJson<GlobalUserBean>> response) {
                if(AppClient.checkResult(response)){

                    if(mAttentionCallback != null){
                        mAttentionCallback.onSuccess(response.body().getData().getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<GlobalUserBean>> call, Throwable t) {

                if(mAttentionCallback != null)
                    mAttentionCallback.onFailure(0,"获取粉丝列表失败");
            }
        });
    }


    public static AttentionListMgr getInstance(){
        return AttentionListMgrHolder.instance;
    }



    public interface AttentionCallback{

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
