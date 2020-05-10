package com.qiji.live.xiaozhibo.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.TCRegisterMgr;
import com.qiji.live.xiaozhibo.logic.UserRegisterMgr;
import com.qiji.live.xiaozhibo.logic.UserRetrievePasswordMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.TCUtils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserRetrievePasswordActivity extends TCBaseActivity implements UIInterface, UserRetrievePasswordMgr.UserRetrievePasswordCallback {

    @Bind(R.id.et_retrieve_pass_phone)
    EditText mEtPhone;

    @Bind(R.id.et_retrieve_pass_code)
    EditText mEtCode;

    //新密码
    @Bind(R.id.et_retrieve_pass_p1)
    EditText mEtP1;

    //确认密码
    @Bind(R.id.et_retrieve_pass_p2)
    EditText mEtP2;

    @Bind(R.id.tv_retrieve_pass_get_code)
    TextView mTvGetCode;

    //提交修改
    @Bind(R.id.btn_retrieve_pass_submit)
    Button mBtnSubmit;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;

    private UserRetrievePasswordMgr mUserRetrievePasswordMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        ButterKnife.bind(this);

        initView();
        initData();
    }


    @Override
    public void initData() {
        mUserRetrievePasswordMgr = UserRetrievePasswordMgr.getInstance();
        mUserRetrievePasswordMgr.setUserRetrievePasswordCallback(this);

    }

    @Override
    public void initView() {
        mTCActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        mEtP1.addTextChangedListener(new TextWatcher() {
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
        mEtP2.addTextChangedListener(new TextWatcher() {
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

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitChangePass(mEtPhone.getText().toString(),mEtCode.getText().toString(),mEtP1.getText().toString(),mEtP2.getText().toString());
            }
        });

        mTvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendSmsCode(mEtPhone.getText().toString());
            }
        });

    }

    private void sendSmsCode(String phone) {
        if (TCUtils.isPhoneNumValid(phone)) {
            if(TCUtils.isNetworkAvailable(this)) {
                mUserRetrievePasswordMgr.smsRegAskCode(phone, new UserRegisterMgr.UserSmsRegCallback() {
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
    private void showRegistError(String errorString) {
        mEtPhone.setError(errorString);
        showOnLoading(false);
    }
    //修改密码
    private void submitChangePass(String username,String code,String pwd1,String pwd2) {

        /*if (TCUtils.isPhoneNumValid(username)) {
            if (TCUtils.isPasswordValid(pwd1)) {
                if (pwd1.equals(pwd2)) {
                    if(TCUtils.isNetworkAvailable(this)) {
                        mUserRetrievePasswordMgr.pwdRetrieve(username, pwd1,code);
                        showOnLoading(true);
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

        if(TCUtils.isNetworkAvailable(this)) {
            mUserRetrievePasswordMgr.pwdRetrieve(username, pwd1,pwd2,code);
            showOnLoading(true);
        } else {
            Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPasswordVerifyError(String errorString) {
        mEtP2.setError(errorString);
        showOnLoading(false);
    }

    private void checkIsChangeSubmitButton() {
        if(mEtP1.getText().length()  > 0 && mEtP2.getText().length()  > 0 && mEtPhone.getText().length() == 11 && mEtCode.getText().length() > 0){
            mBtnSubmit.setBackgroundResource(R.color.deep_sky_blue);
        }else{
            mBtnSubmit.setEnabled(true);
            mBtnSubmit.setBackgroundResource(R.color.colorGray2);
        }
    }

    public static void startRetrievePassActivity(Context context){
        Intent intent = new Intent(context,UserRetrievePasswordActivity.class);
        context.startActivity(intent);
    }

    public void showOnLoading(boolean active) {
        if (active) {
            mEtP1.setEnabled(false);
            mEtP2.setEnabled(false);
            mEtCode.setEnabled(false);

            mBtnSubmit.setEnabled(false);
            //tvRegister.setTextColor(getResources().getColor(R.color.colorBlue));
        } else {
            mEtP1.setEnabled(true);
            mEtP2.setEnabled(true);
            mEtCode.setEnabled(true);
            mBtnSubmit.setEnabled(true);
            //tvRegister.setTextColor(getResources().getColor(R.color.colorAccent));
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        mUserRetrievePasswordMgr.removeTCRPCallback();
    }

    @Override
    public void onSuccess(String username) {

        showToast("密码修改成功");
        finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        if(code == 0){
            showOnLoading(false);
            return;
        }

        showToast("密码修改失败");
    }
}
