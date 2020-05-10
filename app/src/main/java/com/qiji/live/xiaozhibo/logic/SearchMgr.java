package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.widget.GlobalUserItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/19.
 */

/*
* 搜索模块
*/
public class SearchMgr {
    public static final String TAG = SearchMgr.class.getSimpleName();

    public SearchMgr() {
    }

    public static class SearchMgrHolder{
        private static SearchMgr instance = new SearchMgr();
    }

    public static SearchMgr getInstance(){
        return SearchMgrHolder.instance;
    }

    private SearchCallback mSearchCallback;

    public void setSearchCallback(@NonNull SearchCallback searchCallback){
        this.mSearchCallback = searchCallback;
    }

    public boolean checkKeyWord(String key){
        if(TextUtils.isEmpty(key)){
            return false;
        }else{
            return true;
        }
    }


    public void searchUser(String keyword){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<GlobalUserBean>> call = apiStores.requestSearchUser(UserInfoMgr.getInstance().getUserInfoBean().getId(),keyword,"1");
        call.enqueue(new Callback<ResponseJson<GlobalUserBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<GlobalUserBean>> call, Response<ResponseJson<GlobalUserBean>> response) {
                if(AppClient.checkResult(response)){

                    if(mSearchCallback != null){
                        mSearchCallback.onSuccess(response.body().getData().getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<GlobalUserBean>> call, Throwable t) {

                if(mSearchCallback != null)
                    mSearchCallback.onFailure(0,"搜索失败");
            }
        });
    }

    //搜索回调

    public interface SearchCallback{

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
