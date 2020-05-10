package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.LiveRecordBean;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/23.
 */

public class LiveRecordListMgr {
    private final static String TAG = AttentionListMgr.class.getSimpleName();


    private static class LiveRecordListMgrHolder{
        static LiveRecordListMgr instance = new LiveRecordListMgr();
    }
    private LiveRecordListCallback mLiveRecordListCallback;


    public LiveRecordListMgr() {

    }

    public void setLiveRecordListCallback(@NonNull LiveRecordListCallback callback){
        this.mLiveRecordListCallback = callback;
    }

    /*
    *
    * 请求获取直播记录
    * */
    public void getLiveRecordList(String uid){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LiveRecordBean>> call = apiStores.requestLiveRecordList(uid,uid,"1");
        call.enqueue(new Callback<ResponseJson<LiveRecordBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<LiveRecordBean>> call, Response<ResponseJson<LiveRecordBean>> response) {
                if(AppClient.checkResult(response)){

                    if(mLiveRecordListCallback != null){
                        mLiveRecordListCallback.onSuccess(response.body().getData().getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LiveRecordBean>> call, Throwable t) {

                if(mLiveRecordListCallback != null)
                    mLiveRecordListCallback.onFailure(0,"获取粉丝列表失败");
            }
        });
    }


    public static LiveRecordListMgr getInstance(){
        return LiveRecordListMgrHolder.instance;
    }



    public interface LiveRecordListCallback{

          /*
        * 搜索成功
        * */

        void onSuccess(List<LiveRecordBean> list);

        /*
        * 搜索失败
        * */

        void onFailure(int code, String msg);
    }
}
