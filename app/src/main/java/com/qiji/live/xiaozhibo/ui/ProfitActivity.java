package com.qiji.live.xiaozhibo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.ProfitMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.widget.ShapeButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfitActivity extends TCBaseActivity implements UIInterface, ProfitMgr.ProfitCallback {


    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;

    private ProfitMgr mProfitMgr;

    @Bind(R.id.tv_profit_total)
    TextView mTvToTal;

    @Bind(R.id.tv_profit_todaycash)
    TextView mTvToDayCash;

    @Bind(R.id.btn_profit)
    ShapeButton mBtnProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {

        mProfitMgr = ProfitMgr.getInstance();

        mProfitMgr.setProfitCallback(this);

        mProfitMgr.requestGetProfitData(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken());
    }

    @Override
    public void initView() {
        mTCActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestProfit();
            }
        });
    }

    private void requestProfit() {
        mProfitMgr.requestSubmitProfit(UserInfoMgr.getInstance().getUid(), UserInfoMgr.getInstance().getToken(), new ProfitMgr.ProfitSubmitCallback() {
            @Override
            public void success() {
                //重新刷新数据
                mProfitMgr.requestGetProfitData(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken());
                showToast("提现成功");
            }

            @Override
            public void fail() {
                showToast("提现失败");
            }
        });
    }

    @Override
    public void success(LinkedTreeMap<String, String> linkedTreeMap) {
        mTvToDayCash.setText(linkedTreeMap.get("todaycash"));
        mTvToTal.setText(linkedTreeMap.get("total"));
    }

    @Override
    public void fail() {

    }
}
