package com.qiji.live.xiaozhibo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.UserLoginMgr;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class UserLoginActivity extends TCBaseActivity implements UIInterface, UserLoginMgr.UserLoginCallback, PlatformActionListener {


    @Bind(R.id.tv_user_login_register)
    TextView mTvRegister;

    //找回密码
    @Bind(R.id.tv_user_login_retrieve_pass)
    TextView mTvRetrievePass;

    @Bind(R.id.btn_user_login)
    Button mBtnLogin;

    //账号
    @Bind(R.id.et_login_phone)
    EditText mEtPhone;

    //密码
    @Bind(R.id.et_login_pass)
    EditText mEtPass;

    //QQ登录
    @Bind(R.id.iv_other_login_qq)
    ImageView mIvQQLogin;

    @Bind(R.id.iv_other_login_wechat)
    ImageView mIvWechatLogin;

    @Bind(R.id.tv_login_register)
    TextView mTvJumpRegister;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;



    private UserLoginMgr mUserLoginMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {
        mUserLoginMgr = UserLoginMgr.getInstance();
    }

    @Override
    public void initView() {
        mTCActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //注册
        mTvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegisterActivity.startRegisterActivity(UserLoginActivity.this);
            }
        });

        //找回密码
        mTvRetrievePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRetrievePasswordActivity.startRetrievePassActivity(UserLoginActivity.this);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNormalLogin(mEtPhone.getText().toString(),mEtPass.getText().toString());
            }
        });

        //微信登录
        mIvWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...");
                mUserLoginMgr.otherLogin(UserLoginActivity.this,Wechat.NAME,UserLoginActivity.this);
            }
        });
        //QQ登录
        mIvQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...");
                mUserLoginMgr.otherLogin(UserLoginActivity.this,QQ.NAME,UserLoginActivity.this);

            }
        });
        mTvJumpRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegisterActivity.startRegisterActivity(UserLoginActivity.this);
            }
        });

    }


    /**
     * 用户名密码登录
     * @param username 用户名
     * @param password 密码
     */
    public void attemptNormalLogin(String username, String password) {

        /*if (TCUtils.isPhoneNumValid(username)) {
            if (TCUtils.isPasswordValid(password)) {
                if(TCUtils.isNetworkAvailable(this)) {
                    //调用LoginHelper进行普通登陆
                    mUserLoginMgr.pwdLogin(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
                    showOnLoading(false);
                }
            } else {
                showPasswordError("密码过短");
            }
        } else {
            showLoginError("用户名不符合规范");
        }*/

        if(TCUtils.isNetworkAvailable(this)) {
            //调用LoginHelper进行普通登陆
            mUserLoginMgr.pwdLogin(username, password);
        } else {
            Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
            showOnLoading(false);
        }
    }

    public void showPasswordError(String errorString) {
        mEtPass.setError(errorString);
        showOnLoading(false);
    }

    public void showLoginError(String errorString) {
        mEtPhone.setError(errorString);
        showOnLoading(false);
    }

    /**
     * trigger loading模式
     * @param active
     */
    public void showOnLoading(boolean active) {
        if (active) {
            mEtPass.setEnabled(false);
            mEtPhone.setEnabled(false);
            mBtnLogin.setClickable(false);
        } else {
            mEtPass.setEnabled(true);
            mEtPhone.setEnabled(true);
            mBtnLogin.setClickable(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserLoginMgr.setUserLoginCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserLoginMgr.removeTCLoginCallback();
    }

    @Override
    public void onSuccess() {
        hideWaitDialog();
        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
        jumpToHomeActivity();

    }

    @Override
    public void onFailure(int code, String msg) {
        hideWaitDialog();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void jumpToHomeActivity () {
        Intent intent = new Intent(this, TCMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        hideWaitDialog();
        PlatformDb p = platform.getDb();
        mUserLoginMgr.otherLoginRequestService(p.getUserId(),QQ.NAME.toLowerCase(),p.getUserName(),p.getUserIcon());
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        showToast("授权失败" + throwable.getMessage());
        hideWaitDialog();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        hideWaitDialog();
    }
}
