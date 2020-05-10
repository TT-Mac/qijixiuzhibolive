package com.qiji.live.xiaozhibo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMGroupTipsElemMemberInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.base.LiveBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.GiftJson;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.SendGiftJson;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.inter.RecyclerViewItemClick;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.fragment.GiftListDialogFragment;
import com.qiji.live.xiaozhibo.ui.fragment.UserInfoDialogFragment;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.utils.ShareUtils;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.logic.TCChatEntity;
import com.qiji.live.xiaozhibo.logic.TCChatMsgListAdapter;
import com.qiji.live.xiaozhibo.logic.TCChatRoomMgr;
import com.qiji.live.xiaozhibo.logic.TCDanmuMgr;
import com.qiji.live.xiaozhibo.logic.TCFrequeControl;
import com.qiji.live.xiaozhibo.logic.TCPlayerMgr;
import com.qiji.live.xiaozhibo.logic.TCUserAvatarListAdapter;
import com.qiji.live.xiaozhibo.ui.customviews.TCHeartLayout;
import com.qiji.live.xiaozhibo.ui.customviews.TCInputTextMsgDialog;
import com.qiji.live.xiaozhibo.ui.fragment.TCLiveListFragment;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.qiji.live.xiaozhibo.utils.TDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import master.flame.danmaku.controller.IDanmakuView;

/**
 * Created by RTMP on 2016/8/4
 */
public class TCLivePlayerActivity extends LiveBaseActivity implements ITXLivePlayListener, View.OnClickListener, TCPlayerMgr.PlayerListener, TCInputTextMsgDialog.OnTextSendListener, TCChatRoomMgr.TCChatRoomListener {
    private static final String TAG = TCLivePlayerActivity.class.getSimpleName();

    private TXCloudVideoView mTXCloudVideoView;
    private TCInputTextMsgDialog mInputTextMsgDialog;
    private ListView mListViewMsg;

    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();

    private Handler mHandler = new Handler();

    private CircleImageView mHeadIcon;
    private ImageView mRecordBall;
    private TextView mtvPuserName;
    private TextView mMemberCount;
    private int mPageNum = 1;

    private String mPusherAvatar;

    private long mCurrentMemberCount = 0;
    private long mTotalMemberCount = 0;
    private long mHeartCount = 0;
    private long mLastedPraisedTime = 0;

    private boolean mPausing = false;
    private boolean mPlaying = false;
    private String mPusherNickname;
    private String mPusherId;
    private String mPlayUrl = "http://2527.vod.myqcloud.com/2527_000007d04afea41591336f60841b5774dcfd0001.f0.flv";
    private String mGroupId = "";
    private String mFileId = "";
    private String mUserId = "";
    private String mUserToken = "";
    private String mNickname = "";
    private String mHeadPic = "";

    private boolean mIsLivePlay;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;

    private TCPlayerMgr mTCPlayerMgr;
    private TCChatRoomMgr mTCChatRoomMgr;

    //头像列表控件
    private RecyclerView mUserAvatarList;
    private TCUserAvatarListAdapter mAvatarListAdapter;

    //点赞动画
    private TCHeartLayout mHeartLayout;
    //点赞频率控制
    private TCFrequeControl mLikeFrequeControl;

    //弹幕
    private TCDanmuMgr mDanmuMgr;

    //点播相关
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private SeekBar mSeekBar;
    private ImageView mPlayIcon;
    private TextView mTextProgress;

    //手势动画

