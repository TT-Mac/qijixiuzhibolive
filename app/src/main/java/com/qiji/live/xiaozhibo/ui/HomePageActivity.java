package com.qiji.live.xiaozhibo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMConversationType;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.BaseViewHolder;
import com.qiji.live.xiaozhibo.base.LiveBaseActivity;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.HomePageJson;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.LiveRecordBean;
import com.qiji.live.xiaozhibo.chat.TalkActivity;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.HomePageMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.utils.TDevice;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
* 个人主页
*
* */
public class HomePageActivity extends TCBaseActivity implements UIInterface, HomePageMgr.HomePageCallback {

    public static final int START_LIVE_PLAY = 100;

    @Bind(R.id.iv_home_page_ui_head)
    AvatarImageView mIvHead;

    @Bind(R.id.tv_home_page_ui_nickname)
    TextView mTvNiceName;

    @Bind(R.id.iv_home_page_user_info_sex)
    ImageView mIvSex;

    @Bind(R.id.iv_home_page_user_info_level)
    ImageView mIvLevel;

    @Bind(R.id.tv_home_page_follow_num)
    TextView mTvFollowNum;

    @Bind(R.id.tv_home_page_fans_num)
    TextView mTvFansNum;

    @Bind(R.id.tv_home_page_index_sign)
    TextView mTvSign;

    @Bind(R.id.tv_home_page_index_sex2)
    TextView mTvSex2;

    @Bind(R.id.tv_home_page_index_id2)
    TextView mTvId2;

    @Bind(R.id.tv_home_page_index_sign2)
    TextView mTvSign2;

    @Bind(R.id.tv_home_page_index)
    TextView mTvIndex;

    @Bind(R.id.tv_home_page_video)
    TextView mTvVideo;

    @Bind(R.id.view_home_page_line_index)
    View mViewIndex;

    @Bind(R.id.view_home_page_line_video)
    View mViewVideo;

    @Bind(R.id.ll_home_page_index)
    LinearLayout mLlIndex;

    @Bind(R.id.ll_home_page_video)
    LinearLayout mLlVideo;

    //返回
    @Bind(R.id.iv_home_page_back)
    ImageView mIvBack;

    //贡献榜
    @Bind(R.id.ll_home_page_gx_list)
    LinearLayout mLlContribution;

    //直播记录
    @Bind(R.id.rv_home_page_video)
    RecyclerView mRvLiveRecord;

    //关注按钮底部
    @Bind(R.id.ll_home_page_follow)
    LinearLayout mLlFollow;

    //私信按钮底部
    @Bind(R.id.ll_home_page_chat)
    LinearLayout mLlChat;

    //拉黑按钮底部
    @Bind(R.id.ll_home_page_black)
    LinearLayout mLlBlack;

    //底部按钮菜单栏
    @Bind(R.id.ll_home_page_bottom_view)
    LinearLayout mLlBottomMenu;

    @Bind(R.id.rl_order)
    RelativeLayout mRlOrder;

    private HomePageMgr mHomePageMgr;

    private HomePageJson mHomePageJson;

    private String mTouid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ButterKnife.bind(this);

