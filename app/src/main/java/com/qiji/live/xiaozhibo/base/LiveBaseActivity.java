package com.qiji.live.xiaozhibo.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.SendGiftJson;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.qiji.live.xiaozhibo.logic.TCChatEntity;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TDevice;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;
import com.qiji.live.xiaozhibo.widget.StrokeTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * 直播间基类
 * 主要封装常用方法，动画礼物等
 *
 */

public class LiveBaseActivity extends TCBaseActivity {

    //收入
    protected TextView mTvIncomeNum;

    //礼物消息队列
    protected Map<Integer,SendGiftJson> mGiftShowQueue = new HashMap();

    protected Handler mHandler = new Handler();

    //连送礼物动画显示区

    protected LinearLayout mShowGiftAnimator;

    protected int mShowGiftFirstUid = 0;

    protected int mShowGiftSecondUid = 0;

    //当前正在显示的两个动画
    protected Map<Integer,View> mGiftShowNow = new HashMap<>();

    protected Gson mGson = new Gson();

    //动画是否播放完毕
    protected boolean giftAnimationPlayEnd = true;

    //礼物队列
    protected List<SendGiftJson> mLuxuryGiftShowQueue = new ArrayList<>();

    //礼物
    protected View mGiftView;

    protected RelativeLayout mRlRootView;

    protected RelativeLayout mControllLayer;

    //是否在直播间
    public static boolean IS_ON_LIVE = false;

    /*
    * 更新收入
    *
    * */
    protected void updateIncome(String coin){
        //更新收入
        if(mTvIncomeNum != null)
            mTvIncomeNum.setText(String.valueOf(StringUtils.toInt(mTvIncomeNum.getText().toString()) + StringUtils.toInt(coin)));

    }


    /**
     * @dw 赠送礼物进行初始化操作
     * 判断当前礼物是属于豪华礼物还是连送礼物,并且对魅力值进行累加
     * @param mSendGiftInfo 赠送礼物信息
     * */
    protected void showGiftInit(SendGiftJson mSendGiftInfo){

        //更新收入
        updateIncome(String.valueOf(mSendGiftInfo.getTotalcoin()));

        //判断是要播放哪个豪华礼物
        int gId = mSendGiftInfo.getGiftid();

        if(gId == 19 || gId == 21 || gId == 22 || gId == 9 || gId == 19){
            mLuxuryGiftShowQueue.add(mSendGiftInfo);
        }

        switchPlayAnimation(mSendGiftInfo);
    }


    protected void switchPlayAnimation(SendGiftJson mSendGiftBean){
        switch (mSendGiftBean.getGiftid()){
            case 22: //烟花礼物
                //showFireworksAnimation(mSendGiftBean);
                break;
            case 21: //游轮礼物
                showCruisesAnimation(mSendGiftBean);
                break;
            case 9: //红色小轿车
                showRedCarAnimation(mSendGiftBean);
                break;
            case 19: //飞机礼物
                showPlainAnimation(mSendGiftBean);
                break;
            default:
                //普通连送礼物
                showOrdinaryGiftInit(mSendGiftBean);
                break;
        }
    }



