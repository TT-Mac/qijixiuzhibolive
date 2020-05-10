package com.qiji.live.xiaozhibo.ui;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.LiveBaseActivity;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.base.TCHWSupportList;
import com.qiji.live.xiaozhibo.bean.SendGiftJson;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.inter.RecyclerViewItemClick;
import com.qiji.live.xiaozhibo.logic.TCPlayerMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.fragment.UserInfoDialogFragment;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.logic.TCChatEntity;
import com.qiji.live.xiaozhibo.logic.TCChatMsgListAdapter;
import com.qiji.live.xiaozhibo.logic.TCChatRoomMgr;
import com.qiji.live.xiaozhibo.logic.TCDanmuMgr;
import com.qiji.live.xiaozhibo.logic.TCFrequeControl;
import com.qiji.live.xiaozhibo.logic.TCLoginMgr;
import com.qiji.live.xiaozhibo.logic.TCPusherMgr;
import com.qiji.live.xiaozhibo.logic.TCSimpleUserInfo;
import com.qiji.live.xiaozhibo.logic.TCUserAvatarListAdapter;
import com.qiji.live.xiaozhibo.logic.TCUserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.BeautyDialogFragment;
import com.qiji.live.xiaozhibo.ui.customviews.DetailDialogFragment;
import com.qiji.live.xiaozhibo.ui.customviews.TCAudioControl;
import com.qiji.live.xiaozhibo.ui.customviews.TCHeartLayout;
import com.qiji.live.xiaozhibo.ui.customviews.TCInputTextMsgDialog;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.TXRtmpApi;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import master.flame.danmaku.controller.IDanmakuView;

/**
 * Created by RTMP on 2016/8/4
 */
public class TCLivePublisherActivity extends LiveBaseActivity implements ITXLivePushListener, View.OnClickListener, TCPusherMgr.PusherListener, BeautyDialogFragment.SeekBarCallback, TCInputTextMsgDialog.OnTextSendListener, TCChatRoomMgr.TCChatRoomListener,TXRtmpApi.IRtmpDataListener {
    private static final String TAG = TCLivePublisherActivity.class.getSimpleName();



    private TXCloudVideoView mTXCloudVideoView;
    private ListView mListViewMsg;
    private TCInputTextMsgDialog mInputTextMsgDialog;

    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    private BeautyDialogFragment mBeautyDialogFragment;
//    private SeekBar mWhiteningSeekBar;
//    private SeekBar mBeautySeekBar;
    private Button mFlashView;

    //头像列表控件
    private RecyclerView mUserAvatarList;
    private TCUserAvatarListAdapter mAvatarListAdapter;
    private float mScreenHeight;

    private CircleImageView mHeadIcon;
    private ImageView mRecordBall;
    private TextView mBroadcastTime;
    private TextView mHostName;
    private TextView mMemberCount;
    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;


    private long mSecond = 0;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;

    private int mBeautyLevel = 0;
    private int mWhiteningLevel = 0;

    private long lTotalMemberCount = 0;
    private long lMemberCount = 0;
    private long lHeartCount = 0;

    private TXLivePusher mTXLivePusher;
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

    private Handler mHandler = new Handler();

    private boolean mFlashOn = false;
    private boolean mPasuing = false;

    private String mPushUrl;
    private String mRoomId;
    private String mUserId;
    private String mUserToken;
    private String mTitle;
    private String mCoverPicUrl;
    private String mHeadPicUrl;
    private String mNickName;
    private String mLocation;

    private TCPusherMgr mTCPusherMgr;
    private TCChatRoomMgr mTCChatRoomMgr;

    private TCAudioControl mAudioCtrl;
    private Button mBtnAudioCtrl;
    private LinearLayout mAudioPluginLayout;
    private Button mBtnAudioEffect;
    private Button mBtnAudioClose;
    private TXAudioPlayer mAudioPlayer;
    //点赞动画
    private TCHeartLayout mHeartLayout;
    //点赞频率控制
    private TCFrequeControl mLikeFrequeControl;

    //弹幕
    private TCDanmuMgr  mDanmuMgr;

    private TCSwipeAnimationController mTCSwipeAnimationController;

