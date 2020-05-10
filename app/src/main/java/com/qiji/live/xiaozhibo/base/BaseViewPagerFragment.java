package com.qiji.live.xiaozhibo.base;

/**
 * Created by weipeng on 16/10/11.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.ViewPageFragmentAdapter;
import com.qiji.live.xiaozhibo.utils.TDevice;
import com.qiji.live.xiaozhibo.widget.PagerSlidingTabStrip;

public abstract class BaseViewPagerFragment extends BaseFragment {

    private static final String TAG = "BaseViewPagerFragment";
    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ViewPageFragmentAdapter mTabsAdapter;
    protected View mRoot;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot == null) {
            View root = inflater.inflate(getLayoutId(), null);

            mTabStrip = (PagerSlidingTabStrip) root
                    .findViewById(R.id.pager_tabstrip);
            mViewPager = (ViewPager) root.findViewById(R.id.pager);
            setScreenPageLimit();

            mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),
                    mTabStrip, mViewPager);

            mRoot = root;
            onSetupTabAdapter(mTabsAdapter);
        }
        return mRoot;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt("position");
            mViewPager.setCurrentItem(pos, true);
        }
    }

    protected void setScreenPageLimit() {
    }

    // @Override
    // public void onSaveInstanceState(Bundle outState) {
    // //No call for super(). Bug on API Level > 11.
    // if (outState != null && mViewPager != null) {
    // outState.putInt("position", mViewPager.getCurrentItem());
    // }
    // //super.onSaveInstanceState(outState);
    // }

    protected abstract void onSetupTabAdapter(ViewPageFragmentAdapter adapter);
}