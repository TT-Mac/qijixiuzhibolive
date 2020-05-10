package com.qiji.live.xiaozhibo.logic;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ConversationUserJson;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.chat.event.MessageEvent;
import com.qiji.live.xiaozhibo.chat.event.RefreshEvent;
import com.qiji.live.xiaozhibo.utils.TLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 2016/12/5.
 */

public class PrivateChatMgr{


    private static class PrivateChatMgrHolder{
        private static PrivateChatMgr instance = new PrivateChatMgr();
    }

    public static PrivateChatMgr getInstance(){
        return PrivateChatMgrHolder.instance;
    }

    public PrivateChatMgr(){

    }


    /*
    * 对会话列表分类
    * */
    public void requestConversationClass(String uid, String uids, String type, final OnConversationCallback callback){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<ConversationUserJson>> call = apiStores.requestConversationClass(uid,uids,type);

        call.enqueue(new Callback<ResponseJson<ConversationUserJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<ConversationUserJson>> call, Response<ResponseJson<ConversationUserJson>> response) {
                if(AppClient.checkResult(response)){
                    if(callback == null)return;

                    callback.onSuccess(response.body().getData().getInfo());
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<ConversationUserJson>> call, Throwable t) {
                if(callback == null)return;

                callback.onFail();
            }
        });
    }

    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id 会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id){
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(type, id);
    }



    /*
    * 获取分类会话列表用户信息
    *
    * */
    public interface OnConversationCallback{

        void onSuccess(List<ConversationUserJson> conversationUserJsonList);

        void onFail();
    }

}
