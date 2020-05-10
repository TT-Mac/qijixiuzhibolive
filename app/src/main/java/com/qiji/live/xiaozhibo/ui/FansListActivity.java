package com.qiji.live.xiaozhibo.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.FansAdapter;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.FansListMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
*
* 粉丝列表
*
* */
public class FansListActivity extends TCBaseActivity implements UIInterface, FansListMgr.FansCallback {


    @Bind(R.id.rv_fans)
    RecyclerView mRvFansList;

    @Bind(R.id.sw_fans)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.view_tac)
    TCActivityTitle mTCActivityTitle;

    //无数据背景图
    @Bind(R.id.ll_not_data)
    LinearLayout mLlNotData;

    private FansListMgr mFansListMgr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_list);


        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {
        mFansListMgr = FansListMgr.getInstance();

        mFansListMgr.setFansCallback(this);

        mFansListMgr.getFans(UserInfoMgr.getInstance().getUid());

    }

    @Override
    public void initView() {

        mRvFansList.setLayoutManager(new LinearLayoutManager(this));

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               mFansListMgr.getFans(UserInfoMgr.getInstance().getUid());
           }
        });

        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onSuccess(List<GlobalUserBean> list) {
        mSwipeRefreshLayout.setRefreshing(false);
        mRvFansList.setAdapter(new FansAdapter(this,list));

        if(list.size() == 0){
            mLlNotData.setVisibility(View.VISIBLE);
        }else{
            mLlNotData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(int code, String msg) {

        mLlNotData.setVisibility(View.VISIBLE);

    }
}
