package com.qiji.live.xiaozhibo.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberRoleType;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.UserDialogInfoJson;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.TCChatRoomMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoDialogMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.HomePageActivity;
import com.qiji.live.xiaozhibo.ui.ManageListActivity;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.ui.TCLivePlayerActivity;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weipeng on 16/11/26.
 */

public class UserInfoDialogFragment extends DialogFragment implements UIInterface, UserInfoDialogMgr.UserInfoDialogCallback {

    //举报
    @Bind(R.id.tv_live_manage_or_report)
    TextView mTvReport;

    //管理按钮
    @Bind(R.id.iv_dialog_setting)
    ImageView mIvSetting;

    @Bind(R.id.ib_show_dialog_back)
    ImageButton mIbBack;

    @Bind(R.id.iv_show_dialog_level)
    ImageView mIvInfoLevel;

    @Bind(R.id.iv_show_dialog_sex)
    ImageView mIvInfoSex;

    @Bind(R.id.tv_user_info_id)
    TextView mTvInfoId;

    @Bind(R.id.tv_show_dialog_u_address)
    TextView mTvLbs;

    @Bind(R.id.tv_user_info_sign)
    TextView mTvInfoSign;

    @Bind(R.id.tv_show_dialog_u_fllow_num)
    TextView mTvFollowNum;

    @Bind(R.id.tv_show_dialog_u_fans_num)
    TextView mTvFansNum;

    @Bind(R.id.tv_show_dialog_u_send_num)
    TextView mTvSendNum;

    @Bind(R.id.tv_show_dialog_u_ticket)
    TextView mTvTicketNum;

    @Bind(R.id.tv_show_dialog_u_follow_btn)
    TextView mTvFollowBtn;

    @Bind(R.id.tv_show_dialog_u_private_chat_btn)
    TextView mTvPrivateChatBtn;

    @Bind(R.id.tv_show_dialog_u_home_btn)
    TextView mTvHomeBtn;

    @Bind(R.id.av_show_dialog_u_head)
    AvatarImageView mIvInfoHead;

    @Bind(R.id.tv_show_dialog_u_name)
    TextView mTvInfoName;

    @Bind(R.id.ll_user_info_dialog2)
    LinearLayout mLlBottomMenu2;

    @Bind(R.id.ll_user_info_dialog)
    LinearLayout mLlBottomMenu;


    private UserInfoDialogMgr mUserInfoDialogMgr;

    //当前用户id
    private String mTouid;

    //主播id
    private String liveId;

    //groupid
    private String groupId;