    private TCSwipeAnimationController mTCSwipeAnimationController;
    private ImageView mBgImageView;
    private ImageView mIvFollow;
    private View mPushInfoView;
    private TextView mTvId;
    private LinearLayout mLlIncomeView;
    private ImageView mIvPrivateChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_play);

        Intent intent = getIntent();

        mPusherId = intent.getStringExtra(TCConstants.PUSHER_ID);
        mPlayUrl = intent.getStringExtra(TCConstants.PLAY_URL);
        mGroupId = intent.getStringExtra(TCConstants.GROUP_ID);
        mIsLivePlay = intent.getBooleanExtra(TCConstants.PLAY_TYPE, true);
        mPusherNickname = intent.getStringExtra(TCConstants.PUSHER_NAME);
        mPusherAvatar = intent.getStringExtra(TCConstants.PUSHER_AVATAR);
        mHeartCount = Long.decode(intent.getStringExtra(TCConstants.HEART_COUNT));
        mCurrentMemberCount = Long.decode(intent.getStringExtra(TCConstants.MEMBER_COUNT));
        mFileId = intent.getStringExtra(TCConstants.FILE_ID);
        mUserId = UserInfoMgr.getInstance().getUid();
        mUserToken = UserInfoMgr.getInstance().getToken();
        mNickname = UserInfoMgr.getInstance().getNickname();
        mHeadPic = UserInfoMgr.getInstance().getHeadPic();
        mShowGiftAnimator = (LinearLayout) findViewById(R.id.ll_show_gift_animator);

        initView();

        if(mTXCloudVideoView != null) {
            //mTXCloudVideoView.disableLog(false);
        }

        joinRoom();

        TCUtils.blurBgPic(this, mBgImageView, getIntent().getStringExtra(TCConstants.COVER_PIC), R.drawable.bg);

        //在这里停留，让列表界面卡住几百毫秒，给sdk一点预加载的时间，形成秒开的视觉效果
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 观众加入房间操作
     */
    public void joinRoom() {

        //初始化消息回调，当前存在：获取文本消息、用户加入/退出消息、群组解散消息、点赞消息、弹幕消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCPlayerMgr = TCPlayerMgr.getInstance();
        mTCPlayerMgr.setPlayerListener(this);

        if(mIsLivePlay) {
            //仅当直播时才进行执行加入直播间逻辑
            mTCChatRoomMgr.setMessageListener(this);
            mTCChatRoomMgr.joinGroup(mGroupId);
        }

        mTCPlayerMgr.enterGroup(mUserId,mUserToken, mPusherId, mIsLivePlay ? mGroupId : mFileId, mNickname, mHeadPic, mIsLivePlay ? 0 : 1);
        initData();
        startPlay();
    }

    /**
     * 初始化观看点播界面
     */
    private void initVodView() {

        View toolBar = findViewById(R.id.tool_bar);
        if (toolBar != null) {
            toolBar.setVisibility(View.GONE);
        }
        //左上直播信息
        mTvId = (TextView) findViewById(R.id.tv_live_id);

        mControllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);
        mtvPuserName = (TextView) findViewById(R.id.tv_broadcasting_time);
        mtvPuserName.setText(TCUtils.getLimitString(mPusherNickname, 10));
        mRecordBall = (ImageView) findViewById(R.id.iv_record_ball);
        mRecordBall.setVisibility(View.GONE);
        mHeadIcon = (CircleImageView) findViewById(R.id.iv_head_icon);
        mHeadIcon.setOnClickListener(this);
        showHeadIcon(mHeadIcon, mPusherAvatar);
        mMemberCount = (TextView) findViewById(R.id.tv_member_counts);
        mIvFollow = (ImageView) findViewById(R.id.iv_live_follow);

        mIvFollow.setOnClickListener(this);

        mTvIncomeNum = (TextView) findViewById(R.id.tv_live_income_num);
        //初始化观众列表
        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, mPusherId);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mMemberCount.setText(String.format(Locale.CHINA,"%s", mCurrentMemberCount + "人观看"));

        mPushInfoView = findViewById(R.id.layout_live_pusher_info);
