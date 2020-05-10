package com.qiji.live.xiaozhibo.logic;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.base.TCHttpEngine;
import com.qiji.live.xiaozhibo.bean.GiftJson;
import com.qiji.live.xiaozhibo.bean.MembersJson;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.SendGiftJson;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.tencent.rtmp.TXLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 播放端后台Mgr
 */
public class TCPlayerMgr {
    private static final String TAG = TCPlayerMgr.class.getSimpleName();

    public static final int TCLiveListItemType_Live = 0;
    public static final int TCLiveListItemType_Record = 1;
    public PlayerListener mPlayerListener;

    private TCPlayerMgr() {
    }




    private static class TCPlayerMgrHolder {
        private static TCPlayerMgr instance = new TCPlayerMgr();
    }

    public static TCPlayerMgr getInstance() {
        return TCPlayerMgrHolder.instance;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        mPlayerListener = playerListener;
    }

    /**
    * @dw 判断是否关注某人
    * @param uid 当前用户id
    * @param touid 第二者
    *
    * */
    public void requestIsAttention(String uid, String touid, final IsFollowListener listener){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestIsFollow(uid,touid);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {

                if(AppClient.checkResult(response)){
                    if(listener == null)return;
                    listener.onRequestCallback(1, (LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0));
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {

            }
        });
    }


