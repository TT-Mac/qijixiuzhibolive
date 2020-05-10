package com.qiji.live.xiaozhibo.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.utils.TCUtils;


/**
 * 设置等页面条状控制或显示信息的控件
 */
public class TCLineControllerView extends LinearLayout {

    private String name;
    private boolean isBottom;
    private String content;
    private boolean canNav;
    private boolean isSwitch;
    private int maxLength;

    public TCLineControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_line_controller, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TCLineView, 0, 0);
        try {
            name = ta.getString(R.styleable.TCLineView_name);
            content = ta.getString(R.styleable.TCLineView_content);
            isBottom = ta.getBoolean(R.styleable.TCLineView_isBottom, false);
            canNav = ta.getBoolean(R.styleable.TCLineView_canNav,false);
            isSwitch = ta.getBoolean(R.styleable.TCLineView_isSwitch,false);
            maxLength = ta.getInteger(R.styleable.TCLineView_maxLength,-1);
            setUpView(context);
        } finally {
            ta.recycle();
        }
    }


    private void setUpView(Context context){
        TextView tvName = (TextView) findViewById(R.id.ctl_name);

        tvName.setText(name);

        TextView tvContent = (TextView) findViewById(R.id.ctl_content);
        tvContent.setText(TCUtils.getLimitString(content, TCConstants.USER_INFO_MAXLEN));
        View bottomLine = findViewById(R.id.ctl_bottomLine);
        bottomLine.setVisibility(isBottom ? VISIBLE : GONE);
        ImageView navArrow = (ImageView) findViewById(R.id.ctl_rightArrow);
        navArrow.setVisibility(canNav ? VISIBLE : GONE);
        LinearLayout contentPanel = (LinearLayout) findViewById(R.id.ctl_contentText);
        contentPanel.setVisibility(isSwitch ? GONE : VISIBLE);
        Switch switchPanel = (Switch) findViewById(R.id.ctl_btnSwitch);
        switchPanel.setVisibility(isSwitch?VISIBLE:GONE);

        if(maxLength != -1){
            tvContent.setEllipsize(TextUtils.TruncateAt.END);
            tvContent.setMaxEms(maxLength);
            tvContent.setSingleLine(true);
        }

    }


    /**
     * 设置文字内容
     *
     * @param content 内容
     */
    public void setContent(String content){
        this.content = content;
        TextView tvContent = (TextView) findViewById(R.id.ctl_content);
        tvContent.setText(TCUtils.getLimitString(content, TCConstants.USER_INFO_MAXLEN));
    }

    /**
     * 获取内容
     *
     */
    public String getContent() {
        TextView tvContent = (TextView) findViewById(R.id.ctl_content);
        return tvContent.getText().toString();
    }
}