    //groupid
    private String mGroupId;
    private TextView mTvId;
    private LinearLayout mLlIncomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_publish);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(TCConstants.USER_ID);
        mUserToken = intent.getStringExtra(TCConstants.USER_TOKEN);
        mPushUrl = intent.getStringExtra(TCConstants.PUBLISH_URL);
        mTitle = intent.getStringExtra(TCConstants.ROOM_TITLE);
        mCoverPicUrl = intent.getStringExtra(TCConstants.COVER_PIC);
        mHeadPicUrl = intent.getStringExtra(TCConstants.USER_HEADPIC);
        mNickName = intent.getStringExtra(TCConstants.USER_NICK);
        mLocation = intent.getStringExtra(TCConstants.USER_LOC);

        initView();

        mBeautyDialogFragment = new BeautyDialogFragment();

        //打开推流日志
        if(mTXCloudVideoView != null) {
            //mTXCloudVideoView.disableLog(false);
        }

        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        //初始化消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCChatRoomMgr.setMessageListener(this);

        mTCPusherMgr = TCPusherMgr.getInstance();
        mTCPusherMgr.setPusherListener(this);

        mTCChatRoomMgr.createGroup();

        TXRtmpApi.setRtmpDataListener(this);

        initData();
    }



    @Override
    public void onPcmData(byte[] pcm, final int sampleRate, int channel, long nPTS) {
        if (mAudioPlayer == null) {
            mAudioPlayer = new TXAudioPlayer();
            mAudioPlayer.setAudioParam(sampleRate,channel,16);
            mAudioPlayer.start();
        }
        if (mAudioPlayer != null) {
            mAudioPlayer.play(pcm, nPTS);
        }
    }

    @Override
    public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3, long l) {

    }

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();

        TXLog.d(TAG, "publisher broadcastReceiver receive exit app msg");
        //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
        stopRecordAnimation();
        mTXCloudVideoView.onPause();
        stopPublish();
        quitRoom();
    }

    private void initView() {

        mTvId = (TextView) findViewById(R.id.tv_live_id);

        mRlRootView = (RelativeLayout) findViewById(R.id.rl_publish_root);

        mRlRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event);
            }
        });

        mTvIncomeNum = (TextView) findViewById(R.id.tv_live_income_num);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);

        mControllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);

        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);

        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, TCLoginMgr.getInstance().getLastUserInfo().identifier  );
        mUserAvatarList.setAdapter(mAvatarListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mShowGiftAnimator = (LinearLayout) findViewById(R.id.ll_show_gift_animator);

        mListViewMsg = (ListView) findViewById(R.id.im_msg_listview);
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        mFlashView = (Button) findViewById(R.id.flash_btn);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mBroadcastTime = (TextView) findViewById(R.id.tv_broadcasting_time);
        mBroadcastTime.setText(String.format(Locale.US,"%s","00:00:00"));
        mRecordBall = (ImageView) findViewById(R.id.iv_record_ball);

        mHeadIcon = (CircleImageView) findViewById(R.id.iv_head_icon);
        mHeadIcon.setOnClickListener(this);
        showHeadIcon(mHeadIcon, UserInfoMgr.getInstance().getHeadPic());
        mMemberCount = (TextView) findViewById(R.id.tv_member_counts);
        mMemberCount.setText("0" + "人观看");

        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        //AudioControl
        mBtnAudioCtrl = (Button) findViewById(R.id.btn_audio_ctrl);
        mAudioCtrl = (TCAudioControl) findViewById(R.id.layoutAudioControlContainer);
        mAudioPluginLayout = (LinearLayout)findViewById(R.id.audio_plugin);
        //mAudioCtrl.setPluginLayout(mAudioPluginLayout);
        mBtnAudioEffect = (Button)findViewById(R.id.btn_audio_effect);
        mBtnAudioClose = (Button)findViewById(R.id.btn_audio_close);

        //用户列表点击事件
        mAvatarListAdapter.setRecyclerViewItemClick(new RecyclerViewItemClick() {
            @Override
            public void onItemClick(int position) {
                showUserInfoDialog(mAvatarListAdapter.getItem(position).uid);
            }
        });

        mLlIncomeView = (LinearLayout) findViewById(R.id.ll_income_view);
        mLlIncomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(TCLivePublisherActivity.this, WebViewActivity.class);
                a.putExtra("url", TCConstants.SVR_POST_URL + "/index.php?g=appapi&m=contribute&uid=" + mUserId);
                startActivity(a);
            }
        });

    }


    private void initData() {
        IS_ON_LIVE = true;
        //获取收入
        mTCChatRoomMgr.requestGetIncome(mUserId, new TCChatRoomMgr.OnGetIncomeListener() {
            @Override
            public void onRequestCallback(int errCode, LinkedTreeMap<String, String> res) {

                if(errCode == 1){
                    mTvIncomeNum.setText(res.get("votestotal"));
                }
            }
        });

        mTvId.setText("ID:" + mUserId);
    }

    private void startPublish() {
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(this);
            mTXLivePusher.setPushListener(this);
            mTXPushConfig.setAutoAdjustBitrate(false);
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
            mTXPushConfig.setVideoBitrate(1000);
            if(TCHWSupportList.isHWVideoEncodeSupport()){
                mTXPushConfig.setHardwareAcceleration(true);
            }
            //切后台推流图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish,options);
            mTXPushConfig.setPauseImg(bitmap);
            mTXLivePusher.setConfig(mTXPushConfig);
        }
        mAudioCtrl.setPusher(mTXLivePusher);
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        //默认不开启美颜
        if (!mTXLivePusher.setBeautyFilter(TCUtils.filtNumber(9, 100, mBeautyLevel), TCUtils.filtNumber(3, 100, mWhiteningLevel))) {
            Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
        }
        //mBeautySeekBar.setProgress(100);

        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.startPusher(mPushUrl);
    }

    private void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
        if(mAudioCtrl != null){
            mAudioCtrl.unInit();
            mAudioCtrl = null;
        }
    }

    /**
     * 加载主播头像
     * @param view view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.drawable.face);
    }

    private ObjectAnimator mObjAnim;
    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {

        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();

        //直播时间
        if(mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {

        if (null != mObjAnim)
            mObjAnim.cancel();

        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

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
        if(!danmuOpen){
            TCChatEntity entity = new TCChatEntity();
            entity.setSenderName("我");
            entity.setContext(msg);
            entity.setLevel(UserInfoMgr.getInstance().getLevel());
            entity.setType(TCConstants.TEXT_TYPE);
            notifyMsg(entity);
        }

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
                        mTCPusherMgr.requestSendDanmu(mUserId, mUserToken, mUserId, mGroupId, "1", "1", new TCPlayerMgr.OnSendDanmuListener() {
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
                                        mDanmuMgr.addDanmu(UserInfoMgr.getInstance().getHeadPic(),UserInfoMgr.getInstance().getNickname(),msg);
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

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            Log.i(TAG, "timeTask ");
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mTCSwipeAnimationController.isMoving())
                        mBroadcastTime.setText(TCUtils.formattedTime(mSecond));
                }
            });
//            if (MySelfInfo.getInstance().getIdStatus() == TCConstants.HOST)
//                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    public void showDetailDialog() {
        //确认则显示观看detail
        stopRecordAnimation();
        DetailDialogFragment dialogFragment = new DetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("time", TCUtils.formattedTime(mSecond));
        args.putString("heartCount", String.format(Locale.CHINA, "%s", lHeartCount));
        args.putString("totalMemberCount", String.format(Locale.CHINA, "%s", lTotalMemberCount));
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getFragmentManager(), "");

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
        bundle.putString("liveid",mUserId);
        bundle.putString("groupid",mGroupId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),"UserInfoDialogFragment");
    }
    /**
     * 显示确认消息
     * @param msg 消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(String msg, Boolean isError) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    quitRoom();
                    stopRecordAnimation();
                    showDetailDialog();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            stopPublish();
            quitRoom();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopRecordAnimation();
                    showDetailDialog();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //后台系统消息监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.System event){

        if(event.action == 1){

            stopPublish();
            quitRoom();
            stopRecordAnimation();

            new AlertDialog.Builder(this).
                    setTitle("提示")
                    .setMessage("直播涉嫌违规")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showDetailDialog();
                        }
                    }).create().show();

        }

    }

    /**
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {

        mTCChatRoomMgr.deleteGroup();
        mTCPusherMgr.changeLiveStatus(mUserId, mUserToken,TCPusherMgr.TCLiveStatus_Offline);
    }

    @Override
    public void onBackPressed() {

        showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmuMgr != null){
            mDanmuMgr.resume();
        }
        mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;

            if (mTXLivePusher != null) {
                mTXLivePusher.resumePusher();
                mTXLivePusher.startCameraPreview(mTXCloudVideoView);
                mTXLivePusher.resumeBGM();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmuMgr != null){
            mDanmuMgr.pause();
        }
        mTXCloudVideoView.onPause();
        if(mTXLivePusher != null){
            mTXLivePusher.pauseBGM();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        mPasuing = true;
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }


        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDanmuMgr != null){
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        IS_ON_LIVE = false;
        stopRecordAnimation();
        mTXCloudVideoView.onDestroy();

        stopPublish();
        TXRtmpApi.setRtmpDataListener(null);
        mTCChatRoomMgr.removeMsgListener();
        mTCPusherMgr.setPusherListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head_icon:
                showUserInfoDialog(mUserId);
                break;
            case R.id.switch_cam:
                mTXLivePusher.switchCamera();
                break;
            case R.id.flash_btn:
                if(!mTXLivePusher.turnOnFlashLight(!mFlashOn)) {
                    Toast.makeText(getApplicationContext(), "打开闪光灯失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFlashOn = !mFlashOn;
                mFlashView.setBackgroundDrawable(mFlashOn ?
                        getResources().getDrawable(R.drawable.icon_flash_pressed):
                        getResources().getDrawable(R.drawable.icon_flash));

                break;
            case R.id.beauty_btn:
                mBeautyDialogFragment.show(getFragmentManager(), "");
                break;
            case R.id.btn_close:
                showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
//                for(int i = 0; i< 100; i++)
//                    mHeartLayout.addFavor();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_audio_ctrl:
                mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_audio_effect:
                //mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_audio_close:
                //mAudioCtrl.stopBGM();
               // mAudioPluginLayout.setVisibility(View.GONE);
                //mAudioCtrl.setVisibility(View.GONE);
                break;
            default:
                //mLayoutFaceBeauty.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    protected void showErrorAndQuit(String errorMsg) {

        mTXCloudVideoView.onPause();
        stopPublish();
        quitRoom();
        stopRecordAnimation();

        super.showErrorAndQuit(errorMsg);

    }

    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
                showComfirmDialog(TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限，弹对话框强提醒，并退出
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) { //未获得麦克风权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_MIC_FAIL);
            }
            else {
                //其他错误弹Toast弱提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();

                mTXCloudVideoView.onPause();
                TCPusherMgr.getInstance().changeLiveStatus(mUserId,mUserToken, TCPusherMgr.TCLiveStatus_Offline);
                stopRecordAnimation();
                finish();
            }
        }

        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
            mTXPushConfig.setVideoBitrate(700);
            mTXPushConfig.setHardwareAcceleration(false);
            mTXLivePusher.setConfig(mTXPushConfig);
        }

		if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
            TCPusherMgr.getInstance().changeLiveStatus(mUserId,mUserToken, TCPusherMgr.TCLiveStatus_Online);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

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
     * 向服务器获取推流地址 回调
     * @param errCode 错误码，0表示获取成功
     * @param groupId 群ID
     * @param pusherUrl 推流地址
     */
    @Override
    public void onGetPusherUrl(int errCode, String groupId, String pusherUrl) {
        if (errCode == 0) {
            mPushUrl = pusherUrl;
            startRecordAnimation();
            //开启推流
            startPublish();
        } else {
            if (null == groupId) {
                showErrorAndQuit(TCConstants.ERROR_MSG_CREATE_GROUP_FAILED + errCode);
            } else {
                showErrorAndQuit(TCConstants.ERROR_MSG_GET_PUSH_URL_FAILED + errCode);
            }
        }
    }

    @Override
    public void onChangeLiveStatus(int errCode) {
        Log.d(TAG, "onChangeLiveStatus:" + errCode);
    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if(0 == code) {
            //获取推流地址
            Log.d(TAG, "onJoin group success" + msg);
            //赋值groupId
            mGroupId = msg;
            mTCPusherMgr.getPusherUrl(mUserId, mUserToken,msg, mTitle, mCoverPicUrl, mNickName, mHeadPicUrl, mLocation,UserInfoMgr.getInstance().getUserInfoBean().getAvatar_thumb());
            sendSystemMsg();
        } else if (TCConstants.NO_LOGIN_CACHE == code) {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_NO_LOGIN_CACHE);
        } else {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + code);
        }
    }

    /**
     * 消息发送回调
     * @param errCode 错误码，0代表发送成功
     * @param timMessage 发送的TIM消息
     */
    @Override
    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        if (timMessage != null)
            if(errCode == 0) {
                TIMElemType elemType = timMessage.getElement(0).getType();
                if(elemType == TIMElemType.Text) {
                    //发送文本消息成功
                    Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
                } else if(elemType == TIMElemType.Custom) {
                    Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
                }

            } else {
                Log.d(TAG, "onSendMsgfail:" + errCode + " msg:" + timMessage.getMsgId());
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
                break;
        }
    }

    //发送系统信息
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

    public void handleTextMsg(SimpleUserInfo userInfo, String text) {

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.user_nicename);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        entity.setLevel(userInfo.level);
        notifyMsg(entity);
    }


    public void handleMemberJoinMsg(SimpleUserInfo userInfo) {

        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;

        lTotalMemberCount++;
        lMemberCount++;
        mMemberCount.setText(String.format(Locale.CHINA,"%s",lMemberCount + "人观看"));

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        entity.setLevel(userInfo.level);
        if(userInfo.user_nicename.equals(""))
            entity.setContext(userInfo.uid + "加入直播");
        else
            entity.setContext(userInfo.user_nicename + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    public void handleMemberQuitMsg(SimpleUserInfo userInfo) {
        if(lMemberCount > 0)
            lMemberCount--;
        else
            Log.d(TAG, "接受多次退出请求，目前人数为负数");
        mMemberCount.setText(String.format(Locale.CHINA,"%s",lMemberCount + "人观看"));

        mAvatarListAdapter.removeItem(userInfo.uid);

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        entity.setLevel(userInfo.level);
        if(userInfo.user_nicename.equals(""))
            entity.setContext(userInfo.uid + "退出直播");
        else
            entity.setContext(userInfo.user_nicename + "退出直播");
        entity.setType(TCConstants.MEMBER_EXIT);
        notifyMsg(entity);
    }

    @Override
    public void triggerSearch(String query, Bundle appSearchData) {
        super.triggerSearch(query, appSearchData);
    }

    public void handlePraiseMsg(SimpleUserInfo userInfo,String content) {

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.user_nicename);
        entity.setLevel(userInfo.level);
        if(userInfo.user_nicename.equals(""))
            entity.setContext(/*userInfo.uid + */"我点亮了");
        else
            entity.setContext(/*userInfo.user_nicename + */"我点亮了");

        //点赞接收请求限制
       /* if (mLikeFrequeControl == null) {
            mLikeFrequeControl = new TCFrequeControl();
            mLikeFrequeControl.init(10, 1);
        }
        if (mLikeFrequeControl.canTrigger() && mHeartLayout != null) {
            mHeartLayout.addFavor();
        }*/
        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }
        lHeartCount++;
        //不是第一次点亮
        if(StringUtils.toInt(content) > 1){
            return;
        }
        //todo：修改显示类型
        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    public void handleDanmuMsg(SimpleUserInfo userInfo, String text) {

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.user_nicename);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        entity.setLevel(userInfo.level);
        notifyMsg(entity);

        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(userInfo.avatar, userInfo.user_nicename, text);
        }
    }

    @Override
    public void onGroupDelete() {
        //useless
    }

    /**
     * 美颜fragment progress更新回调
     * @param progress seekbar进度数值
     * @param state 美颜/美白
     */
    @Override
    public void onProgressChanged(int progress, int state) {
        if (state == BeautyDialogFragment.STATE_BEAUTY) {
            mBeautyLevel = progress;
        } else if (state == BeautyDialogFragment.STATE_WHITE) {
            mWhiteningLevel = progress;
        }

        if (mTXLivePusher != null) {
            //当前美颜支持0-9，美白支持0-3
            if (!mTXLivePusher.setBeautyFilter(TCUtils.filtNumber(9, 100, mBeautyLevel), TCUtils.filtNumber(3, 100, mWhiteningLevel))) {
                Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != mAudioCtrl && mAudioCtrl.getVisibility() != View.GONE && ev.getRawY() < mAudioCtrl.getTop()) {
            mAudioCtrl.setVisibility(View.GONE);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//是否选择，没选择就不会继续
            if (requestCode == mAudioCtrl.REQUESTCODE) {
                if (data == null) {
                    Log.e(TAG, "null data");
                } else {
                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                    if (mAudioCtrl != null) {
                        mAudioCtrl.processActivityResult(uri);
                    } else {
                        Log.e(TAG, "NULL Pointer! Get Music Failed");
                    }
                }
            }
        }
    }
}
