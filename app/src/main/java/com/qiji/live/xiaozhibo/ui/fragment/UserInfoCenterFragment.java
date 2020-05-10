package com.qiji.live.xiaozhibo.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.BaseFragment;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.inter.ITCUserInfoMgrListener;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.AttentionListActivity;
import com.qiji.live.xiaozhibo.ui.EditUseInfoActivity;
import com.qiji.live.xiaozhibo.ui.FansListActivity;
import com.qiji.live.xiaozhibo.ui.LiveRecordListActivity;
import com.qiji.live.xiaozhibo.ui.ProfitActivity;
import com.qiji.live.xiaozhibo.ui.SettingActivity;
import com.qiji.live.xiaozhibo.ui.UserDiamondsActivity;
import com.qiji.live.xiaozhibo.ui.WebViewActivity;
import com.qiji.live.xiaozhibo.ui.customviews.TCLineControllerView;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;

/**
 * 用户中心
 */
public class UserInfoCenterFragment extends BaseFragment implements View.OnClickListener {


    private static final String TAG = "UserInfoCenterFragment";
    private AvatarImageView mHeadPic;
    private TextView mNickName;
    private TextView mUserId;

    private TCLineControllerView mBtnLogout;
    private TCLineControllerView mBtnProfit;
    private TCLineControllerView mBtnDiamonds;
    private TCLineControllerView mBtnRz;
    private TCLineControllerView mBtnAbout;
    private TCLineControllerView mBtnLevel;

    private ImageView mIvEditInfo;
    private ImageView mIvUserLevel;
    private ImageView mIvSex;
    private TextView mTvLiveNum,mTvFansNum,mTvFollowNum;

    private RelativeLayout mRlLive,mRlFans,mRlAttention;

    private UserInfoMgr mUserInfoMgr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_user_info_center, container, false);


        initView(view);

        initData();

        return view;
    }

    @Override
    public void initView(View view) {
        mHeadPic = (AvatarImageView) view.findViewById(R.id.iv_ui_head);
        mNickName = (TextView) view.findViewById(R.id.tv_ui_nickname);
        mUserId = (TextView) view.findViewById(R.id.tv_ui_user_id);
        mBtnLogout = (TCLineControllerView) view.findViewById(R.id.lcv_ui_logout);
        mBtnProfit = (TCLineControllerView) view.findViewById(R.id.lcv_ui_profit);
        mBtnDiamonds = (TCLineControllerView) view.findViewById(R.id.lcv_ui_diamonds);
        mBtnRz = (TCLineControllerView) view.findViewById(R.id.lcv_ui_rz);
        mBtnAbout = (TCLineControllerView) view.findViewById(R.id.lcv_ui_about);

        mBtnLevel = (TCLineControllerView) view.findViewById(R.id.lcv_ui_level);

        mIvEditInfo = (ImageView) view.findViewById(R.id.iv_user_info_edit);
        mIvUserLevel = (ImageView) view.findViewById(R.id.iv_user_info_level);
        mIvSex = (ImageView) view.findViewById(R.id.iv_user_info_sex);
        mTvLiveNum = (TextView) view.findViewById(R.id.tv_user_info_live_num);
        mTvFansNum = (TextView) view.findViewById(R.id.tv_user_info_fans_num);
        mTvFollowNum = (TextView) view.findViewById(R.id.tv_user_info_follow_num);

        mRlLive = (RelativeLayout) view.findViewById(R.id.rl_user_info_center_live);
        mRlAttention = (RelativeLayout) view.findViewById(R.id.rl_user_info_center_attention);
        mRlFans = (RelativeLayout) view.findViewById(R.id.rl_user_info_center_fans);

        mRlLive.setOnClickListener(this);
        mRlAttention.setOnClickListener(this);
        mRlFans.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
        mBtnProfit.setOnClickListener(this);
        mBtnDiamonds.setOnClickListener(this);
        mIvEditInfo.setOnClickListener(this);
        mBtnRz.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);
        mBtnLevel.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mUserInfoMgr = UserInfoMgr.getInstance();
        fillUI();

    }

    private void fillUI() {


        mHeadPic.setLoadImageUrl(mUserInfoMgr.getUserInfoBean().getAvatar());
        mNickName.setText(mUserInfoMgr.getUserInfoBean().getUser_nicename());
        mUserId.setText("ID:" + mUserInfoMgr.getUserInfoBean().getId());
        mIvUserLevel.setImageResource(TCUtils.getLevelRes(StringUtils.toInt(mUserInfoMgr.getUserInfoBean().getLevel())));
        mTvFollowNum.setText(TCUtils.setDefaultValue(mUserInfoMgr.getUserInfoBean().getFollows(),"0"));
        mTvFansNum.setText(TCUtils.setDefaultValue(mUserInfoMgr.getUserInfoBean().getFans(),"0"));
        mTvLiveNum.setText(TCUtils.setDefaultValue(mUserInfoMgr.getUserInfoBean().getLives(),"0"));
        mIvSex.setImageResource(TCUtils.getSexRes(StringUtils.toInt(mUserInfoMgr.getUserInfoBean().getSex())));

    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfoMgr.getInstance().queryUserInfo(new ITCUserInfoMgrListener() {
            @Override
            public void OnQueryUserInfo(int error, String errorMsg) {

            }

            @Override
            public void OnSetUserInfo(int error, String errorMsg) {
                fillUI();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //关于我们
            case R.id.lcv_ui_about:
                Intent a = new Intent(getActivity(), WebViewActivity.class);
                a.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=Appapi&m=page&a=lists");
                startActivity(a);
                break;
            //认证
            case R.id.lcv_ui_rz:
                Intent r = new Intent(getActivity(), WebViewActivity.class);
                r.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=Appapi&m=auth&a=index&uid=" + UserInfoMgr.getInstance().getUid());
                startActivity(r);
                break;
            case R.id.lcv_ui_set: //设置用户信息
                enterEditUserInfo();
                break;
            case R.id.lcv_ui_logout: //设置
                SettingActivity.startSettingActivity(getActivity());
                break;

            //收益
            case R.id.lcv_ui_profit:
                Intent p = new Intent(getActivity(), ProfitActivity.class);
                startActivity(p);
                break;
            //钻石
            case R.id.lcv_ui_diamonds:
                Intent u = new Intent(getActivity(), UserDiamondsActivity.class);
                startActivity(u);
                break;
            //编辑资料
            case R.id.iv_user_info_edit:
                Intent e = new Intent(getActivity(),EditUseInfoActivity.class);
                startActivity(e);
                break;
            //直播
            case R.id.rl_user_info_center_live:
                Intent l = new Intent(getActivity(), LiveRecordListActivity.class);
                startActivity(l);

                break;
            //粉丝
            case R.id.rl_user_info_center_fans:
                Intent f = new Intent(getActivity(), FansListActivity.class);
                startActivity(f);
                break;
            //关注列表
            case R.id.rl_user_info_center_attention:
                Intent at = new Intent(getActivity(), AttentionListActivity.class);
                startActivity(at);
                break;
            //签名
            case R.id.lcv_eui_sign:

                break;
            //等级
            case R.id.lcv_ui_level:
                Intent le = new Intent(getActivity(), WebViewActivity.class);
                le.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=Appapi&m=level&a=index&uid=" + UserInfoMgr.getInstance().getUid());
                startActivity(le);
                break;
        }
    }
    private void enterEditUserInfo() {
        try {
            Intent intent = new Intent(getContext(), EditUseInfoActivity.class);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
