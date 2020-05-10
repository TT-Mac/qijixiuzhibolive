package com.qiji.live.xiaozhibo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.LiveRecordAdapter;
import com.qiji.live.xiaozhibo.base.LiveBaseActivity;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.LiveRecordBean;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.LiveRecordListMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
*
* 直播记录
*
* */
public class LiveRecordListActivity extends TCBaseActivity implements UIInterface, LiveRecordListMgr.LiveRecordListCallback {
    public static final int START_LIVE_PLAY = 100;

    @Bind(R.id.lv_record_list)
    ListView mLvRecordList;

    @Bind(R.id.view_tac)
    TCActivityTitle mTCActivityTitle;

    private LiveRecordListMgr mLiveRecordListMgr;

    private LiveRecordAdapter mLiveRecordAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_record_list);

        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    public void initData() {
        mLiveRecordListMgr = LiveRecordListMgr.getInstance();

        mLiveRecordListMgr.setLiveRecordListCallback(this);

        mLiveRecordListMgr.getLiveRecordList(UserInfoMgr.getInstance().getUid());

    }

    @Override
    public void initView() {
        mLvRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startLivePlay(mLiveRecordAdapter.getItem(position));
            }
        });

        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSuccess(List<LiveRecordBean> list) {
        mLiveRecordAdapter = new LiveRecordAdapter(this, (ArrayList<LiveRecordBean>) list);
        mLvRecordList.setAdapter(mLiveRecordAdapter);
    }

    @Override
    public void onFailure(int code, String msg) {

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
        Intent intent = new Intent(LiveRecordListActivity.this, TCLivePlayerActivity.class);
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