//        if (head != null) {
//            head.setVisibility(View.GONE);
//        }
        View progressBar = findViewById(R.id.progressbar_container);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTextProgress = (TextView) findViewById(R.id.progress_time);
        mPlayIcon = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextProgress != null) {
                    mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress%3600)/60, (progress%3600) % 60, seekBar.getMax() / 3600, (seekBar.getMax()%3600) / 60, (seekBar.getMax()%3600) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTXLivePlayer.seek(seekBar.getProgress());
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });

        mBgImageView = (ImageView) findViewById(R.id.background);

        mLlIncomeView = (LinearLayout) findViewById(R.id.ll_income_view);
        mLlIncomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(TCLivePlayerActivity.this, WebViewActivity.class);
                a.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=appapi&m=contribute&uid=" + mPusherId);
                startActivity(a);
            }
        });


        changeAttentionBtnWHAndOtherAction(7);
    }

    //左上角关注按钮
    private void followEmcee() {
        mIvFollow.setVisibility(View.GONE);
        changeAttentionBtnWHAndOtherAction(1);
        UserInfoMgr.getInstance().attentionOrCancelUser(new SimpleActionListener() {
            @Override
            public void onSuccess() {
                //修改状态

            }

            @Override
            public void onFail(int code, String msg) {

            }
        },mPusherId);
    }


    //关注后修改直播左上角信息长度
    private void changeAttentionBtnWHAndOtherAction(int action ) {
        if(action == 1){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPushInfoView.getLayoutParams();
            params.width = (int) TDevice.dp2px(100);
            params.height = (int)TDevice.dp2px(35);
            mPushInfoView.setLayoutParams(params);

            mTCChatRoomMgr.sendNotifyMessage(UserInfoMgr.getInstance().getNickname() + "关注了主播");
            if(mIsLivePlay){
                //本地回显提示关注信息
                handleNotifyMsg(new SimpleUserInfo(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getNickname(),
                        UserInfoMgr.getInstance().getHeadPic(),UserInfoMgr.getInstance().getLevel()),UserInfoMgr.getInstance().getNickname() + "关注了主播");
            }


        }else if(action == 7){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPushInfoView.getLayoutParams();
            params.width = (int) TDevice.dp2px(100);
            params.height = (int)TDevice.dp2px(35);
            mPushInfoView.setLayoutParams(params);
        }else{
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPushInfoView.getLayoutParams();
            params.width = (int) TDevice.dp2px(150);
            params.height = (int)TDevice.dp2px(35);
            mPushInfoView.setLayoutParams(params);
        }

    }
    /**
     * 初始化观看直播界面
     */
    private void initLiveView() {
        mRlRootView = (RelativeLayout) findViewById(R.id.rl_play_root);

        mRlRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event);
            }
        });

        mTvIncomeNum = (TextView) findViewById(R.id.tv_live_income_num);

        mTvId = (TextView) findViewById(R.id.tv_live_id);

        mControllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mListViewMsg = (ListView) findViewById(R.id.im_msg_listview);
        mListViewMsg.setVisibility(View.VISIBLE);
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);
        mtvPuserName = (TextView) findViewById(R.id.tv_broadcasting_time);
        mtvPuserName.setText(TCUtils.getLimitString(mPusherNickname, 10));
        mRecordBall = (ImageView) findViewById(R.id.iv_record_ball);
        mRecordBall.setVisibility(View.GONE);

        mIvFollow = (ImageView) findViewById(R.id.iv_live_follow);

        mIvFollow.setOnClickListener(this);

        mPushInfoView = findViewById(R.id.layout_live_pusher_info);
        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, mPusherId);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        //用户列表点击事件
        mAvatarListAdapter.setRecyclerViewItemClick(new RecyclerViewItemClick() {
            @Override
            public void onItemClick(int position) {
                showUserInfoDialog(mAvatarListAdapter.getItem(position).uid);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mHeadIcon = (CircleImageView) findViewById(R.id.iv_head_icon);
        mHeadIcon.setOnClickListener(this);
        showHeadIcon(mHeadIcon, mPusherAvatar);
        mMemberCount = (TextView) findViewById(R.id.tv_member_counts);

        mCurrentMemberCount++;
        mMemberCount.setText(String.format(Locale.CHINA,"%s",mCurrentMemberCount + "人观看"));
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        danmakuView.setVisibility(View.VISIBLE);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        mBgImageView = (ImageView) findViewById(R.id.background);


        mLlIncomeView = (LinearLayout) findViewById(R.id.ll_income_view);
        mLlIncomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(TCLivePlayerActivity.this, WebViewActivity.class);
                a.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=appapi&m=contribute&uid=" + mPusherId);
                startActivity(a);
            }
        });

        mIvPrivateChat = (ImageView) findViewById(R.id.btn_private);
        mIvPrivateChat.setOnClickListener(this);

    }

    /**
    * @dw 用户列表点击弹窗
    * @param userId 点击用户id
    *
    * */
    private void showUserInfoDialog(String  userId) {
        UserInfoDialogFragment dialog = new UserInfoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("touid",userId);
        bundle.putString("liveid",mPusherId);
        bundle.putString("groupid",mGroupId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),"UserInfoDialogFragment");
    }


    private void initView() {
        if (mIsLivePlay) {
            initLiveView();
        } else {
            initVodView();
        }

    }
    private void initData() {

        IS_ON_LIVE = true;

        //判断是否关注
        if(mIsLivePlay){
            mTCPlayerMgr.requestIsAttention(mUserId, mPusherId, new TCPlayerMgr.IsFollowListener() {
                @Override
                public void onRequestCallback(int errorCode, LinkedTreeMap<String, String> linkedTreeMap) {

                    if(StringUtils.toInt(linkedTreeMap.get("isattent")) == 0 && mIsLivePlay){
                        mIvFollow.setVisibility(View.VISIBLE);
                    }else{
                        changeAttentionBtnWHAndOtherAction(1);
                    }
                }
            });
        }


        //获取收入
        mTCChatRoomMgr.requestGetIncome(mPusherId, new TCChatRoomMgr.OnGetIncomeListener() {
            @Override
            public void onRequestCallback(int errCode, LinkedTreeMap<String, String> res) {

                if(errCode == 1){
                    mTvIncomeNum.setText(res.get("votestotal"));
                }
            }
        });

        mTvId.setText("ID:" + mPusherId);

    }

    /**
     * 加载主播头像
     *
     * @param view   view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this,view,avatar,R.drawable.face);
    }

    private boolean checkPlayUrl() {
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl.startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mIsLivePlay) {
            if (mPlayUrl.startsWith("rtmp://")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
            } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://"))&& mPlayUrl.contains(".flv")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
            } else {
                Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) {
                if (mPlayUrl.contains(".flv")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
                } else if (mPlayUrl.contains(".m3u8")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
                } else if (mPlayUrl.toLowerCase().contains(".mp4")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
                } else {
                    Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void startPlay() {
        if (!checkPlayUrl()) {
            return;
        }

        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);
        mTXLivePlayer.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);

        int result;
        result = mTXLivePlayer.startPlay(mPlayUrl, mPlayType);

        if (0 != result) {

            Intent rstData = new Intent();

            if (-1 == result) {
                Log.d(TAG, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
//                Toast.makeText(getApplicationContext(), TCConstants.ERROR_MSG_NOT_QCLOUD_LINK, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT,TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            } else {
                Log.d(TAG, TCConstants.ERROR_RTMP_PLAY_FAILED);
//                Toast.makeText(getApplicationContext(), TCConstants.ERROR_RTMP_PLAY_FAILED, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT,TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            }

            mTXCloudVideoView.onPause();
            stopPlay(true);
            setResult(TCLiveListFragment.START_LIVE_PLAY,rstData);
            finish();
        } else {
            mPlaying = true;
        }
    }

    private void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    protected void showErrorAndQuit(String errorMsg) {

        mTXCloudVideoView.onPause();
        stopPlay(true);

        Intent rstData = new Intent();
        rstData.putExtra(TCConstants.ACTIVITY_RESULT,errorMsg);
        setResult(TCLiveListFragment.START_LIVE_PLAY,rstData);

        super.showErrorAndQuit(errorMsg);

    }

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();

        TXLog.d(TAG, "player broadcastReceiver receive exit app msg");
        //在被踢下线的情况下，执行退出前的处理操作：关闭rtmp连接、退出群组
        mTXCloudVideoView.onPause();
        stopPlay(true);
        //quitRoom();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head_icon:
                showUserInfoDialog(mPusherId);
                break;
            //关注
            case R.id.iv_live_follow:
                followEmcee();
                break;
            //礼物
            case R.id.btn_gift:
                showGiftDialogList();
                break;
            case R.id.btn_share:
                ShareUtils.showSharePopWindow(this,v);
                break;
            case R.id.btn_vod_back:
                finish();
                break;
            case R.id.btn_back:
                Intent rstData = new Intent();
                long memberCount = mCurrentMemberCount - 1;
                rstData.putExtra(TCConstants.MEMBER_COUNT, memberCount>=0 ? memberCount:0);
                rstData.putExtra(TCConstants.HEART_COUNT, mHeartCount);
                rstData.putExtra(TCConstants.PUSHER_ID, mPusherId);
                setResult(0,rstData);
                finish();
                break;
            case R.id.btn_like:
                if (mHeartLayout != null) {
                    mHeartLayout.addFavor();
                }

                //点赞发送请求限制
                if (mLikeFrequeControl == null) {
                    mLikeFrequeControl = new TCFrequeControl();
                    mLikeFrequeControl.init(2, 1);
                }
                if (mLikeFrequeControl.canTrigger()) {

                    //向后台发送点赞信息
                    mTCPlayerMgr.addHeartCount(mPusherId);
                    //向ChatRoom发送点赞消息
                    mTCChatRoomMgr.sendPraiseMessage(mHeartCount);
                    //自己回显
                    handlePraiseMsg(new SimpleUserInfo(mUserId,mNickname,UserInfoMgr.getInstance().getHeadPic(),UserInfoMgr.getInstance().getLevel()), String.valueOf(mHeartCount));

                    mHeartCount++;
                }
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.play_btn: {
                if (mPlaying) {
                    if (mVideoPause) {
                        mTXLivePlayer.resume();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                        }
                    } else {
                        mTXLivePlayer.pause();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_start);
                        }
                    }
                    mVideoPause = !mVideoPause;
                } else {
                    if (mPlayIcon != null) {
                        mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                    }
                    startPlay();
                }

            }
            break;
            //私信
            case R.id.btn_private:
                TCUtils.getInstanceToast("即将开放,尽请期待").show();
                break;
            default:
                break;
        }
    }

    //礼物列表
    private void showGiftDialogList() {

        final GiftListDialogFragment gift = new GiftListDialogFragment();
        gift.show(getSupportFragmentManager(),"GiftListDialogFragment");


        gift.setOnSendGiftInterface(new GiftListDialogFragment.OnSendGiftInterface() {
            @Override
            public void onSendGift(final GiftJson giftJson) {

                //判断是否被禁言
                TIMGroupManager.getInstance().getSelfInfo(mGroupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                        if(timGroupSelfInfo.getSilenceSeconds() == 0){
                            //请求接口礼物扣费
                            TCPlayerMgr.getInstance().requestSendGift(UserInfoMgr.getInstance().getToken(), UserInfoMgr.getInstance().getUid(),
                                    mPusherId,giftJson,"1" , mGroupId, new TCPlayerMgr.OnSendGiftListener() {
                                        @Override
                                        public void onRequestCallback(int errCode, SendGiftJson sendGiftJson,LinkedTreeMap<String,String> res) {
                                            if(errCode != 0){

                                                if(StringUtils.toInt(sendGiftJson.getType()) == 1){
                                                    Event.SendGift event = new Event.SendGift();
                                                    event.result = 1;
                                                    EventBus.getDefault().post(event);
                                                }

                                                //更余额
                                                Event.CoinChange event = new Event.CoinChange();
                                                event.coin = res.get("coin");
                                                EventBus.getDefault().post(event);
                                                UserInfoMgr.getInstance().getUserInfoBean().setCoin(res.get("coin"));
                                                UserInfoMgr.getInstance().getUserInfoBean().setLevel(res.get("level"));
                                                UserInfoMgr.getInstance().saveUserInfo();

                                                String sendMsg = mGson.toJson(sendGiftJson);
                                                //发送
                                                mTCChatRoomMgr.sendGiftMessage(sendMsg);
                                                handleGiftMsg(new SimpleUserInfo(mUserId,mNickname,mHeadPic,UserInfoMgr.getInstance().getLevel()),sendMsg);
                                            }

                                        }
                                    });
                        }else{

                            TCUtils.getInstanceToast("你已被禁言").show();
                        }
                    }
                });

            }
        });

    }

    //分享
    public void share(View v){
        ShareUtils.share(this,v.getId(),UserInfoMgr.getInstance().getUserInfoBean());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();
        if (!mIsLivePlay && !mVideoPause && mTXLivePlayer != null) {
            mTXLivePlayer.resume();
        }
        else {
            if (mDanmuMgr != null) {
                mDanmuMgr.resume();
            }
            if (mPausing) {
                mPausing = false;

                if (Build.VERSION.SDK_INT >= 23) {
                    startPlay();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mTXCloudVideoView.onPause();
        if (!mIsLivePlay && mTXLivePlayer != null) {
            mTXLivePlayer.pause();
        } else {

            if (mDanmuMgr != null) {
                mDanmuMgr.pause();
            }

            mPausing = true;

            if (Build.VERSION.SDK_INT >= 23) {
                stopPlay(false);
            }
        }

    }

    //弹窗关注订阅监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.DialogFollow event){

        if(StringUtils.toInt(event.uid) == StringUtils.toInt(mPusherId) && event.action == 1){

            mIvFollow.setVisibility(View.GONE);
            changeAttentionBtnWHAndOtherAction(1);


        }else if(StringUtils.toInt(event.uid) == StringUtils.toInt(mPusherId) && event.action == 0){

            mIvFollow.setVisibility(View.VISIBLE);
            changeAttentionBtnWHAndOtherAction(0);
        }
    }

    //发言回调监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.SendMsg event) {

        if(event.action == TCConstants.IMCMD_PAILN_TEXT){
            if(event.errCode == -1){
                showToast("你已被禁言");
                return;
            }
            //消息回显
            TCChatEntity entity = new TCChatEntity();
            entity.setSenderName("我");
            entity.setContext(event.msg.toString());
            entity.setLevel(UserInfoMgr.getInstance().getLevel());
            entity.setType(TCConstants.TEXT_TYPE);
            notifyMsg(entity);
        }

    }
   /* //发言弹窗状态回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.InputDialogStatusChange event) {

        if(event.status == 1){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mListViewMsg.getLayoutParams();
            params.setMargins(0,50,0,0);
            mListViewMsg.setLayoutParams(params);
        }

    }*/

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        IS_ON_LIVE = false;
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
        quitRoom();
    }

    public void quitRoom() {
        if(mIsLivePlay) {

            mTCChatRoomMgr.quitGroup(mGroupId);
            mTCChatRoomMgr.removeMsgListener();
            mTCPlayerMgr.quitGroup(mUserId, mPusherId, mGroupId, 0);

        } else {
            mTCPlayerMgr.quitGroup(mUserId, mPusherId, mFileId, 1);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
//        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
////            stopLoadingAnimation();
//        } else
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress%3600) / 60, progress % 60, duration / 3600, (duration%3600) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            if(!mIsLivePlay){
                showErrorAndQuit("直播回放未生成");
            }

            //showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay(false);
            mVideoPause = false;
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%s","00:00:00/00:00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            if (mPlayIcon != null) {
                mPlayIcon.setBackgroundResource(R.drawable.play_start);
            }
        }
