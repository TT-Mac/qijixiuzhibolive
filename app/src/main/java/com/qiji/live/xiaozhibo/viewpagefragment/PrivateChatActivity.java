package com.qiji.live.xiaozhibo.viewpagefragment;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.ViewPageFragmentAdapter;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.chat.ConversationFragment;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.widget.PagerSlidingTabStrip;

import butterknife.Bind;
import butterknife.ButterKnife;


/*
*
* 私信
* */
public class PrivateChatActivity extends TCBaseActivity implements UIInterface {


    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;

    @Bind(R.id.chat_pt)
    PagerSlidingTabStrip mTabStrip;

    @Bind(R.id.chat_vp)
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        ButterKnife.bind(this);

        initData();
        initView();

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

        mTabStrip.setUnderlineColor(getResources().getColor(R.color.white));
        mTabStrip.setDividerColor(getResources().getColor(R.color.white));
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.white));
        mTabStrip.setTextColor(Color.BLACK);

        mTabStrip.setSelectedTextColor(getResources().getColor(R.color.cool_blue));
        mTabStrip.setIndicatorHeight(4);
        mTabStrip.setZoomMax(0f);
        mTabStrip.setIndicatorColorResource(R.color.cornflower);


        ViewPageFragmentAdapter adapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),mTabStrip,mViewPager);

        Bundle b1 = new Bundle();
        b1.putString("type","1");
        Bundle b2 = new Bundle();
        b2.putString("type","0");
        adapter.addTab("关注","GZ", ConversationFragment.class,b1);
        adapter.addTab("未关注","WGZ", ConversationFragment.class,b2);



    }
}
