package com.qiji.live.xiaozhibo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.HomePageActivity;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;

/**
 * Created by weipeng on 16/11/19.
 */

public class GlobalUserItem extends RelativeLayout {

    private AvatarImageView avHead;
    private ImageView ivSex,ivLevel,ivAtt;
    private TextView tvName,tvSign;

    private GlobalUserBean userInfo;

    private Context mContext;

    public GlobalUserItem(Context context) {
        this(context,null);
    }

    public GlobalUserItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GlobalUserItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.item_global_user,this);

        avHead = (AvatarImageView) findViewById(R.id.item_iv_head);
        ivSex = (ImageView) findViewById(R.id.item_iv_sex);
        ivLevel = (ImageView) findViewById(R.id.item_iv_level);
        ivAtt = (ImageView) findViewById(R.id.item_iv_att);
        tvName = (TextView) findViewById(R.id.item_tv_u_name);
        tvSign = (TextView) findViewById(R.id.item_tv_sign);



    }

    public void setUserInfoAndFillUI(@NonNull GlobalUserBean u){
        this.userInfo = u;

        fillUI();
    }

    private void fillUI() {

        avHead.setLoadImageUrl(userInfo.getAvatar_thumb());
        tvName.setText(userInfo.getUser_nicename());
        tvSign.setText(userInfo.getSignature());
        ivSex.setImageResource(TCUtils.getSexRes(StringUtils.toInt(userInfo.getSex())));
        ivLevel.setImageResource(TCUtils.getLevelRes(StringUtils.toInt(userInfo.getLevel())));
        ivAtt.setImageResource(TCUtils.returnAttentionRes(StringUtils.toInt(userInfo.getIsattention())));
        //关注点击事件
        ivAtt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoMgr.getInstance().attentionOrCancelUser(new SimpleActionListener() {
                    @Override
                    public void onSuccess() {
                        //修改状态
                        if(StringUtils.toInt(userInfo.getIsattention()) == 1){
                            userInfo.setIsattention("0");
                            ivAtt.setImageResource(TCUtils.returnAttentionRes(0));
                        }else{
                            userInfo.setIsattention("1");
                            ivAtt.setImageResource(TCUtils.returnAttentionRes(1));
                        }

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                },userInfo.getId());
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageActivity.startHomePageActivity(mContext,userInfo.getId());
            }
        });
    }


}
