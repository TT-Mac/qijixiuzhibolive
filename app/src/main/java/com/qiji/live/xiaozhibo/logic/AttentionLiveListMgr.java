package com.qiji.live.xiaozhibo.logic;

import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.tencent.rtmp.TXLog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/19.
 */

public class AttentionLiveListMgr {
    private static final String TAG = TCLiveListMgr.class.getSimpleName();
    private static final int PAGESIZE = 20;
    private static final int LIST_TYPE_LIVE = 1;
    private static final int LIST_TYPE_VOD  = 2;
    private static final int LIST_TYPE_ALL  = 3;
    private boolean mIsFetching;

    private ArrayList<LiveBean> mLiveInfoList = new ArrayList<>();

    private AttentionLiveListMgr() {
        mIsFetching = false;
    }

    private static class AttentionLiveListMgrHolder {
        private static AttentionLiveListMgr instance = new AttentionLiveListMgr();
    }

    public static AttentionLiveListMgr getInstance() {
        return AttentionLiveListMgrHolder.instance;
    }

    /**
     * 获取内存中缓存的直播列表
     * @return 完整列表
     */
    public ArrayList<LiveBean> getLiveList() {
        return mLiveInfoList;
    }

    /**
     * 分页获取完整直播列表
     * @param listener 列表回调，每获取到一页数据回调一次
     */
    public boolean reloadLiveList(TCLiveListMgr.Listener listener, int type) {
        if (mIsFetching) {
            TXLog.w(TAG,"reloadLiveList ignore when fetching");
            return false;
        }
        TXLog.d(TAG,"fetchLiveList start");
        mLiveInfoList.clear();
        fetchLiveList(LIST_TYPE_ALL, 1, PAGESIZE, listener);

        mIsFetching = true;
        return true;
    }


    /**
     * 获取直播列表
     *
     * @param type     1:拉取在线直播列表 2:拉取7天内录播列表 3:拉取在线直播和7天内录播列表，直播列表在前，录播列表在后
     * @param pageNo   页数
     * @param pageSize 每页个数
     */
    public void fetchLiveList(final int type, final int pageNo, final int pageSize, final TCLiveListMgr.Listener listener) {
        final JSONObject req = new JSONObject();
        try {
            req.put("flag", type);
            req.put("pageno", pageNo);
            req.put("pagesize", pageSize);
            req.put("Action","FetchList");
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);

        Call<ResponseJson<LiveBean>> call = apiStores.requestAttention(TCApplication.getInstance().getLoginUid());
        call.enqueue(new Callback<ResponseJson<LiveBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<LiveBean>> call, Response<ResponseJson<LiveBean>> response) {
                mIsFetching = false;
                if(AppClient.checkResult(response)){
                    ArrayList<LiveBean> result = (ArrayList<LiveBean>) response.body().getData().getInfo();
                    mLiveInfoList.addAll(result);
                    if (listener != null) {
                        listener.onLiveList(0,mLiveInfoList,pageNo == 1);
                    }
                    return;
                }

                if (listener != null) {
                    listener.onLiveList(400,null,true);
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<LiveBean>> call, Throwable t) {

            }
        });
    }


    /**
     * 视频列表获取结果回调
     */
    public interface Listener {
        /**
         * @param retCode 获取结果，0表示成功
         * @param result  列表数据
         * @param refresh 是否需要刷新界面，首页需要刷新
         */
        public void onLiveList(int retCode, final ArrayList<LiveBean> result, boolean refresh);
    }
}
