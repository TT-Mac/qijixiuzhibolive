package com.qiji.live.xiaozhibo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;

import butterknife.Bind;
import butterknife.ButterKnife;


/*
* 修改密码页面
* 生活不止眼前的苟且,还有诗和远方的田野...
* */
public class ChangePassActivity extends TCBaseActivity implements UIInterface {

    @Bind(R.id.et_change_pass1)
    EditText mEtPass1;

    @Bind(R.id.et_change_pass2)
    EditText mEtPass2;

    @Bind(R.id.et_change_pass3)
    EditText mEtPass3;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;


    //提交修改
    @Bind(R.id.btn_user_login)
    Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);


        ButterKnife.bind(this);
        initView();
        initData();


    }

    @Override
    public void initData() {


    }

    @Override
    public void initView() {
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChangePass(mEtPass1.getText().toString(),mEtPass2.getText().toString(),mEtPass3.getText().toString());
            }
        });

        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    //提交修改密码
    private void submitChangePass(String s, String s1, String s2) {

        if(TextUtils.isEmpty(s) || TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)){

            showToast("请检查是否填写完整");
            return;
        }

        showWaitDialog("正在修改...");
        UserInfoMgr.getInstance().requestChangePass(UserInfoMgr.getInstance().getUid(), UserInfoMgr.getInstance().getToken(),
                s, s1, s2, new SimpleActionListener() {
                    @Override
                    public void onSuccess() {
                        showToast("修改成功");
                        hideWaitDialog();
                        finish();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        hideWaitDialog();
                    }
                });
    }
}