    /**
     * @dw 赠送礼物
     * @param token 用户验证token
     * @param uid   用户id
     * @param liveuid 赠送用户id
     * @param giftJson 礼物信息
     * @param giftcount 礼物数量
     * @param groupid 分组id
     * */
    public void requestSendGift(String token,final String uid,final String liveuid,final GiftJson giftJson,String giftcount,String groupid,final OnSendGiftListener listent) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestSendGift(uid,token,liveuid, String.valueOf(giftJson.getId()),giftcount,groupid);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String,String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Response<ResponseJson<LinkedTreeMap<String,String>>> response) {
                if(AppClient.checkResult(response)){
                    if (listent != null) {
                        LinkedTreeMap<String,String> list = (LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0);

                        SendGiftJson sendGiftJson = new SendGiftJson();
                        sendGiftJson.setType(String.valueOf(giftJson.getType()));
                        sendGiftJson.setAvatar(UserInfoMgr.getInstance().getHeadPic());
                        sendGiftJson.setGiftid(giftJson.getId());
                        sendGiftJson.setEvensend(StringUtils.toInt(giftJson.getType()) == 1 ? "y" : "n");
                        sendGiftJson.setTotalcoin(giftJson.getNeedcoin());
                        sendGiftJson.setGiftname(giftJson.getGiftname());
                        sendGiftJson.setGiftcount(1);
                        sendGiftJson.setGifticon(giftJson.getGifticon());
                        sendGiftJson.setShowid(liveuid);
                        sendGiftJson.setSendTime(System.currentTimeMillis());
                        sendGiftJson.setTouid(StringUtils.toInt(liveuid,0));
                        sendGiftJson.setUid(StringUtils.toInt(uid));
                        sendGiftJson.setNicename(UserInfoMgr.getInstance().getNickname());

                        listent.onRequestCallback(1,sendGiftJson,list);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Throwable t) {

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
    public void requestSendDanmu(String uid, String token, String liveuid, String groupId, String s, String s1, final OnSendDanmuListener listener) {

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
     * 发送点赞信息
     * @param mUserId 主播ID
     */
    public void addHeartCount(String mUserId) {
        internalSendRequest(mUserId, 1, 0, 0, null);
    }

    /**
     * 后台发送请求接口
     * @param userId 用户id
     * @param type:0：修改观看数量 1：修改点赞数量
     * @param optype 0：增加 1：减少
     * @param flag 0：直播 1：点播
     * @param fileId 文件id，在点播情况下使用，直播情况填null
     */
    private void internalSendRequest(String userId, int type, int optype, int flag, String fileId) {
        try {
            final JSONObject req = new JSONObject();
            req.put("Action", "ChangeCount");
            req.put("userid", userId);
            req.put("type", type);
            req.put("optype", optype);
            req.put("flag", flag);
            req.put("fileid", fileId == null ? "" : fileId);

            if (type == 0 && optype == 1) {
                /*TCHttpEngine.getInstance().post(req, new TCHttpEngine.Listener() {
                    @Override
                    public void onResponse(int retCode, String retMsg, JSONObject retData) {
                        if (mPlayerListener != null) {
                            mPlayerListener.onRequestCallback(retCode);
                        }
                        mPlayerListener = null;
                    }
                });*/
            } else {

                AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
                Call<ResponseJson<Object>> call = apiStores.requestInternalSend(userId);
                call.enqueue(new Callback<ResponseJson<Object>>() {
                    @Override
                    public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                        if(AppClient.checkResult(response)){
                            if (mPlayerListener != null) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {

                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 举报接口
     * @param userId 举报人id
     * @param hostId 被举报人id
     */
    public void reportUser(String userId, String hostId) {
        try {
            JSONObject req = new JSONObject();
            req.put("Action", "ReportUser");
            req.put("userid", userId);
            req.put("hostuserid", hostId);

            TCHttpEngine.getInstance().post(req, new TCHttpEngine.Listener() {
                @Override
                public void onResponse(int retCode, String retMsg, JSONObject retData) {
                    TXLog.d(TAG, "ReportUser: retCode --" + retCode + "|" + retMsg);
                    if (mPlayerListener != null)
                        mPlayerListener.onRequestCallback(retCode);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param userId 用户ID
     * @param liveUserId 主播ID
     * @param groupId 群组ID
     * @param nickname 昵称
     * @param headPic 头像链接
     * @param flag 直播/点播 直播--0 点播--1
     */
    public void enterGroup(String userId ,String token,String liveUserId ,String groupId ,
                           String nickname ,String headPic, int flag) {

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestJoinRoom(userId,token,liveUserId,groupId,nickname,headPic);
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)){
                    TXLog.d(TAG, "EnterGroup: retCode --" + 1 + "|" + 1);
                    if (mPlayerListener != null) {
                        mPlayerListener.onRequestCallback(0);
                    }
                    mPlayerListener = null;
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {

            }
        });
    }

    /**
     * 退出群组
     * @param userId 用户ID
     * @param liveUserId 主播ID
     * @param groupId 群组ID
     * @param flag 直播/点播 直播--0 点播--1
     */
    public void quitGroup(String userId, String liveUserId, String groupId, int flag) {


        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestLeaveRoom(userId,UserInfoMgr.getInstance().getToken(),
                liveUserId,groupId);
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)) {
                    /*if (mPlayerListener != null)
                        mPlayerListener.onRequestCallback(retCode);*/
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {

            }
        });
    }

    /**
     * 拉取观众列表
     * @param liveUserId 主播ID
     * @param groupId 群组ID
     * @param pageNo 页码，从1开始
     * @param pageSize 页的大小
     * @param listener 回调
     */
    public void fetchGroupMembersList(String liveUserId, String groupId, int pageNo, int pageSize, final OnGetMembersListener listener) {

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<MembersJson>> call = apiStores.requestGetMembers(liveUserId,groupId,"1");
        call.enqueue(new Callback<ResponseJson<MembersJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<MembersJson>> call, Response<ResponseJson<MembersJson>> response) {
                if(AppClient.checkResult(response)){
                    if (listener != null) {
                        MembersJson membersJson = (MembersJson) response.body().getData().getInfo().get(0);
                        listener.onGetMembersList(0, StringUtils.toInt(membersJson.totalcount), membersJson.memberlist);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<MembersJson>> call, Throwable t) {
                if (listener != null) {
                    listener.onGetMembersList(1, -1, null);
                }
            }
        });


    }

    /*
    *
    * 获取礼物列表
    * */
    public void getGiftList(final OnGetGiftListListener listen) {
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<GiftJson>> call = apiStores.requestGiftList();
        call.enqueue(new Callback<ResponseJson<GiftJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<GiftJson>> call, Response<ResponseJson<GiftJson>> response) {
                if(AppClient.checkResult(response)){
                    if(listen != null){
                        listen.onGetGiftList(response.body().getData().getInfo());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseJson<GiftJson>> call, Throwable t) {

            }
        });
    }

    /*
    *
    * 礼物列表
    * */

    public interface OnGetGiftListListener{
        void onGetGiftList(List<GiftJson> list);
    }
    /**
     * 获取观看用户列表
     */
    public interface OnGetMembersListener {

        void onGetMembersList(int retCode, int totalCount, List<SimpleUserInfo> membersList);
    }

    /**
     * request回调
     */
    public interface PlayerListener {

        void onRequestCallback(int errCode);

    }

    /*
    * 赠送礼物
    *
    * */
    public interface OnSendGiftListener{


        void onRequestCallback(int errCode, SendGiftJson giftJson, LinkedTreeMap<String, String> res);
    }

    /*
    * 赠送弹幕
    *
    * */
    public interface OnSendDanmuListener{


        void onRequestCallback(int errCode, LinkedTreeMap<String, String> res);
    }



    /*
    * 是否关注回调
    *
    * */
    public interface IsFollowListener {

        void onRequestCallback(int errorCode, LinkedTreeMap<String, String> linkedTreeMap);
    }


}
