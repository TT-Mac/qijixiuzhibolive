package com.qiji.live.xiaozhibo.logic;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.AdminJson;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 2016/12/3.
 * 管理员列表
 */

public class ManageListMgr {

    public static final String TAG = TCChatRoomMgr.class.getSimpleName();

    private static class ManageListMgrHolder{
        private static ManageListMgr instance = new ManageListMgr();
    }

    public static ManageListMgr getInstance(){

        return ManageListMgrHolder.instance;
    }


    //请求回调接口
    private ManageListMgrCallback mManageListMgrCallback;


    public void setManageListMgrCallback(ManageListMgrCallback manageListMgrCallback) {
        mManageListMgrCallback = manageListMgrCallback;
    }

    /*
        *
        * 获取管理列表
        * */
    public void requestGetManageList(String liveuid){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<AdminJson>> call = apiStores.requestGetManageList(liveuid);

        call.enqueue(new Callback<ResponseJson<AdminJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<AdminJson>> call, Response<ResponseJson<AdminJson>> response) {

                if(AppClient.checkResult(response)){

                    if(mManageListMgrCallback == null)return;

                    mManageListMgrCallback.onSuccess(response.body().getData().getInfo());
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<AdminJson>> call, Throwable t) {

                if(mManageListMgrCallback == null)return;

                mManageListMgrCallback.onFail(-1,"获取管理员列表失败");
            }
        });
    }


    /*
    *
    * 获取管理员列表接口
    * */
    public interface ManageListMgrCallback{

        void onSuccess(List<AdminJson> adminlist);

        void onFail(int errorCode,String msg);


    }

}
