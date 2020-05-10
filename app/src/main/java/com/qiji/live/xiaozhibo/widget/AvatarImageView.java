package com.qiji.live.xiaozhibo.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.EditUseInfoActivity;
import com.qiji.live.xiaozhibo.utils.TCUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by weipeng on 16/11/11.
 */

public class AvatarImageView extends CircleImageView {

    private int mDefaultBg = R.drawable.bg;
    private Context mContext;

    public AvatarImageView(Context context) {
        super(context);
        mContext = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setDefaultBg(int df){
        this.mDefaultBg = df;
    }

    public void setLoadImageUrl(String url){

        TCUtils.showPicWithUrl(mContext, this, url,mDefaultBg);
    }
}
