package com.qiji.live.xiaozhibo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;

/**
 * Created by weipeng on 16/11/21.
 */

public class EmptyDataView extends RelativeLayout {
    private ImageView ivDefault;
    private TextView tvText;

    private int defaultImg = 0;
    private String text = "";

    public EmptyDataView(Context context) {
        this(context,null);
    }

    public EmptyDataView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EmptyDataView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.EmptyDataView,0,0);

        defaultImg = ta.getInteger(R.styleable.EmptyDataView_default_img,0);
        text = ta.getString(R.styleable.EmptyDataView_default_text);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_empty_data,this);



        ivDefault = (ImageView) findViewById(R.id.iv_empty);
        tvText = (TextView) findViewById(R.id.tv_empty);

        if(defaultImg != 0){
            ivDefault.setImageResource(defaultImg);
        }

        if(!TextUtils.isEmpty(text)){
            tvText.setText(text);
        }

    }
}
