package com.qiji.live.xiaozhibo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.logic.UserLoginMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.UserRegisterMgr;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class UserRegisterActivity extends TCBaseActivity implements UIInterface,UserRegisterMgr.UserRegisterCallback, UserLoginMgr.UserLoginCallback, PlatformActionListener {


    //账号
    @Bind(R.id.et_user_register_phone)
    EditText mEtPhone;

    //验证码
    @Bind(R.id.et_user_register_code)
    EditText mEtCode;

    //密码
    @Bind(R.id.et_user_register_pass)
    EditText mEtPass;

    //验证码
    @Bind(R.id.btn_user_register_get_code)
    TextView mTvGetCode;

    //提交注册信息
    @Bind(R.id.btn_user_register_submit)
    Button mBtnSubmit;

    //QQ登录
    @Bind(R.id.iv_other_login_qq)
    ImageView mIvQQLogin;

    @Bind(R.id.iv_other_login_wechat)
    ImageView mIvWechatLogin;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;

    private UserRegisterMgr mUserRegisterMgr;

    private String mPassword;

    private UserLoginMgr mUserLoginMgr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    public void initData() {
        mUserRegisterMgr = UserRegisterMgr.getInstance();
        mUserRegisterMgr.setUserRegisterCallback(this);

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


        mTvGetCode.setEnabled(false);
        mBtnSubmit.setEnabled(false);
        //检测是否输入手机号码
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEtPhone.getText().length()  == 11){
                    mTvGetCode.setBackgroundResource(R.color.turbo);
                }else{
                    if(!(mTvGetCode.getText().toString().indexOf("s") > 0)){
                        mTvGetCode.setEnabled(true);
                        mTvGetCode.setBackgroundResource(R.color.colorGray2);
                    }
                }

                checkIsChangeSubmitButton();
            }
        });

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIsChangeSubmitButton();
            }
        });

        //检测是否输入密码
        mEtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIsChangeSubmitButton();
            }
        });



        //点击注册
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegister(mEtPhone.getText().toString(),mEtPass.getText().toString(),mEtCode.getText().toString());
            }
        });

        //点击获取验证码
        mTvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerificationCode(mEtPhone.getText().toString());
            }
        });

        //微信登录
        mIvWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...");
                mUserLoginMgr.otherLogin(UserRegisterActivity.this,Wechat.NAME,UserRegisterActivity.this);
            }
        });
        //QQ登录
        mIvQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...");
                mUserLoginMgr.otherLogin(UserRegisterActivity.this,QQ.NAME,UserRegisterActivity.this);

            }
        });
    }

    //获取验证码
    private void getVerificationCode(String phoneNum) {

        if (TCUtils.isPhoneNumValid(phoneNum)) {
            if(TCUtils.isNetworkAvailable(this)) {
                mUserRegisterMgr.smsRegAskCode(phoneNum, new UserRegisterMgr.UserSmsRegCallback() {
                    @Override
                    public void onGetVerifyCode(int reaskDuration, int expireDuration) {

                        Toast.makeText(getApplicationContext(), "注册短信下发,验证码" + expireDuration / 60 + "分钟内有效", Toast.LENGTH_SHORT).show();
                        TCUtils.startTimer(new WeakReference<>(mTvGetCode), "验证码", reaskDuration, 1);

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
            }
        } else {
            showRegistError("手机格式错误");
        }
    }

    private void showOnLoading(boolean b) {
        if(b){
            showWaitDialog();
        }else{
            hideWaitDialog();
        }
    }

    private void showRegistError(String errorString) {
        mEtPhone.setError(errorString);
        showOnLoading(false);
    }
    private String getWellFormatMobile(String countryCode, String phoneNumber) {
        return countryCode + "-" + phoneNumber;
    }

    //提交注册信息
    private void submitRegister(String username, String password, String passwordVerify) {
        /*if (TCUtils.isPhoneNumValid(username)) {
            if (TCUtils.isPasswordValid(password)) {
                if (password.equals(password)) {
                    if(TCUtils.isNetworkAvailable(this)) {
                        mPassword = password;
                        showOnLoading(true);
                        mUserRegisterMgr.pwdRegist(username, password,passwordVerify);

                    } else {
                        Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showPasswordVerifyError("两次输入密码不一致");
                }
            } else {
                showPasswordVerifyError("密码长度应为6-32位");
            }
        } else {
            showRegistError("用户名不符合规范");
        }*/

        mPassword = password;
        showOnLoading(true);
        mUserRegisterMgr.pwdRegist(username, password,passwordVerify);
    }

    private void checkIsChangeSubmitButton() {
        if(mEtPass.getText().length()  > 0 && mEtPhone.getText().length() == 11 && mEtCode.getText().length() > 0){
            mBtnSubmit.setBackgroundResource(R.color.deep_sky_blue);
        }else{
            mBtnSubmit.setEnabled(true);
            mBtnSubmit.setBackgroundResource(R.color.colorGray2);
        }
    }


    public static void startRegisterActivity(Context context){
        Intent intent = new Intent(context,UserRegisterActivity.class);
        context.startActivity(intent);
    }

    private void showPasswordVerifyError(String errorString) {
        mEtPass.setError(errorString);
        showOnLoading(false);
    }

    //跳转首页
    private void jumpToHomeActivity () {
        Intent intent = new Intent(this, TCMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserLoginMgr.setUserLoginCallback(new UserLoginMgr.UserLoginCallback() {
            @Override
            public void onSuccess() {
                hideWaitDialog();
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                jumpToHomeActivity();
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserLoginMgr.removeTCLoginCallback();
    }
    @Override
    public void onSuccess(String username) {
        hideWaitDialog();
        showToast("注册成功");
        mUserRegisterMgr.removeTCRegisterCallback();

        finish();

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int code, String msg) {
        hideWaitDialog();
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
