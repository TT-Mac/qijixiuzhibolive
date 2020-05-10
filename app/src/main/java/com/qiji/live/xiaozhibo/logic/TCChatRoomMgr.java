package com.qiji.live.xiaozhibo.logic;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.tencent.rtmp.TXLog;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.utils.TLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by teckjiang on 2016/8/4
 */
public class TCChatRoomMgr implements TIMMessageListener {

    public static final String TAG = TCChatRoomMgr.class.getSimpleName();
    private TIMConversation mGroupConversation;
    private TCChatRoomListener mTCChatRoomListener;
    private String mRoomId;
    private static String mUserId;

    private TCChatRoomMgr() {
    }

    private static class TCChatRoomMgrHolder {
        private static TCChatRoomMgr instance = new TCChatRoomMgr();
    }

    public static TCChatRoomMgr getInstance() {
        mUserId = TCLoginMgr.getInstance().getLastUserInfo().identifier;
        return TCChatRoomMgrHolder.instance;
    }

    public void sendPraiseMessage(long count) {
        sendMessage(TCConstants.IMCMD_PRAISE,String.valueOf(count));

    }

    public void sendGiftMessage(String msg){
        try {
            JSONObject object = new JSONObject(msg);
            sendMessage(TCConstants.IMCMD_GIFT,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //禁言
    public void sendShutUpMessage(String uid,String uname){

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid",uid);
            obj.put("name",uname);
            sendMessage(TCConstants.IMCMD_SHUT_UP,obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //设置管理员取消管理
    public void sendSetCancelManage(String uid,String uname,boolean action){

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid",uid);
            obj.put("name",uname);
            if(action){
                sendMessage(TCConstants.IMCMD_SET_MANAGE, obj);
            }else{
                sendMessage(TCConstants.IMCMD_SET_MANAGE_CANCEL, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void sendDanmuMessage(String msg) {
        sendMessage(TCConstants.IMCMD_DANMU, msg);
    }

    public void sendTextMessage(String msg) {
        sendMessage(TCConstants.IMCMD_PAILN_TEXT, msg);
    }

    //发送通知消息
    public void sendNotifyMessage(String msg){
        sendMessage(TCConstants.IMCMD_NOTIFY, msg);
    }

    /**
     * 发送消息
     *
     * @param cmd   控制符（代表不同的消息类型）具体查看TCContants.IMCMD开头变量
     * @param param 参数
     */
    private void sendMessage(final int cmd, final Object param) {

        sendMessage(cmd, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                TXLog.d("send cmd ", "error:" + s);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(-1, null);

                if(cmd == TCConstants.IMCMD_PAILN_TEXT){
                    Event.SendMsg msg = new Event.SendMsg();
                    msg.errCode = -1;
                    msg.action = cmd;
                    msg.msg = param;
                    EventBus.getDefault().post(msg);
                }

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0, timMessage);

                if(cmd == TCConstants.IMCMD_PAILN_TEXT){
                    Event.SendMsg msg = new Event.SendMsg();
                    msg.errCode = 0;
                    msg.action = cmd;
                    msg.msg = param;
                    EventBus.getDefault().post(msg);
                }

            }
        });
    }

    /**
     * @dw 获取直播间收入
     * @param liveuid 直播间主播id
     *
     * */
    public void requestGetIncome(String liveuid,final OnGetIncomeListener listener){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestGetIncome(liveuid);
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
     * 观看者退出退出群组接口
     *
     * @param roomId 群组Id
     */
    public void quitGroup(final String roomId) {

        sendMessage(TCConstants.IMCMD_EXIT_LIVE, "");

        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "quitGroup failed, code:" + i + ",msg:" + s);
//                    if(null != mTCChatRoomListener) {
//                        mTCChatRoomListener.onQuitGroupCallback(i, s);
//                    }
                mTCChatRoomListener = null;
                mGroupConversation = null;
                //TIMManager.getInstance().deleteConversation(TIMConversationType.Group, roomId);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "quitGroup success, groupid:" + roomId);
                mTCChatRoomListener = null;
                mGroupConversation = null;
            }
        });
    }

    /**
     * 主播创建直播群组接口
     */
    public void createGroup() {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new UserLoginMgr.UserLoginCallback() {
            @Override
            public void onSuccess() {
                TCLoginMgr.getInstance().removeTCLoginCallback();
                //用户登录，创建直播间
                TIMGroupManager.getInstance().createAVChatroomGroup("TVShow", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.d(TAG, "create av group failed. code: " + code + " errmsg: " + msg);

                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(code, msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        Log.d(TAG, "create av group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(0, roomId);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                TCLoginMgr.getInstance().removeTCLoginCallback();
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(TCConstants.NO_LOGIN_CACHE, "no login cache, user has been kicked out");
            }
        });

    }

    /**
     * 登录态检测
     * 在进行createRoom/joinGroup操作时检测用户是否处于登录态，若不处于登录态执行login操作
     * @param tcLoginCallback 登录回调
     */
    private void checkLoginState(UserLoginMgr.UserLoginCallback tcLoginCallback) {

        UserLoginMgr tcLoginMgr = UserLoginMgr.getInstance();
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            tcLoginMgr.setUserLoginCallback(tcLoginCallback);
            tcLoginMgr.checkCacheAndLogin();
        } else {
            //已经处于登录态直接进行回调
            if (null != tcLoginCallback)
                tcLoginCallback.onSuccess();
        }
    }


    /**
     * 主播端退出前删除直播群组
     */
    public void deleteGroup() {

        sendMessage(TCConstants.IMCMD_EXIT_LIVE, "");

        if (mRoomId == null)
            return;

        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "delete av group failed. code: " + code + " errmsg: " + msg);
//                if (null != mTCChatRoomListener)
//                    mTCChatRoomListener.onQuitGroupCallback(code, msg);
                mRoomId = null;
                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "delete av group succ. groupid: " + mRoomId);
//                if (null != mTCChatRoomListener)
//                    mTCChatRoomListener.onQuitGroupCallback(0, mRoomId);
                mRoomId = null;
                mTCChatRoomListener = null;
            }
        });
    }

    /**
     * 加入群组
     *
     * @param roomId 群组ID
     */
    public void joinGroup(final String roomId) {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new UserLoginMgr.UserLoginCallback() {
            @Override
            public void onSuccess() {
                TCLoginMgr.getInstance().removeTCLoginCallback();
                //用户登录，加入房间
                mRoomId = roomId;
                TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "joingroup failed, code:" + i + ",msg:" + s);
                        mRoomId = null;
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(i, s);
                        else
                            Log.d(TAG, "mPlayerListener not init");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "joingroup success, groupid:" + roomId);
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                        //加入群组成功后，发送加入消息
                        sendMessage(TCConstants.IMCMD_ENTER_LIVE, "");
                        if (null != mTCChatRoomListener) {
                            mTCChatRoomListener.onJoinGroupCallback(0, roomId);
                        } else {
                            Log.d(TAG, "mPlayerListener not init");
                        }
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d(TAG, "relogin fail. code: " + code + " errmsg: " + msg);
                TCLoginMgr.getInstance().removeTCLoginCallback();
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(TCConstants.NO_LOGIN_CACHE, "no login cache, user has been kicked out");

            }
        });

    }

    /**
     * IMSDK消息回调接口
     *
     * @param list 消息列表
     * @return 默认情况下所有消息监听器都将按添加顺序被回调一次
     * 除非用户在OnNewMessages回调中返回true
     * 此时将不再继续回调下一个消息监听器，此处默认返回false
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        parseIMMessage(list);
        return false;
    }


    /**
     * 注入消息回调监听类
     * 需要实现并注入TCChatRoomListener才能获取相应消息回调
     *
     * @param listener
     */
    public void setMessageListener(TCChatRoomListener listener) {
        mTCChatRoomListener = listener;
        TIMManager.getInstance().addMessageListener(this);
    }

    /**
     * 解析TIM消息列表
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {

        if (list.size() > 0) {
            if (mGroupConversation != null)
                mGroupConversation.setReadMessage(list.get(0));
            Log.d(TAG, "parseIMMessage readMessage " + list.get(0).timestamp());
        }
//        if (!bNeverLoadMore && (tlist.size() < mLoadMsgNum))
//            bMore = false;

        for (int i = list.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = list.get(i);

            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();
                TIMUserProfile timUserProfile = currMsg.getSenderProfile();

                if (sendId.equals(mUserId)) {
                    TXLog.d(TAG, "recevie a self-msg type:" + type.name());
                    continue;
                }
                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        //群被解散
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onGroupDelete();
                    }
                    try {

                        //后台关闭直播
                        String a = new String(((TIMGroupSystemElem) elem).getUserData(),"UTF-8");
                        if(a.equals("C4CFFF20E0B2B97C4C298CB8FCB3C673")){
                            Event.System event = new Event.System();
                            event.action = 1;
                            EventBus.getDefault().post(event);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }


                //定制消息 -- 已弃用（不支持敏感词过滤）
                if (type == TIMElemType.Custom) {
                    continue;
                }

                //其他群消息过滤

                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null)
//                    if (!CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
//                        continue;
//                    }

                    //最后处理文本消息
                    if (type == TIMElemType.Text) {

                        handleCustomTextMsg(elem, timUserProfile);
                    }
            }
        }
    }

    /**
     * 退出房间
     */
    public void removeMsgListener() {
        mTCChatRoomListener = null;
        TIMManager.getInstance().removeMessageListener(this);
        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
    }

    /**
     * 发送消息
     * @param msg              TIM消息
     * @param timValueCallBack 发送消息回调类
     */
    private void sendTIMMessage(TIMMessage msg, TIMValueCallBack<TIMMessage> timValueCallBack) {
        if (mGroupConversation != null)
            mGroupConversation.sendMessage(msg, timValueCallBack);
    }

    private void sendMessage(int cmd, Object param, TIMValueCallBack<TIMMessage> timValueCallBack) {

        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("userAction", cmd);
            sendJson.put("userId", UserInfoMgr.getInstance().getUid());
            sendJson.put("nickName", UserInfoMgr.getInstance().getNickname());
            sendJson.put("headPic", UserInfoMgr.getInstance().getHeadPic());
            sendJson.put("level",UserInfoMgr.getInstance().getLevel());

            //如果是点亮多加一个参数
            if(cmd == TCConstants.IMCMD_PRAISE){
                sendJson.put("heartTwo",param);
            }
            sendJson.put("msg", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();

        if (mGroupConversation != null)
            Log.i(TAG, "send cmd : " + cmd + "|" + cmds + "|" + mGroupConversation.toString());

        TIMMessage msg = new TIMMessage();

//        if (cmd == TCConstants.IMCMD_PAILN_TEXT) {

            TIMTextElem elem = new TIMTextElem();
            elem.setText(cmds);

            if (msg.addElement(elem) != 0) {
                Log.d(TAG, "addElement failed");
                return;
            }

        sendTIMMessage(msg, timValueCallBack);

    }

    /**
     * 处理定制消息 赞 加入 退出 弹幕 并执行相关回调
     *
     * @param elem 消息体
     */
    private void handleCustomTextMsg(TIMElem elem, TIMUserProfile timUserProfile) {
        try {

            //String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");

            if (elem.getType() != TIMElemType.Text)
                return;

            String jsonString = ((TIMTextElem) elem).getText();
            Log.i(TAG, "cumstom msg  " + jsonString);

            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = (int) json.get("userAction");
            String userId = (String) json.get("userId");
            String nickname = (String) json.get("nickName");
            String level = (String) json.get("level");
            nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
            String headPic = (String) json.get("headPic");
            switch (action) {
                case TCConstants.IMCMD_PRAISE:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level),json.getString("heartTwo"));
                    break;
                case TCConstants.IMCMD_ENTER_LIVE:
                case TCConstants.IMCMD_EXIT_LIVE:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level), null);
                    break;
                case TCConstants.IMCMD_PAILN_TEXT:
                case TCConstants.IMCMD_DANMU:
                    String msg = (String) json.get("msg");
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level), msg);
                    break;
                case TCConstants.IMCMD_GIFT:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level),json.getString("msg"));
                    break;
                case TCConstants.IMCMD_SHUT_UP:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level),json.getString("msg"));
                    break;
                case TCConstants.IMCMD_SET_MANAGE:
                case TCConstants.IMCMD_SET_MANAGE_CANCEL:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level),json.getString("msg"));
                    break;
                case TCConstants.IMCMD_NOTIFY:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, new SimpleUserInfo(userId, nickname, headPic,level),json.getString("msg"));
                    break;
                default:
                    break;
            }

        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        catch (ClassCastException e) {
            String senderId = timUserProfile.getIdentifier();
            String nickname = timUserProfile.getNickName();
            String headPic = timUserProfile.getFaceUrl();

            nickname = TextUtils.isEmpty(nickname) ? senderId : nickname;
            mTCChatRoomListener.onReceiveMsg(TCConstants.IMCMD_PAILN_TEXT,
                    new SimpleUserInfo(senderId, nickname, headPic,"0"), ((TIMTextElem) elem).getText());
        }
        catch (JSONException e) {
            e.printStackTrace();
            // 异常处理代码
        }
    }

    /**
     * 消息循环监听类
     */
    public interface TCChatRoomListener {

        /**
         * 加入群组回调
         *
         * @param code 错误码，成功时返回0，失败时返回相应错误码
         * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onJoinGroupCallback(int code, String msg);

        //void onGetGroupMembersList(int code, List<TIMUserProfile> result);

        /**
         * 发送消息结果回调
         *
         * @param code       错误码，成功时返回0，失败时返回相应错误码
         * @param timMessage 发送的TIM消息
         */
        void onSendMsgCallback(int code, TIMMessage timMessage);

        /**
         * 接受消息监听接口
         * 文本消息回调
         *
         * @param type     消息类型
         * @param userInfo 发送者信息
         * @param content  内容
         */
        void onReceiveMsg(int type, SimpleUserInfo userInfo, String content);

        /**
         * 群组删除回调，在主播群组解散时被调用
         */
        void onGroupDelete();
    }


    /*
    * 获取收入
    *
    * */
    public interface OnGetIncomeListener{


        void onRequestCallback(int errCode, LinkedTreeMap<String, String> res);
    }
}
