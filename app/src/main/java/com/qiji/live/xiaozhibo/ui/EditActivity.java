package com.qiji.live.xiaozhibo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.EditMgr;
import com.qiji.live.xiaozhibo.inter.ITCUserInfoMgrListener;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
*
* 编辑信息页面
*
* */
public class EditActivity extends TCBaseActivity implements UIInterface {


    @Bind(R.id.at_eui_edit)
    TCActivityTitle mTCActivityTitle;

    @Bind(R.id.et_edit)
    EditText mEtInputBody;

    @Bind(R.id.tv_max_length)
    TextView mTvMaxLenght;

    private String fieldName;
    private int maxLeng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.bind(this);

        initView();
        initData();

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        fieldName = intent.getStringExtra(EditMgr.FIELD_KEY);
        mEtInputBody.setHint(intent.getStringExtra(EditMgr.HIDE_TEXT));
        mTvMaxLenght.setText(intent.getStringExtra(EditMgr.SIZE_TEXT));

        mTCActivityTitle.setTitle(intent.getStringExtra(EditMgr.TITLE_TEXT));

        maxLeng = StringUtils.toInt(intent.getStringExtra(EditMgr.SIZE_TEXT));

        //设置最大字数
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLeng)};
        mEtInputBody.setFilters(filters);

        mEtInputBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(maxLeng > 0){

                    mTvMaxLenght.setText(String.valueOf(StringUtils.toInt(maxLeng) - s.length()));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void initView() {

        //点击完成
        mTCActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUserInfo(mEtInputBody.getText().toString());
            }
        });

        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUserInfo(String value) {

        if(TextUtils.isEmpty(value)){
            showToast("不能输入空内容");
            return;
        }

        if(value.length() > maxLeng){
            showToast("输入长度超过限制");
            return;
        }
        if(fieldName.equals("signature")){
            UserInfoMgr.getInstance().setUserSign(value, new ITCUserInfoMgrListener() {
                @Override
                public void OnQueryUserInfo(int error, String errorMsg) {

                }

                @Override
                public void OnSetUserInfo(int error, String errorMsg) {
                    if(error != 1){
                        showToast("修改成功");

                        finish();
                        setResult(0x200);
                    }else{
                        showToast("修改失败,请重试");
                    }
                }
            });
        }
    }

}
