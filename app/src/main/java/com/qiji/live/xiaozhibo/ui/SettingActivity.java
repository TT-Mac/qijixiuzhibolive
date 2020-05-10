package com.qiji.live.xiaozhibo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.CommonMgr;
import com.qiji.live.xiaozhibo.logic.UserLoginMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.ui.customviews.TCLineControllerView;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends TCBaseActivity implements UIInterface {

    @Bind(R.id.btn_setting_login_out)
    Button mBtnLoginOut;

    @Bind(R.id.at_eui_edit)
    TCActivityTitle mTCActivityTitle;

    //修改密码
    @Bind(R.id.btn_setting_login_change_pass)
    TCLineControllerView mTCVChangePass;

    //检查版本
    @Bind(R.id.btn_setting_login_change_check_version)
    TCLineControllerView mTCVCheckVersion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
        initView();
        initData();

    }

    @Override
    public void initData() {


        //获取最新版本
        CommonMgr.getInstance().setConfigMgrCallback(new CommonMgr.ConfigMgrCallback() {
            @Override
            public void onSuccess(LinkedTreeMap<String, String> config) {
                String versionCode = TDevice.getVersionName();
                //判断版本号,是否是最新版本
                if(StringUtils.toInt(versionCode) != StringUtils.toInt(config.get("version_android"))){

                    //版本号
                    mTCVCheckVersion.setContent(versionCode + "(发现最新版本" + config.get("version_android") + ")");
                }else{

                    mTCVCheckVersion.setContent(String.valueOf(versionCode) + "(当前已经是最新版本)");
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
        CommonMgr.getInstance().requestGetConfig();

    }

    @Override
    public void initView() {

        //退出登录
        mBtnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLoginMgr.getInstance().logout();
                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //密码修改
        mTCVChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,ChangePassActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void startSettingActivity(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
    }
}
