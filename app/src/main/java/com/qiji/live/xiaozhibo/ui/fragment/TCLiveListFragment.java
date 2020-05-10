package com.qiji.live.xiaozhibo.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.logic.CommonMgr;
import com.qiji.live.xiaozhibo.logic.TCLiveInfo;
import com.qiji.live.xiaozhibo.logic.TCLiveListMgr;
import com.qiji.live.xiaozhibo.logic.TCLiveListAdapter;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.TCLivePlayerActivity;
import com.qiji.live.xiaozhibo.utils.StringUtils;

import java.util.ArrayList;


/**
 * 直播列表页面，展示当前直播及回放视频
 * 界面展示使用：ListView+SwipeRefreshLayout
 * 列表数据Adapter：TCLiveListAdapter
 * 数据获取接口： TCLiveListMgr
 */
public class TCLiveListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public static final int START_LIVE_PLAY = 100;
    private static final String TAG = "TCLiveListFragment";
    private ListView mVideoListView;
    private TCLiveListAdapter mVideoListViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //避免连击
    private long mLastClickTime = 0;
    //无数据
    private LinearLayout mLlNotData;

    public TCLiveListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videolist, container, false);

        mLlNotData = (LinearLayout) view.findViewById(R.id.ll_not_data);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mVideoListView = (ListView) view.findViewById(R.id.live_list);
        mVideoListView.setDivider(null);
        //轮播

        mVideoListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.view_index_head,null));

        mVideoListViewAdapter = new TCLiveListAdapter(getActivity(), (ArrayList<LiveBean>) TCLiveListMgr.getInstance().getLiveList().clone());
        mVideoListView.setAdapter(mVideoListViewAdapter);
        mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (0 == mLastClickTime || System.currentTimeMillis() - mLastClickTime > 1000) {
                    LiveBean item = mVideoListViewAdapter.getItem(i - 1);
                    if (item == null) {
                        Log.e(TAG,"live list item is null at position:"+i);
                        return;
                    }

                    startLivePlay(item);
                }
                mLastClickTime = System.currentTimeMillis();
                
            }
        });

        refreshListView();

        return view;
    }

    @Override
    public void onRefresh() {
        refreshListView();
    }

    /**
     * 刷新直播列表
     */
    private void refreshListView() {
        if (reloadLiveList()) {
            /*mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if (START_LIVE_PLAY == requestCode ) {
            if (0 != resultCode) {
                //观看直播返回错误信息后，刷新列表，但是不显示动画
                reloadLiveList();
            } else {
                if (data == null) {
                    return;
                }
                //更新列表项的观看人数和点赞数
                String userId = data.getStringExtra(TCConstants.PUSHER_ID);
                for (int i = 0; i < mVideoListViewAdapter.getCount(); i++) {
                    LiveBean info = mVideoListViewAdapter.getItem(i);
                    if (info != null && info.getUid().equalsIgnoreCase(userId)) {
                        info.setNums(String.valueOf(data.getLongExtra(TCConstants.MEMBER_COUNT, StringUtils.toLong(info.getNums()))));
                        info.setLight(String.valueOf(data.getLongExtra(TCConstants.HEART_COUNT, StringUtils.toLong(info.getLight()))));
                        mVideoListViewAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 重新加载直播列表
     */
    private boolean reloadLiveList() {
        return TCLiveListMgr.getInstance().reloadLiveList(new TCLiveListMgr.Listener() {
            @Override
            public void onLiveList(int retCode, final ArrayList<LiveBean> result, boolean refresh) {
                if (retCode == 0) {
                    mVideoListViewAdapter.clear();
                    if (result != null) {
                        mVideoListViewAdapter.addAll((ArrayList<LiveBean>)result.clone());
                    }
                    if (refresh) {
                        mVideoListViewAdapter.notifyDataSetChanged();
                    }
                    if(mVideoListViewAdapter.getCount() == 0){
                        mLlNotData.setVisibility(View.VISIBLE);
                    }else{
                        mLlNotData.setVisibility(View.GONE);
                    }

                } else {
                    mLlNotData.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "刷新列表失败", Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },0);
    }

    /**
     * 开始播放视频
     * @param item 视频数据
     */
    private void startLivePlay(final LiveBean item) {
        if(StringUtils.toInt(item.getIslive()) == 0){
            new AlertDialog.Builder(getContext()).setTitle("提示")
                    .setMessage("直播已结束")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
            return;
        }

        CommonMgr.getInstance().requestGetRoomSimpleInfo(UserInfoMgr.getInstance().getUid(), item.getUid(), new SimpleActionListener() {
            @Override
            public void onSuccess() {
                TCLivePlayerActivity.startLivePlayerActivity(getActivity(),item,START_LIVE_PLAY);
            }

            @Override
            public void onFail(int code, String msg) {

                
            }
        });

    }

}