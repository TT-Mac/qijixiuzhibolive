package com.qiji.live.xiaozhibo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.tencent.TIMManager;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.logic.CommonMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.logic.UserLoginMgr;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.logic.TCLoginMgr;
import com.qiji.live.xiaozhibo.logic.TCUserInfoMgr;
import com.qiji.live.xiaozhibo.ui.fragment.UserInfoCenterFragment;
import com.qiji.live.xiaozhibo.viewpagefragment.MainFragment;

import tencent.tls.platform.TLSUserInfo;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 注释乃立国之根本
 *
 * 主界面，包括直播列表，用户信息页
 * UI使用FragmentTabHost+Fragment
 * 直播列表：TCLiveListFragment
 * 个人信息页：TCUserInfoFragment
 */
public class TCMainActivity extends FragmentActivity {
    private static final String TAG = TCMainActivity.class.getSimpleName();

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    private final Class mFragmentArray[] = {MainFragment.class, MainFragment.class, UserInfoCenterFragment.class};
    private int mImageViewArray[] = {R.drawable.tab_video, R.drawable.play_click, R.drawable.tab_user};
    private String mTextviewArray[] = {"video", "publish", "user"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mLayoutInflater = LayoutInflater.from(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);

        int fragmentCount = mFragmentArray.length;
        for (int i = 0; i < fragmentCount; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }
        mTabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonMgr.getInstance().requestGetRoomSimpleInfo(UserInfoMgr.getInstance().getUid(), UserInfoMgr.getInstance().getUid(), new SimpleActionListener() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(TCMainActivity.this, TCPublishSettingActivity.class));
                    }

                    @Override
                    public void onFail(int code, String msg) {


                    }
                });

            }
        });

        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));

        Log.w("TCLog","mainactivity oncreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("TCLog","mainactivity onstart");
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            //relogin
            final UserLoginMgr tcLoginMgr = UserLoginMgr.getInstance();
            final UserInfoBean userInfo = UserLoginMgr.getInstance().getLastUserInfo();
            tcLoginMgr.setUserLoginCallback(new UserLoginMgr.UserLoginCallback() {
                @Override
                public void onSuccess() {
                    tcLoginMgr.removeTCLoginCallback();
                    UserInfoMgr.getInstance().setUserId(userInfo.getId(), null);
                }

                @Override
                public void onFailure(int code, String msg)  {
                    tcLoginMgr.removeTCLoginCallback();
                }
            });

            tcLoginMgr.checkCacheAndLogin();
            Log.w("TCLog","mainactivity onstart relogin");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
    }

    public class ExitBroadcastRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                onReceiveExitMsg();
            }
        }
    }

    public void onReceiveExitMsg() {
        TCUtils.showKickOutDialog(this);
    }

    /**
     * 动态获取tabicon
     * @param index tab index
     * @return
     */
    private View getTabItemView(int index) {
        View view;
        if (index % 2 == 0) {
            view = mLayoutInflater.inflate(R.layout.tab_button1, null);
        } else {
            view = mLayoutInflater.inflate(R.layout.tab_button, null);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