//        else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {
//            startLoadingAnimation();
//        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        Log.d(TAG, "Current status: " + status.toString());
        if(status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            if(mTXLivePlayer != null) mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        }
        else if(mTXLivePlayer != null) mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
    }

    private void notifyMsg(final TCChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                if(entity.getType() == TCConstants.PRAISE) {
//                    if(mArrayListChatEntity.contains(entity))
//                        return;
//                }
                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 加入群组/退出群组回调
     * @param errCode 错误码
     */
    @Override
    public void onRequestCallback(int errCode) {
        if (errCode != 0) {
            if (TCConstants.ERROR_GROUP_NOT_EXIT == errCode) {
                showErrorAndQuit(TCConstants.ERROR_MSG_GROUP_NOT_EXIT);
            }
            else if(TCConstants.ERROR_QALSDK_NOT_INIT == errCode){
                ((TCApplication)getApplication()).initSDK();
                joinRoom();
            }
            else {
                showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + errCode);
            }
        } else {
            if (null != mTXLivePlayer) {
                getGroupMembersList();

                //直播下提示系统消息
                if(mIsLivePlay){

                    sendSystemMsg();
                }

            }
        }
    }



    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if(code == 0){
            Log.d(TAG, "onJoin group success" + msg);
        } else if (TCConstants.NO_LOGIN_CACHE == code) {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_NO_LOGIN_CACHE);
        } else {
            TXLog.d(TAG, "onJoin group failed" + msg);
            //showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + code);
            showErrorAndQuit("直播已结束");
        }
    }

    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        //消息发送成功后回显
        if(errCode == 0) {
            TIMElemType elemType =  timMessage.getElement(0).getType();
            if(elemType == TIMElemType.Text) {
                Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
            } else if(elemType == TIMElemType.Custom) {
                //custom消息存在消息回调,此处显示成功失败
                Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
            }
        } else {
            Log.d(TAG, "onSendMsgfail:" + errCode);
        }

    }

    @Override
    public void onReceiveMsg(int type, SimpleUserInfo userInfo, String content) {
        switch (type) {
            case TCConstants.IMCMD_ENTER_LIVE:
                handleMemberJoinMsg(userInfo);
                break;
            case TCConstants.IMCMD_EXIT_LIVE:
                handleMemberQuitMsg(userInfo);
                break;
            case TCConstants.IMCMD_PRAISE:
                handlePraiseMsg(userInfo,content);
                break;
            case TCConstants.IMCMD_PAILN_TEXT:
                handleTextMsg(userInfo, content);
                break;
            case TCConstants.IMCMD_DANMU:
                handleDanmuMsg(userInfo, content);
                break;
            case TCConstants.IMCMD_GIFT:
                handleGiftMsg(userInfo,content);
                break;
            case TCConstants.IMCMD_SHUT_UP:
                handleShutUp(userInfo,content);
                break;
            case TCConstants.IMCMD_SET_MANAGE:
                handleSetOrCancleManage(userInfo,true,content);
                break;
            case TCConstants.IMCMD_SET_MANAGE_CANCEL:
                handleSetOrCancleManage(userInfo,false,content);
                break;
            case TCConstants.IMCMD_NOTIFY:
                handleNotifyMsg(userInfo,content);
                break;
            default:
        }
    }



    private void sendSystemMsg() {
        //左下角显示用户加入消息
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("直播消息");
        entity.setLevel("1");
        entity.setContext(getResources().getString(R.string.system_msg));
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    //通知类型消息
    private void handleNotifyMsg(SimpleUserInfo userInfo, String content) {
        //左下角显示用户加入消息
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        entity.setLevel("1");
        entity.setContext(content);
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    /**
     * @dw 设置管理员
     * @param b true为设置，false为取消
     * */
    private void handleSetOrCancleManage(SimpleUserInfo userInfo,boolean b,String content) {

        try {
            JSONObject object = new JSONObject(content);
            //左下角显示用户加入消息
            TCChatEntity entity = new TCChatEntity();

            if(b){
                entity.setSenderName("通知");
                entity.setLevel(userInfo.level);
                if (userInfo.user_nicename.equals(""))
                    entity.setContext(object.getString("uid") + "被设置为管理员");
                else
                    entity.setContext(object.getString("name") + "被设置为管理员");
                entity.setType(TCConstants.MEMBER_ENTER);
                notifyMsg(entity);
            }else{
                entity.setSenderName("通知");
                entity.setLevel(userInfo.level);
                if (userInfo.user_nicename.equals(""))
                    entity.setContext(object.getString("uid") + "被取消管理员");
                else
                    entity.setContext(object.getString("name") + "被取消管理员");
                entity.setType(TCConstants.MEMBER_ENTER);
                notifyMsg(entity);
            }

            //如果是自己
            if(StringUtils.toInt(object.getString("uid")) == StringUtils.toInt(mUserId)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage(b ? "你已经被设置为管理员" : "你已经被取消管理员");
                builder.setNegativeButton("确定",null);
                builder.create().show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
    * @dw 禁言
    * @param content 被禁言用户id
    * */
    private void handleShutUp(SimpleUserInfo userInfo, String content) {

        try {
            JSONObject object = new JSONObject(content);

            //左下角显示用户加入消息
            TCChatEntity entity = new TCChatEntity();
            entity.setSenderName("通知");
            entity.setLevel(userInfo.level);
            if (userInfo.user_nicename.equals(""))
                entity.setContext(object.getString("uid") + "被禁言5分钟");
            else
                entity.setContext(object.getString("name") + "被禁言5分钟");
            entity.setType(TCConstants.MEMBER_ENTER);
            notifyMsg(entity);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * @dw 礼物发送
     * @param userInfo 发送用户信息
     * @param content 发送内容
     * */
    protected void handleGiftMsg(SimpleUserInfo userInfo, String content) {
        SendGiftJson sendGiftJson = mGson.fromJson(content,SendGiftJson.class);
        showGiftInit(sendGiftJson);
        TCChatEntity entity = new TCChatEntity();

        entity.setSenderName(userInfo.user_nicename);
        entity.setLevel(userInfo.level);
        if (userInfo.user_nicename.equals(""))
            entity.setContext("我送了一个" + sendGiftJson.getGiftname());
        else
            entity.setContext("我送了一个" + sendGiftJson.getGiftname());

        entity.setType(TCConstants.GIFT);
        notifyMsg(entity);
    }

    //用户加入房间
    public void handleMemberJoinMsg(SimpleUserInfo userInfo) {

        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;

        mCurrentMemberCount++;
        mTotalMemberCount++;
        mMemberCount.setText(String.format(Locale.CHINA,"%s",mCurrentMemberCount + "人观看"));

        //左下角显示用户加入消息
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        entity.setLevel(userInfo.level);
        if (userInfo.user_nicename.equals(""))
            entity.setContext(userInfo.uid + "加入直播");
        else
            entity.setContext(userInfo.user_nicename + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    public void handleMemberQuitMsg(SimpleUserInfo userInfo) {

        if(mCurrentMemberCount > 0)
            mCurrentMemberCount--;
        else
            Log.d(TAG, "接受多次退出请求，目前人数为负数");

        mMemberCount.setText(String.format(Locale.CHINA,"%s",mCurrentMemberCount + "人观看"));

        mAvatarListAdapter.removeItem(userInfo.uid);

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        entity.setLevel(userInfo.level);
        if (userInfo.user_nicename.equals(""))
            entity.setContext(userInfo.uid + "退出直播");
        else
            entity.setContext(userInfo.user_nicename + "退出直播");
        entity.setType(TCConstants.MEMBER_EXIT);
        notifyMsg(entity);
    }


    @Override
    public void onGroupDelete() {
        showErrorAndQuit(TCConstants.ERROR_MSG_LIVE_STOPPED);
    }


    //点赞
    public void handlePraiseMsg(SimpleUserInfo userInfo,String content) {
        TCChatEntity entity = new TCChatEntity();

        entity.setSenderName(userInfo.user_nicename);
        entity.setLevel(userInfo.level);
        if (userInfo.user_nicename.equals(""))
            entity.setContext(/*userInfo.uid + */"我点亮了");
        else
            entity.setContext(/*userInfo.user_nicename + */"我点亮了");
        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }
        mHeartCount++;

        //不是第一次点亮
        if(StringUtils.toInt(content) > 1){
            return;
        }
        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    //弹幕
    public void handleDanmuMsg(SimpleUserInfo userInfo, String text) {
        /*TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.user_nicename);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        entity.setLevel(userInfo.level);
        notifyMsg(entity);*/
        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(userInfo.avatar, userInfo.user_nicename, text);
        }
    }

    public void handleTextMsg(SimpleUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.user_nicename);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        entity.setLevel(userInfo.level);
        notifyMsg(entity);
    }

    /**
     * 拉取用户头像列表
     */
    public void getGroupMembersList() {
        mTCPlayerMgr.fetchGroupMembersList(mPusherId, mIsLivePlay ? mGroupId : mFileId, mPageNum, 20,
                new TCPlayerMgr.OnGetMembersListener() {
                    @Override
                    public void onGetMembersList(int retCode, int totalCount, List<SimpleUserInfo> membersList) {
                        if (retCode == 0) {
                            mTotalMemberCount = totalCount;
                            mCurrentMemberCount = totalCount;
                            mMemberCount.setText("" + mTotalMemberCount + "人观看");
                            for (SimpleUserInfo userInfo : membersList)
                                mAvatarListAdapter.addItem(userInfo);
                        } else {
                            TXLog.d(TAG, "getGroupMembersList failed");
                        }
                    }
                });
    }

    /**
     * TextInputDialog发送回调
     * @param msg 文本信息
     * @param danmuOpen 是否打开弹幕
     */
    @Override
    public void onTextSend(final String msg, boolean danmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        //消息回显
        /*if(!danmuOpen){
            TCChatEntity entity = new TCChatEntity();
            entity.setSenderName("我");
            entity.setContext(msg);
            entity.setLevel(UserInfoMgr.getInstance().getLevel());
            entity.setType(TCConstants.TEXT_TYPE);
            notifyMsg(entity);
        }*/


        if (danmuOpen) {
            //判断是否被禁言
            TIMGroupManager.getInstance().getSelfInfo(mGroupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                    if(timGroupSelfInfo.getSilenceSeconds() == 0){
                        //请求接口扣费
                        mTCPlayerMgr.requestSendDanmu(mUserId, mUserToken, mPusherId, mGroupId, "1", "1", new TCPlayerMgr.OnSendDanmuListener() {
                            @Override
                            public void onRequestCallback(int errCode, LinkedTreeMap<String, String> res) {

                                if(errCode != -1){
                                    //更新余额
                                    UserInfoMgr.getInstance().getUserInfoBean().setCoin(res.get("coin"));
                                    UserInfoMgr.getInstance().saveUserInfo();
                                    UserInfoMgr.getInstance().updateUserInfo();
                                    //更新收入
                                    updateIncome(String.valueOf(res.get("totalcoin")));
                                    if (mDanmuMgr != null) {
                                        mDanmuMgr.addDanmu(mHeadPic, mNickname, msg);
                                    }
                                    mTCChatRoomMgr.sendDanmuMessage(msg);
                                }
                            }
                        });
                    }else{
                        showToast("你已被禁言");
                    }
                }
            });


        } else {
            mTCChatRoomMgr.sendTextMessage(msg);
        }
    }



    //启动activity
    public static void startLivePlayerActivity(Activity context, LiveBean item, int resultCode){

        Intent intent = new Intent(context, TCLivePlayerActivity.class);
        intent.putExtra(TCConstants.PUSHER_ID, item.getUid());
        intent.putExtra(TCConstants.PLAY_URL,item.getPlay_url());
        intent.putExtra(TCConstants.PUSHER_NAME, item.getUser_nicename() == null ? item.getUid() : item.getUser_nicename());
        intent.putExtra(TCConstants.PUSHER_AVATAR, item.getAvatar());
        intent.putExtra(TCConstants.HEART_COUNT, item.getNums());
        intent.putExtra(TCConstants.MEMBER_COUNT, item.getLight());
        intent.putExtra(TCConstants.GROUP_ID, item.getGroupid());
        intent.putExtra(TCConstants.PLAY_TYPE, StringUtils.toInt(item.getIslive()) == 1 ? true:false);
        intent.putExtra(TCConstants.FILE_ID, item.getFile_id());
        intent.putExtra(TCConstants.COVER_PIC, item.getAvatar());
        context.startActivityForResult(intent,resultCode);
    }






}
