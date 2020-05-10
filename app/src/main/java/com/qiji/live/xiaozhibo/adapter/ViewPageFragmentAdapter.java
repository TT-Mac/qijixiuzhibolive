package com.qiji.live.xiaozhibo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.qiji.live.xiaozhibo.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by weipeng on 16/10/12.
 */
public class ViewPageFragmentAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    protected PagerSlidingTabStrip mPagerStrip;
    private final ViewPager mViewPager;
    public ArrayList<ViewPageInfo> mTabs = new ArrayList<>();
    private Map<String,Fragment> mFragments = new ArrayMap<>();


    public ViewPageFragmentAdapter(FragmentManager fm, PagerSlidingTabStrip pageStrip, ViewPager pager) {

        super(fm);
        mContext = pager.getContext();
        mPagerStrip = pageStrip;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setCurrentItem(2);
        mPagerStrip.setViewPager(mViewPager);
    }

    public void addTab(String title, String tag, Class<?> clss, Bundle args){
        ViewPageInfo viewPageInfo = new ViewPageInfo(title,tag,clss,args);
        addFragment(viewPageInfo);
    }


    public void addAllTab(ArrayList<ViewPageInfo> mTabs) {
        for (ViewPageInfo viewPageInfo : mTabs) {
            addFragment(viewPageInfo);
        }
    }

    /**
     * 移除第一次
     */
    public void remove() {
        remove(0);
    }


    //如果index小于0，则从第一个开始删 如果大于tab的数量值则从最后一个开始删除
    public void remove(int index){
        if(mTabs.isEmpty()){
            return;
        }
        if(index < 0){
            index = 0;
        }
        if(index >= mTabs.size()){
            index = mTabs.size() - 1;
        }

        ViewPageInfo info = mTabs.get(index);
        if(mFragments.containsKey(info.tag)){
            mFragments.remove(info.tag);
        }
        mTabs.remove(info);
        mPagerStrip.removeViewAt(index);

        notifyDataSetChanged();
    }

    private void addFragment(ViewPageInfo info) {
        if(info == null){
            return;
        }

        /*if(!TextUtils.isEmpty(info.title)){
            mPagerStrip.
        }*/
        mTabs.add(info);
        notifyDataSetChanged();
        mPagerStrip.setViewPager(mViewPager);
    }


    @Override
    public Fragment getItem(int position) {
        ViewPageInfo info = mTabs.get(position);
        Fragment fragment = mFragments.get(info.tag);
        if(fragment == null){
            fragment = Fragment.instantiate(mContext,info.clss.getName(),info.args);
            //避免重复创建进行缓存
            mFragments.put(info.tag,fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).title;
    }
}