        initData();
        initView();
    }



    @Override
    public void initData() {

        mTouid = getIntent().getStringExtra("touid");
        mHomePageMgr = HomePageMgr.getInstance();
        mHomePageMgr.setHomePageCallback(this);
        mHomePageMgr.getHomePageInfo(UserInfoMgr.getInstance().getUid(),mTouid);
    }

    @Override
    public void initView() {
        //如果是自己隐藏底部菜单
        if(StringUtils.toInt(mTouid) == StringUtils.toInt(UserInfoMgr.getInstance().getUid())){
            mLlBottomMenu.setVisibility(View.GONE);
        }

        mTvIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(1);
            }
        });

        mTvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(2);
            }
        });

        mRvLiveRecord.setLayoutManager(new LinearLayoutManager(this));


        //关注点击事件
        mLlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser();
            }
        });


        //拉黑点击事件
        mLlBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackUser();
            }
        });

        //返回
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //私信
        mLlChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCUtils.getInstanceToast("即将开放,尽请期待").show();
                //TalkActivity.navToChat(HomePageActivity.this,mTouid,mHomePageJson.avatar_thumb,mHomePageJson.user_nicename, TIMConversationType.C2C);
            }
        });

        //排行榜
        mRlOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent a = new Intent(HomePageActivity.this, WebViewActivity.class);
                a.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=appapi&m=contribute&uid=" + mTouid);
                startActivity(a);
            }
        });
    }

    //拉黑取消拉黑
    private void blackUser() {
        mHomePageMgr.requestBlack(UserInfoMgr.getInstance().getUid(), mTouid, new SimpleActionListener() {
            @Override
            public void onSuccess() {
                if(StringUtils.toInt(mHomePageJson.isblack) == 1){
                    changeBlackState(0);
                }else{
                    changeBlackState(1);
                }
                //mHomePageMgr.getHomePageInfo(UserInfoMgr.getInstance().getUid(),mTouid);
            }

            @Override
            public void onFail(int code, String msg) {
                showToast(msg);
            }
        });
    }


    //关注取消关注
    private void followUser() {
        UserInfoMgr.getInstance().attentionOrCancelUser(new SimpleActionListener() {
            @Override
            public void onSuccess() {
                if(StringUtils.toInt(mHomePageJson.isattention) == 1){
                    changeFollowState(0);
                }else{
                    changeFollowState(1);
                }
                //mHomePageMgr.getHomePageInfo(UserInfoMgr.getInstance().getUid(),mTouid);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        },mTouid);
    }

    /*
    * 修改拉黑按钮状态
    *
    * */
    private void changeBlackState(int action) {
        if(action == 1){
            mHomePageJson.isblack = "1";
            ((TextView)mLlBlack.getChildAt(1)).setText("取消拉黑");
            changeFollowState(0);
        }else{
            mHomePageJson.isblack = "0";
            ((TextView)mLlBlack.getChildAt(1)).setText("拉黑");
        }
    }

    /*
    * 修改关注按钮的文字颜色状态
    *
    * */
    private void changeFollowState(int action) {

        if(action == 1){
            mHomePageJson.isattention = "1";
            ((TextView)mLlFollow.getChildAt(1)).setText("已关注");
            ((TextView)mLlFollow.getChildAt(1)).setTextColor(getResources().getColor(R.color.text_orange2));
            ((ImageView)mLlFollow.getChildAt(0)).setImageResource(R.drawable.ic_home_yiguanzhu);
            changeBlackState(0);
        }else{
            mHomePageJson.isattention = "0";
            ((TextView)mLlFollow.getChildAt(1)).setText("关注");
            ((TextView)mLlFollow.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue2));
            ((ImageView)mLlFollow.getChildAt(0)).setImageResource(R.drawable.ic_home_wei_guanzhu);
        }
    }

    private void changeTab(int i) {
        if(i == 1){
            mLlIndex.setVisibility(View.VISIBLE);
            mLlVideo.setVisibility(View.GONE);
            mViewIndex.setBackgroundColor(getResources().getColor(R.color.blue2));
            mViewVideo.setBackgroundColor(getResources().getColor(R.color.main_bg_color));

            mTvIndex.setTextColor(getResources().getColor(R.color.blue2));
            mTvVideo.setTextColor(getResources().getColor(R.color.colorGray4));

        }else{
            mLlIndex.setVisibility(View.GONE);
            mLlVideo.setVisibility(View.VISIBLE);
            mViewIndex.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
            mViewVideo.setBackgroundColor(getResources().getColor(R.color.blue2));

            mTvIndex.setTextColor(getResources().getColor(R.color.colorGray4));
            mTvVideo.setTextColor(getResources().getColor(R.color.blue2));
        }
    }


    public static void startHomePageActivity(Context context,String uid){
        Intent intent = new Intent(context,HomePageActivity.class);
        intent.putExtra("touid",uid);
        context.startActivity(intent);

    }

    /*
    * 填充用户个人主页信息
    *
    * */
    private void fillUI() {
        mIvHead.setLoadImageUrl(mHomePageJson.avatar_thumb);
        mIvLevel.setImageResource(TCUtils.getLevelRes(StringUtils.toInt(mHomePageJson.level)));
        mIvSex.setImageResource(TCUtils.getSexRes(StringUtils.toInt(mHomePageJson.sex)));
        mTvSex2.setText(TCUtils.getSexStr(StringUtils.toInt(mHomePageJson.sex)));
        mTvNiceName.setText(mHomePageJson.user_nicename);
        mTvSign.setText(mHomePageJson.signature);
        mTvSign2.setText(mHomePageJson.signature);
        mTvFansNum.setText(  "粉丝:" + mHomePageJson.fans);
        mTvFollowNum.setText("关注:" + mHomePageJson.follows);
        mTvId2.setText(mHomePageJson.id);

        changeFollowState(StringUtils.toInt(mHomePageJson.isattention));

        changeBlackState(StringUtils.toInt(mHomePageJson.isblack));


        //贡献榜
        for(int i = 0;i < mHomePageJson.contribute.size(); i ++){
            AvatarImageView head = new AvatarImageView(this);
            head.setLayoutParams(new LinearLayout.LayoutParams((int)TDevice.dp2px(40),(int)TDevice.dp2px(40)));
            head.setLoadImageUrl(mHomePageJson.contribute.get(i).getUserinfo().getAvatar_thumb());
            mLlContribution.addView(head);

        }

        //视频回放
        mRvLiveRecord.setAdapter(new RecyclerView.Adapter<BaseViewHolder>() {
            @Override
            public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(HomePageActivity.this).inflate(R.layout.item_live_record,parent,false);

                return new BaseViewHolder(HomePageActivity.this,view);
            }

            @Override
            public void onBindViewHolder(BaseViewHolder holder, int position) {

                final LiveRecordBean record = mHomePageJson.liverecord.get(position);
                holder.setText(R.id.tv_item_live_record_num,record.getNums());
                holder.setText(R.id.tv_item_live_record_time,record.dateendtime);
                holder.setText(R.id.tv_item_live_record_title,TextUtils.isEmpty(record.getTitle().trim()) ? "无标题" : record.getTitle());
                //回放记录点击事件
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLivePlay(record);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mHomePageJson.liverecord.size();
            }
        });

    }



    @Override
    public void onSuccess(HomePageJson homePageJson) {
        mHomePageJson = homePageJson;
        fillUI();
    }



    @Override
    public void onFail(String msg) {

    }

    /**
     * 开始播放视频
     * @param item 视频数据
     */
    private void startLivePlay(final LiveBean item) {

        if(LiveBaseActivity.IS_ON_LIVE == true){

            showToast("请关闭直播后,查看回放");
            return;
        }

        if(TextUtils.isEmpty(item.getVideo_url())){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("直播回放暂未生成")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
            return;
        }

        Intent intent = new Intent(HomePageActivity.this, TCLivePlayerActivity.class);
        intent.putExtra(TCConstants.PUSHER_ID, item.getUid());
        intent.putExtra(TCConstants.PLAY_URL,item.getVideo_url());
        intent.putExtra(TCConstants.PUSHER_NAME, item.getUser_nicename() == null ? item.getUid() : item.getUser_nicename());
        intent.putExtra(TCConstants.PUSHER_AVATAR, item.getAvatar());
        intent.putExtra(TCConstants.HEART_COUNT, item.getNums());
        intent.putExtra(TCConstants.MEMBER_COUNT, item.getLight());
        intent.putExtra(TCConstants.GROUP_ID, item.getGroupid());
        intent.putExtra(TCConstants.PLAY_TYPE, StringUtils.toInt(item.getIslive()) == 1 ? true:false);
        intent.putExtra(TCConstants.FILE_ID, item.getFile_id());
        intent.putExtra(TCConstants.COVER_PIC, item.getAvatar());
        startActivityForResult(intent,START_LIVE_PLAY);
    }
}