    /**
     * @dw 连送
     * @author 魏鹏
     * @param mShowGiftLayout 礼物显示View
     * @param gitInfo 赠送的礼物信息
     * @param num 赠送礼物的数量(无用)
     * */
    protected void showEvenSentGiftAnimation(final View mShowGiftLayout, final SendGiftJson gitInfo, int num) {
        final AvatarImageView mGiftIcon = (AvatarImageView) mShowGiftLayout.findViewById(R.id.av_gift_icon);
        final TextView mGiftNum = (TextView) mShowGiftLayout.findViewById(R.id.tv_show_gift_num);
        ((AvatarImageView) mShowGiftLayout.findViewById(R.id.av_gift_uhead)).setLoadImageUrl(gitInfo.getAvatar());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_uname)).setText(gitInfo.getNicename());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(gitInfo.getGiftname());
        mGiftIcon.setLoadImageUrl(gitInfo.getGifticon());

        if(mShowGiftAnimator != null){
            mShowGiftAnimator.addView(mShowGiftLayout);//添加到动画区域显示效果
        }
        //动画开始平移
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mShowGiftLayout,"translationX",-340f,0f);
        oa1.setDuration(300);
        oa1.start();
        oa1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showGiftNumAnimation(mGiftNum, gitInfo.getUid());
                //礼物图片平移动画
                ObjectAnimator giftIconAnimator = ObjectAnimator.ofFloat(mGiftIcon, "translationX", -40f, (mShowGiftLayout.getRight() - mGiftIcon.getWidth()*2));
                giftIconAnimator.setDuration(500);
                giftIconAnimator.start();
                //获取当前礼物是正在显示的哪一个
                final int index = mShowGiftFirstUid == gitInfo.getUid() ? 1 : 2;
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (timingDelGiftAnimation(index)) {
                                if (mHandler != null) {
                                    mHandler.postDelayed(this, 1000);
                                }
                            } else {
                                mHandler.removeCallbacks(this);
                            }

                        }
                    }, 1000);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //定时检测当前显示礼物是否超时过期
    protected boolean timingDelGiftAnimation(int index){

        int id = index == 1 ? mShowGiftFirstUid:mShowGiftSecondUid;

        SendGiftJson mSendGiftInfo = mGiftShowQueue.get(id);

        if(mSendGiftInfo != null){

            long time = System.currentTimeMillis() - mSendGiftInfo.getSendTime();
            if((time > 4000) && (mShowGiftAnimator != null)){
                //超时 从礼物队列和显示队列中移除
                mShowGiftAnimator.removeView(mGiftShowNow.get(id));

                mGiftShowQueue.remove(id);

                mGiftShowNow.remove(id);
                if(index == 1){
                    mShowGiftFirstUid = 0;
                }else{
                    mShowGiftSecondUid = 0;
                }
                //从礼物队列中获取一条新的礼物信息进行显示
                if(mGiftShowQueue.size() != 0){

                    Iterator iterator = mGiftShowQueue.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry entry = (Map.Entry) iterator.next();
                        SendGiftJson sendGift = (SendGiftJson) entry.getValue();

                        if(mShowGiftFirstUid != sendGift.getUid() && mShowGiftSecondUid != sendGift.getUid()){//判断队列中的第一个礼物是否已经正在显示
                            showEvenSentGiftAnimation(initShowEvenSentSendGift(sendGift),sendGift,1);
                            break;
                        }
                    }
                }
                return false;
            }else{
                return true;
            }
        }
        return true;
    }

    //进入显示礼物队列信息初始化
    protected View initShowEvenSentSendGift(SendGiftJson mSendGiftInfo){
        View view = getLayoutInflater().inflate(R.layout.item_show_gift_animator, null);
        if(mShowGiftFirstUid == 0){
            mShowGiftFirstUid = mSendGiftInfo.getUid();
        }else{
            mShowGiftSecondUid = mSendGiftInfo.getUid();
        }
        mGiftShowNow.put(mSendGiftInfo.getUid(), view);
        return view;
    }


    /**
     * @dw 礼物数量增加动画
     * @param tv 显示数量的TextView
     * @param uid 送礼物用户id,需要根据id取出队列中的礼物信息进行赠送时间重置
     * */
    protected void showGiftNumAnimation(TextView tv,int uid){
        tv.setText("X" + mGiftShowQueue.get(uid).getGiftcount());
        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("scaleX",1f,0.2f,1f);
        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("scaleY",1f,0.2f,1f);
        ObjectAnimator.ofPropertyValuesHolder(tv,p1,p2).setDuration(200).start();
        mGiftShowQueue.get(uid).setSendTime(System.currentTimeMillis());//重置发送时间
    }
    /**
     * @dw 连送礼物初始化
     * @param mSendGiftInfo 赠送礼物信息
     * */
    protected void showOrdinaryGiftInit(final SendGiftJson mSendGiftInfo){
        //礼物动画View
        View mShowGiftLayout = mGiftShowNow.get(mSendGiftInfo.getUid());
        //设置当前礼物赠送时间
        mSendGiftInfo.setSendTime(System.currentTimeMillis());
        boolean isShow = false;//是否刚刚加入正在显示队列
        boolean isFirst = false;//是否是第一次赠送礼物
        //是否是第一次送礼物,为空表示礼物队列中没有查询到该用户的送礼纪录
        if(mGiftShowQueue.get(mSendGiftInfo.getUid()) == null){
            mGiftShowQueue.put(mSendGiftInfo.getUid(),mSendGiftInfo);//加入礼物消息队列
            //将是否第一次送礼设为true
            isFirst = true;
        }
        //是否是新的礼物类型,对比两次礼物的id是否一致
        boolean isNewGift = !(mSendGiftInfo.getGiftid() == mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftid());
        //当前的正在显示礼物队列不够两条(最多两条),并且当前送礼物用户不在list中
        if((mGiftShowNow.size() < 2) && (mShowGiftLayout == null)){
            //初始化显示礼物布局和信息
            mShowGiftLayout = initShowEvenSentSendGift(mSendGiftInfo);
            isShow = true;
        }
        /*
        * mShowGiftLayout不等于null表示在正在显示的礼物队列中查询到了该用户送礼物纪录
        * 将是否正在显示isShow设置为true
        * */
        if(mShowGiftLayout != null){
            isShow = true;
        }
        /*
        * 如果是新礼物(表示礼物队列中存在送礼物纪录)
        * 存在就将最新礼物的icon和数量重置,并且覆盖older信息
        * */
        if(isNewGift&&mShowGiftLayout != null){
            ((AvatarImageView)mShowGiftLayout.findViewById(R.id.av_gift_icon)).setLoadImageUrl(mSendGiftInfo.getGifticon());
            ((TextView)mShowGiftLayout.findViewById(R.id.tv_show_gift_num)).setText("X1");
            ((TextView)mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(mSendGiftInfo.getGiftname());
            //新礼物覆盖之前older礼物信息
            mGiftShowQueue.put(mSendGiftInfo.getUid(), mSendGiftInfo);
        }
        /*
        * 判断是否是连送礼物并且不是第一次赠送并且不是新礼物
        * 不是第一次赠送并且不是新礼物才需要添加数量(否则数量和礼物信息需要重置),
        * */
        if(mSendGiftInfo.getEvensend().equals("y")&&(!isFirst)&&(!isNewGift)){//判断当前礼物是否属于连送礼物
            //是连送礼物,将消息队列中的礼物赠送数量加1
            mGiftShowQueue.get(mSendGiftInfo.getUid()).setGiftcount(mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftcount() + 1);
        }
        //需要显示在屏幕上并且是第一次送礼物需要进行动画初始化
        if(isShow && isFirst){
            showEvenSentGiftAnimation(mShowGiftLayout,mSendGiftInfo,1);
        }else if(isShow && (!isNewGift)){//存在显示队列并且不是新礼物进行数量加一动画
            showGiftNumAnimation((TextView) mShowGiftLayout.findViewById(R.id.tv_show_gift_num), mSendGiftInfo.getUid());
        }
    }

    /**
     * @dw 豪华礼物飞机动画
     * @param mSendGiftBean 礼物信息
     * */
    protected void showPlainAnimation(final SendGiftJson mSendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }
        final DisplayMetrics mDisplayMetrics =  TDevice.getDisplayMetrics();
        //飞机动画初始化
        giftAnimationPlayEnd = false;
        //撒花的颜色
        final int[] colorArr = new int[]{R.color.red,R.color.yellow,R.color.blue2,R.color.green,R.color.orange,R.color.pink};

        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_plain,null);
        //用户头像
        AvatarImageView uHead = (AvatarImageView) mGiftView.findViewById(R.id.iv_animation_head);
        //用户昵称
        TextView uName = (TextView) mGiftView.findViewById(R.id.tv_gift_gname);

        uName.setText(mSendGiftBean.getNicename());

        uHead.setLoadImageUrl(mSendGiftBean.getAvatar());
        mControllLayer.addView(mGiftView);
        final RelativeLayout mRootAnimation = (RelativeLayout) mGiftView.findViewById(R.id.rl_animation_flower);

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {

                ObjectAnimator plainAnimator = ObjectAnimator.ofFloat(mGiftView,"translationX",mDisplayMetrics.widthPixels,0);
                plainAnimator.setDuration(3000);
                plainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        subscriber.onNext("");
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                plainAnimator.start();
            }
        });

        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Random random = new Random();
                int num = random.nextInt(50) + 10;
                int width = mRootAnimation.getWidth();
                int height = mRootAnimation.getHeight();
                //获取花朵

                for(int i  = 0; i<num; i ++){
                    int color = random.nextInt(5);
                    int x  = random.nextInt(50);
                    final int tx = width==0?0:random.nextInt(width);
                    final int ty = height==0?0:random.nextInt(height);
                    TextView flower = new TextView(LiveBaseActivity.this);
                    flower.setX(x);
                    flower.setText("❀");
                    flower.setTextColor(getResources().getColor(colorArr[color]));
                    flower.setTextSize(50);
                    //每个花朵的动画
                    mRootAnimation.addView(flower);
                    flower.animate().alpha(0f).translationX(tx).translationY(ty).setDuration(5000).start();

                }
                if(mHandler == null) return;
                //飞机移动到中间后延迟一秒钟,开始继续前行-x
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ObjectAnimator plainAnimator = ObjectAnimator.ofFloat(mGiftView,"translationX",-mGiftView.getWidth());
                        plainAnimator.setDuration(2000);
                        plainAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(null != mGiftView){
                                    if(null != mControllLayer){
                                        mControllLayer.removeView(mGiftView);
                                    }
                                    if(mLuxuryGiftShowQueue.size() > 0){
                                        mLuxuryGiftShowQueue.remove(0);
                                    }
                                    giftAnimationPlayEnd = true;
                                    if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                        switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                    }
                                }

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        plainAnimator.start();
                    }
                },4000);
            }
        });

    }
    /**
     * @dw 红色小轿车动画
     * @author 魏鹏
     * @param sendGiftBean 赠送的礼物信息
     * */
    protected void showRedCarAnimation(SendGiftJson sendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }
        final DisplayMetrics mDisplayMetrics =  TDevice.getDisplayMetrics();
        giftAnimationPlayEnd = false;
        //获取汽车动画布局
        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_car_general,null);
        AvatarImageView uHead = (AvatarImageView) mGiftView.findViewById(R.id.iv_animation_red_head);

        //用户昵称
        TextView uName = (TextView) mGiftView.findViewById(R.id.tv_gift_gname);

        uName.setText(sendGiftBean.getNicename());

        uHead.setLoadImageUrl(sendGiftBean.getAvatar());
        //获取到汽车image控件
        final ImageView redCar = (ImageView) mGiftView.findViewById(R.id.iv_animation_red_car);
        //添加到当前页面
        mControllLayer.addView(mGiftView);

        final int vw = redCar.getLayoutParams().width;
        //动画第二次
        final Runnable carRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                //小汽车切换帧动画开始继续移动向-x
                redCar.setImageResource(R.drawable.car_red1);
                mGiftView.animate().translationX(~vw)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //小汽车从底部重新回来切换帧动画
                                redCar.setImageResource(R.drawable.car_10001);
                                ObjectAnimator oX = ObjectAnimator.ofFloat(mGiftView,"translationX",mDisplayMetrics.widthPixels,mDisplayMetrics.widthPixels/2-vw/2);
                                ObjectAnimator oY = ObjectAnimator.ofFloat(mGiftView,"translationY",mDisplayMetrics.heightPixels/2,mDisplayMetrics.heightPixels >> 2);

                                //重新初始化帧动画参数
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(oX,oY);
                                animatorSet.setDuration(2000);
                                animatorSet.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        //小汽车加速帧动画切换
                                        redCar.setImageResource(R.drawable.backcar1);
                                        mGiftView.animate().translationX(-vw).translationY(0)
                                                .setDuration(1000)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //从汽车队列中移除,开始下一个汽车动画
                                                        if(mGiftView == null || mControllLayer == null)return;
                                                        mControllLayer.removeView(mGiftView);
                                                        if(mLuxuryGiftShowQueue.size() > 0){
                                                            mLuxuryGiftShowQueue.remove(0);
                                                        }

                                                        giftAnimationPlayEnd = true;
                                                        if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                                            switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                                        }
                                                    }
                                                })
                                                .start();

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                animatorSet.start();

                            }
                        })
                        .setDuration(1000).start();
            }
        };

        //汽车动画init
        ObjectAnimator ox = ObjectAnimator.ofFloat(mGiftView,"translationX",mDisplayMetrics.widthPixels + vw,mDisplayMetrics.widthPixels/2-vw/2);
        ox.setDuration(1500);
        ObjectAnimator oy = ObjectAnimator.ofFloat(mGiftView,"translationY",mDisplayMetrics.heightPixels >> 2);
        //设置背景帧动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ox,oy);
        animatorSet.setDuration(1500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                //小汽车停在中间
                redCar.setImageResource(R.drawable.car_red6);
                if(mHandler == null) return;
                mHandler.postDelayed(carRunnable,500);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }
    /**
     * @dw 邮轮
     * @author 魏鹏
     * @param mSendGiftBean 赠送的礼物信息
     * */
    protected void showCruisesAnimation(SendGiftJson mSendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }
        final DisplayMetrics mDisplayMetrics =  TDevice.getDisplayMetrics();

        giftAnimationPlayEnd = false;
        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_cruises,null);

        //游轮上的用户头像
        AvatarImageView mUhead = (AvatarImageView) mGiftView.findViewById(R.id.live_cruises_uhead);

        ((TextView)mGiftView.findViewById(R.id.tv_live_cruises_uname)).setText(mSendGiftBean.getNicename());

        /*//用户昵称
        TextView uName = (TextView) mGiftView.findViewById(R.id.tv_gift_gname);

        uName.setText(mSendGiftBean.getNicename());*/

        mUhead.setLoadImageUrl(mSendGiftBean.getAvatar());

        mControllLayer.addView(mGiftView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGiftView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGiftView.setLayoutParams(params);
        final RelativeLayout cruises = (RelativeLayout) mGiftView.findViewById(R.id.rl_live_cruises);

        //游轮开始平移动画
        cruises.animate().translationX(mDisplayMetrics.widthPixels/2 + mDisplayMetrics.widthPixels/3).translationY(120f).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mHandler == null) return;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //游轮移动到中间后重新开始移动
                        cruises.animate().translationX(mDisplayMetrics.widthPixels*2).translationY(200f).setDuration(3000)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        //结束后从队列移除开始下一个邮轮动画
                                        if(mControllLayer == null)return;
                                        mControllLayer.removeView(mGiftView);
                                        if(mLuxuryGiftShowQueue.size() > 0){
                                            mLuxuryGiftShowQueue.remove(0);
                                        }
                                        giftAnimationPlayEnd = true;
                                        if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                            switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                        }
                                    }
                                }).start();
                    }
                },2000);

            }
        }).setDuration(3000).start();

        //邮轮海水动画
        ImageView seaOne = (ImageView) mGiftView.findViewById(R.id.iv_live_water_one);
        ImageView seaTwo = (ImageView) mGiftView.findViewById(R.id.iv_live_water_one2);
        ObjectAnimator obj = ObjectAnimator.ofFloat(seaOne,"translationX",-50,50);
        obj.setDuration(1000);
        obj.setRepeatCount(-1);
        obj.setRepeatMode(ObjectAnimator.REVERSE);
        obj.start();
        ObjectAnimator obj2 = ObjectAnimator.ofFloat(seaTwo,"translationX",50,-50);
        obj2.setDuration(1000);
        obj2.setRepeatCount(-1);
        obj2.setRepeatMode(ObjectAnimator.REVERSE);
        obj2.start();
    }
}
