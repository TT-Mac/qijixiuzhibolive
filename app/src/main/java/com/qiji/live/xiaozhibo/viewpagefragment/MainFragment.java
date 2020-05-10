package com.qiji.live.xiaozhibo.viewpagefragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.ViewPageFragmentAdapter;
import com.qiji.live.xiaozhibo.base.BaseViewPagerFragment;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.ui.SearchUserActivity;
import com.qiji.live.xiaozhibo.ui.fragment.AttentionFragment;
import com.qiji.live.xiaozhibo.ui.fragment.NewsFragment;
import com.qiji.live.xiaozhibo.ui.fragment.TCLiveListFragment;
import com.qiji.live.xiaozhibo.utils.TCUtils;

/**
 * 首页
 */

public class MainFragment extends BaseViewPagerFragment implements UIInterface, View.OnClickListener {


    private ImageView mIvSearch;

    private ImageView mIvChat;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_index;
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        adapter.addTab("关注","GZ", AttentionFragment.class,new Bundle());
        adapter.addTab("热门","RM", TCLiveListFragment.class,new Bundle());
        adapter.addTab("最新","ZX", NewsFragment.class,new Bundle());
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void setScreenPageLimit() {

        mTabStrip.setUnderlineColor(getResources().getColor(R.color.white));
        mTabStrip.setDividerColor(getResources().getColor(R.color.white));
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.white));
        mTabStrip.setTextColor(Color.BLACK);
      
        mTabStrip.setSelectedTextColor(getResources().getColor(R.color.cool_blue));
        mTabStrip.setIndicatorHeight(4);
        mTabStrip.setZoomMax(0.2f);
        mTabStrip.setIndicatorColorResource(R.color.cornflower);

    }

    @Override
    public void initView() {
        mIvSearch = (ImageView) mRoot.findViewById(R.id.iv_main_index_search);
        mIvChat = (ImageView) mRoot.findViewById(R.id.iv_main_index_chat);
        mIvSearch.setOnClickListener(this);
        mIvChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //搜索
            case R.id.iv_main_index_search:
                SearchUserActivity.startSearchUserActivity(getActivity());
                break;
            //私信
            case R.id.iv_main_index_chat:
                /*Intent intent = new Intent(getContext(),PrivateChatActivity.class);
                startActivity(intent);*/
                TCUtils.getInstanceToast("即将开放,尽请期待").show();
                //TalkActivity.navToChat(getContext(), UserInfoMgr.getInstance().getUid(), TIMConversationType.C2C);
                break;
        }
    }
}