    private UserDialogInfoJson mUserDialogInfoJson;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_no_background);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fragment_user_info_dialog);


        ButterKnife.bind(this,dialog);
        initView();
        initData();

        return dialog;
    }



    @Override
    public void initData() {
        mTouid  = getArguments().getString("touid");
        liveId  = getArguments().getString("liveid");
        groupId = getArguments().getString("groupid");

        //自己
        if(StringUtils.toInt(mTouid) == StringUtils.toInt(UserInfoMgr.getInstance().getUid())){
            mLlBottomMenu2.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.GONE);
        }

        mUserInfoDialogMgr = UserInfoDialogMgr.getInstance();

        mUserInfoDialogMgr.setUserInfoDialogCallback(this);

        mUserInfoDialogMgr.requestGetUserInfo(UserInfoMgr.getInstance().getUid(), mTouid,liveId);


        //判断是自己还是主播还是管理员
        if(StringUtils.toInt(mTouid) == StringUtils.toInt(UserInfoMgr.getInstance().getUid())){

            mTvReport.setVisibility(View.GONE);

        }else if(liveId == mTouid && StringUtils.toInt(mTouid) == StringUtils.toInt(UserInfoMgr.getInstance().getUid())){
            //当前用户是主播
            mIvSetting.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void initView() {


        //关闭弹窗
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mTvFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                followUser();
            }
        });

        //主页
        mTvHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageActivity.startHomePageActivity(getActivity(),mTouid);
                dismiss();
            }
        });

        mLlBottomMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageActivity.startHomePageActivity(getActivity(),mTouid);
                dismiss();
            }
        });

        //举报
        mTvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });
        
        //设置
        mIvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingDialog();
            }
        });

        //私信
        mTvPrivateChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCUtils.getInstanceToast("即将开放,尽请期待").show();
            }
        });

    }

    //设置弹窗
    private void showSettingDialog() {
        final SparseArray<String> array = new SparseArray<>();

        //当前用户是主播
        array.put(1,"举报");
        array.put(3,"禁言");

        array.put(5,"取消");

        //是否是主播
        if(StringUtils.toInt(UserInfoMgr.getInstance().getUid()) == StringUtils.toInt(liveId)){

            array.put(4,"管理员列表");
            array.put(2,StringUtils.toInt(mUserDialogInfoJson.isadmin) == 2 ? "删除管理" : "设置管理");
        }


        final String[] str = new String[array.size()];

        for(int i = 0; i < array.size();i ++){
            str[i] = array.valueAt(i);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (array.keyAt(array.indexOfValue(str[which]))){
                    //举报
                    case 1:
                        showReportDialog();
                        break;
                    //设置取消管理
                    case 2:

                        setOrCancleManage();

                        break;
                    //禁言
                    case 3:
                        TIMGroupManager.getInstance().modifyGroupMemberInfoSetSilence(groupId, mTouid, 300, new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {

                                Toast.makeText(getContext(),"禁言成功",Toast.LENGTH_SHORT).show();
                                TCChatRoomMgr.getInstance().sendShutUpMessage(mTouid,mUserDialogInfoJson.user_nicename);
                            }
                        });
                        break;
                    //管理员列表
                    case 4:
                        Intent intent = new Intent(getContext(), ManageListActivity.class);

                        getContext().startActivity(intent);
                        break;

                    default:
                        break;

                }

            }
        });
        builder.create().show();
    }

    //设置取消管理
    private void setOrCancleManage() {

        //请求接口
        mUserInfoDialogMgr.requestSetManage(UserInfoMgr.getInstance().getUid(), UserInfoMgr.getInstance().getToken(), liveId, mTouid, new SimpleActionListener() {
            @Override
            public void onSuccess() {

                TIMGroupMemberRoleType type;

                if(StringUtils.toInt(mUserDialogInfoJson.isadmin) == 2){
                    type = TIMGroupMemberRoleType.Normal;
                }else{
                    type = TIMGroupMemberRoleType.Admin;

                }

                //修改用户类型
                TIMGroupManager.getInstance().modifyGroupMemberInfoSetRole(groupId, mTouid, type, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {

                        if(StringUtils.toInt(mUserDialogInfoJson.isadmin) == 2){
                            mUserDialogInfoJson.isadmin = "1";
                            TCChatRoomMgr.getInstance().sendSetCancelManage(mTouid,mUserDialogInfoJson.user_nicename,false);
                            Toast.makeText(getContext(),"取消管理员成功",Toast.LENGTH_SHORT).show();
                        }else{
                            mUserDialogInfoJson.isadmin = "2";
                            TCChatRoomMgr.getInstance().sendSetCancelManage(mTouid,mUserDialogInfoJson.user_nicename,true);
                            Toast.makeText(getContext(),"设置管理员成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //举报弹窗
    private void showReportDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入举报内容");
        final EditText et = new EditText(getActivity());

        builder.setView(et);

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(TextUtils.isEmpty(et.getText().toString())){
                    Toast.makeText(getActivity(),"举报内容为空",Toast.LENGTH_SHORT).show();
                }

                mUserInfoDialogMgr.requestReport(UserInfoMgr.getInstance().getUid(), mTouid,UserInfoMgr.getInstance().getToken(),
                        et.getText().toString(), new SimpleActionListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),"感谢你的举报，我们会尽快处理",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
            }
        });

        builder.create().show();
    }

    private void followUser() {

        UserInfoMgr.getInstance().attentionOrCancelUser(new SimpleActionListener() {
            @Override
            public void onSuccess() {

                Event.DialogFollow event = new Event.DialogFollow();
                event.uid = mTouid;

                //修改状态
                if(StringUtils.toInt(mUserDialogInfoJson.isattention) == 1){
                    mUserDialogInfoJson.isattention = "0";
                    mTvFollowBtn.setText("关注");
                    event.action = 0;
                    EventBus.getDefault().post(event);
                }else{
                    mUserDialogInfoJson.isattention = "1";
                    mTvFollowBtn.setText("已关注");
                    event.action = 1;
                    EventBus.getDefault().post(event);
                }


                //mTvFollowBtn.setTextColor(getActivity().getResources().getColor(R.color.text_gray3));


            }

            @Override
            public void onFail(int code, String msg) {

            }
        },mTouid);
    }

    private void fillUI(UserDialogInfoJson userInfo) {

        mUserDialogInfoJson = userInfo;

        mIvInfoHead.setLoadImageUrl(userInfo.avatar_thumb);

        mIvInfoSex.setImageResource(TCUtils.getSexRes(StringUtils.toInt(userInfo.sex)));

        mTvInfoName.setText(userInfo.user_nicename);

        mTvInfoSign.setText(userInfo.signature);

        mTvInfoId.setText("ID:" + userInfo.id);

        mIvInfoLevel.setImageResource(TCUtils.getLevelRes(StringUtils.toInt((userInfo.level))));

        mTvLbs.setText(userInfo.city);

        mTvTicketNum.setText("收入:  " + userInfo.votestotal);

        mTvSendNum.setText(  "送出:  " + userInfo.recommend);

        mTvFansNum.setText(  "粉丝  " + userInfo.fans);

        mTvFollowNum.setText("关注  " + userInfo.follows);

        if(StringUtils.toInt(userInfo.isattention) == 1){
            //修改状态
            //mTvFollowBtn.setEnabled(false);
            mTvFollowBtn.setText("已关注");
            //mTvFollowBtn.setTextColor(getActivity().getResources().getColor(R.color.text_gray3));
        }

        //根据用户的状态显示操作按钮

        switch (StringUtils.toInt(userInfo.action)){
            //自己
            case 0:
                mTvReport.setVisibility(View.GONE);
                mIvSetting.setVisibility(View.GONE);
                break;
            //普通人
            case 1:
                mTvReport.setVisibility(View.VISIBLE);
                break;
            //管理员
            case 2:
                mIvSetting.setVisibility(View.VISIBLE);
                break;
            //主播
            case 3:
                mIvSetting.setVisibility(View.VISIBLE);
                break;
        }


    }


    @Override
    public void onSuccess(UserDialogInfoJson userInfo) {
        fillUI(userInfo);

    }



    @Override
    public void onFail(int error, String msg) {

    }




}
