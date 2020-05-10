package com.qiji.live.xiaozhibo.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.GridViewAdapter;
import com.qiji.live.xiaozhibo.adapter.ViewPageGridViewAdapter;
import com.qiji.live.xiaozhibo.bean.GiftJson;
import com.qiji.live.xiaozhibo.logic.TCPlayerMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.widget.ShapeButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @dw 直播间礼物列表
 */

public class GiftListDialogFragment extends DialogFragment {


    private RelativeLayout mSendGiftLian;

    private TextView mUserCoin;

    //赠送礼物按钮
    private ShapeButton mSendGiftBtn;

    private List<GiftJson> mGiftList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpGiftAdapter;

    //礼物view
    private ViewPager mVpGiftView;

    //当前选中的礼物
    private GiftJson mSelectedGiftItem;

    private LinearLayout mLlLua;

    private int mShowGiftSendOutTime = 5;

    protected List<GridView> mGiftViews = new ArrayList<>();

    private Handler mHandler = new Handler();


    private OnSendGiftInterface mOnSendGiftInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),R.style.dialog_gift);
        dialog.setContentView(R.layout.view_show_viewpager);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        initView(dialog);
        initData();

        return dialog;
    }

    private void initData() {
        //获取礼物列表
        TCPlayerMgr.getInstance().getGiftList(new TCPlayerMgr.OnGetGiftListListener() {
            @Override
            public void onGetGiftList(List<GiftJson> list) {
                mGiftList = list;
                fillGift();
            }
        });
    }


    public void setOnSendGiftInterface(OnSendGiftInterface onSendGiftInterface) {
        mOnSendGiftInterface = onSendGiftInterface;
    }

    /**
     * @param isOutTime 是否连送超时(如果是连送礼物的情况下)
     * @dw 赠送礼物, 请求服务端获取数据扣费
     */
    private void doSendGift(final String isOutTime) {
        if (mSelectedGiftItem != null) {

            //changeSendGiftBtnStatue(true);

            if(mOnSendGiftInterface != null){
                mOnSendGiftInterface.onSendGift(mSelectedGiftItem);
            }

        }
    }

    /**
     * @param statue 开启or关闭
     * @dw 赠送礼物按钮状态修改
     */
    private void changeSendGiftBtnStatue(boolean statue) {
        if (statue) {
            mSendGiftBtn.setFillColor(getResources().getColor(R.color.blue3));
            mSendGiftBtn.setEnabled(true);
        } else {
            mSendGiftBtn.setFillColor(getResources().getColor(R.color.light_gray));
            mSendGiftBtn.setEnabled(false);
        }
    }

    //连送按钮隐藏
    private void recoverySendGiftBtnLayout() {
        ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText("");
        mSendGiftLian.setVisibility(View.GONE);
        mSendGiftBtn.setVisibility(View.VISIBLE);
        mShowGiftSendOutTime = 5;
    }

    //展示礼物列表
    private void initView(Dialog dialog) {

        mUserCoin = (TextView) dialog.findViewById(R.id.tv_show_select_user_coin);
        mUserCoin.setText(UserInfoMgr.getInstance().getCoin());
        //点击底部跳转充值页面
        dialog.findViewById(R.id.rl_show_gift_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mLlLua = (LinearLayout) dialog.findViewById(R.id.ll_show_view_pager_lua);
        mVpGiftView = (ViewPager) dialog.findViewById(R.id.vp_gift_page);

        mVpGiftView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mLlLua.getChildCount() > 0){
                    for(int i = 0; i < mLlLua.getChildCount(); i++){
                        if(position == i){
                            ((ImageView)mLlLua.getChildAt(i)).setImageResource(R.drawable.ic_g_v1);
                        }else{
                            ((ImageView)mLlLua.getChildAt(i)).setImageResource(R.drawable.ic_g_v2);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSendGiftLian = (RelativeLayout) dialog.findViewById(R.id.iv_show_send_gift_lian);
        mSendGiftLian.bringToFront();
        mSendGiftLian.setOnClickListener(new View.OnClickListener() {//礼物连送
            @Override
            public void onClick(View v) {
                doSendGift("y");//礼物发送
                mShowGiftSendOutTime = 5;
                ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
            }
        });
        mSendGiftBtn = (ShapeButton) dialog.findViewById(R.id.btn_show_send_gift);
        mSendGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendGift(v);
            }
        });
        if (mSelectedGiftItem != null) {
            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.blue2));
        }

    }
    //赠送礼物单项被选中
    private void giftItemClick(AdapterView<?> parent, View view, int position) {
        if (parent.getItemAtPosition(position) != mSelectedGiftItem) {
            recoverySendGiftBtnLayout();
            mSelectedGiftItem = (GiftJson) parent.getItemAtPosition(position);
            //点击其他礼物
            changeSendGiftBtnStatue(true);
            for (int i = 0; i < mGiftViews.size(); i++) {
                for (int j = 0; j < mGiftViews.get(i).getChildCount(); j++) {
                    if (((GiftJson) mGiftViews.get(i).getItemAtPosition(j)).getType() == 1) {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
                    } else {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
                    }

                }
            }
            view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift_chosen);

        } else {
            if (((GiftJson) parent.getItemAtPosition(position)).getType() == 1) {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
            } else {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
            }
            mSelectedGiftItem = null;
            changeSendGiftBtnStatue(false);

        }
    }
    /**
     * @param v 发送按钮
     * @dw 点击赠送礼物按钮
     */
    private void onClickSendGift(View v) {//赠送礼物


        if ((mSelectedGiftItem != null) && (mSelectedGiftItem.getType() == 1)) {//连送礼物
            //v.setVisibility(View.GONE);
            if (mHandler == null) return;
            mHandler.postDelayed(new Runnable() {//开启连送定时器
                @Override
                public void run() {
                    if (mShowGiftSendOutTime == 1) {
                        recoverySendGiftBtnLayout();
                        mHandler.removeCallbacks(this);
                        return;
                    }
                    mHandler.postDelayed(this, 1000);
                    mShowGiftSendOutTime --;
                    ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));

                }
            }, 1000);
            doSendGift("y");//礼物发送
        } else {
            doSendGift("n");//礼物发送
        }
    }

    //礼物列表填充
    private void fillGift() {
        if (null == mVpGiftAdapter && isAdded()) {

            //礼物item填充
            List<View> mGiftViewList = new ArrayList<>();

            int index = 0;

            int giftsPageSize;

            if(mGiftList.size() % 8 == 0)
            {
                giftsPageSize = mGiftList.size()/8;
            }else{
                giftsPageSize = (int)(mGiftList.size()/8)+1;
            }

            for (int i = 0; i < giftsPageSize; i++) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_show_gifts_gv, null);
                mGiftViewList.add(v);
                List<GiftJson> l = new ArrayList<>();
                for (int j = 0; j < 8; j++) {
                    if (index >= mGiftList.size()) {
                        break;
                    }
                    l.add(mGiftList.get(index));
                    index++;
                }
                mGiftViews.add((GridView) v.findViewById(R.id.gv_gift_list));
                mGiftViews.get(i).setAdapter(new GridViewAdapter(l));
                mGiftViews.get(i).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        giftItemClick(parent, view, position);
                    }
                });

                ImageView lua = new ImageView(getActivity());
                lua.setImageResource(R.drawable.ic_g_v2);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4,0,4,0);
                lua.setLayoutParams(params);
                mLlLua.addView(lua);
            }
            mVpGiftAdapter = new ViewPageGridViewAdapter(mGiftViewList);
        }
        mVpGiftView.setAdapter(mVpGiftAdapter);
    }

    public interface OnSendGiftInterface{

        void onSendGift(GiftJson giftJson);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.SendGift event) {
        if(event.result == 1){

            if (mSelectedGiftItem.getType() == 1 && mSendGiftLian.getVisibility() != View.VISIBLE) {
                mSendGiftLian.setVisibility(View.VISIBLE);
                mSendGiftBtn.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CoinChange event) {

        mUserCoin.setText(event.coin);

    }
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

}
