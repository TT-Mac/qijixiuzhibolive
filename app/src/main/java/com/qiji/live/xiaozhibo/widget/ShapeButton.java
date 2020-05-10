package com.qiji.live.xiaozhibo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.qiji.live.xiaozhibo.R;

/**
 * Created by weipeng on 16/11/18.
 */

public class ShapeButton extends Button {

    private int radius;

    private int strokeWidth;

    private int strokeColor;

    private int fillColor;

    public ShapeButton(Context context) {
        this(context,null);
    }

    public ShapeButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShapeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.ShapeButton,0,0);

        try {
            radius = ta.getInteger(R.styleable.ShapeButton_radius,0);

            strokeWidth = ta.getInteger(R.styleable.ShapeButton_strokeWidth,0);

            strokeColor = ta.getColor(R.styleable.ShapeButton_strokeColor,context.getResources().getColor(R.color.light_gray));

            fillColor = ta.getColor(R.styleable.ShapeButton_fillColor,context.getResources().getColor(R.color.light_gray));

        }finally {
            ta.recycle();
        }

        init(context);
    }

    private void init(Context c) {


        GradientDrawable gradientDrawable = new GradientDrawable();

        if(radius != 0){
            gradientDrawable.setCornerRadius(radius);
            if(strokeWidth != 0){
                gradientDrawable.setStroke(strokeWidth,strokeColor);
            }
            gradientDrawable.setColor(fillColor);

            setBackgroundDrawable(gradientDrawable);
        }

    }

    public void setFillColor(int color){

        GradientDrawable gradientDrawable = new GradientDrawable();

        if(radius != 0){
            gradientDrawable.setCornerRadius(radius);
            if(strokeWidth != 0){
                gradientDrawable.setStroke(strokeWidth,strokeColor);
            }
            gradientDrawable.setColor(color);

            setBackgroundDrawable(gradientDrawable);
        }
    }
}
