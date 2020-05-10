package com.qiji.live.xiaozhibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.TCApplication;

/**
 * Created by weipeng on 16/11/11.
 */

public class LoadUrlImageView extends ImageView {

    private int mDefaultBg = R.drawable.bg;
    private Context mContext;

    public LoadUrlImageView(Context context) {
        super(context);
        mContext = context;
    }

    public LoadUrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LoadUrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }
    public void setDefaultBg(int df){
        this.mDefaultBg = df;
    }
    public void setLoadImageUrl(String url){
        Glide.with(mContext)
                .load(url)
                .placeholder(mDefaultBg)
                .into(this);
    }
}
