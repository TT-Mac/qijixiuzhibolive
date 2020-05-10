package com.qiji.live.xiaozhibo.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.alipay.AliPay;
import com.qiji.live.xiaozhibo.alipay.Keys;
import com.qiji.live.xiaozhibo.alipay.PayResult;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.event.Event;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.UserDiamondsMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.widget.ShapeButton;
import com.qiji.live.xiaozhibo.wxpay.WChatPay;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserDiamondsActivity extends TCBaseActivity implements UIInterface, UserDiamondsMgr.UserDiamondsCallback {

    private static final int SDK_PAY_FLAG = 1;
    /*
    * 充值列表
    * */
    @Bind(R.id.lv_recharge_prices)
    ListView mLvRechargePrices;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;

    /*
    * 余额
    * */
    @Bind(R.id.tv_diamonds_coin)
    TextView mTvCoin;

    private UserDiamondsMgr mUserDiamondsMgr;
    private String[] mDiamonds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_diamonds);

        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    public void initData() {
        mUserDiamondsMgr = UserDiamondsMgr.getInstance();
        mUserDiamondsMgr.setUserDiamondsCallback(this);

        mDiamonds = new String[]{"60","300","980","3880"};
        final String[] price = new String[]{"￥6","￥30","￥98","￥388"};

        mLvRechargePrices.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mDiamonds.length;
            }

            @Override
            public Object getItem(int position) {
               return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(UserDiamondsActivity.this,R.layout.item_user_diamonds,null);

                TextView tvDiamonds = (TextView) convertView.findViewById(R.id.item_tv_number);
                ShapeButton tvPrice = (ShapeButton) convertView.findViewById(R.id.item_btn_price);
                tvDiamonds.setText(mDiamonds[position]);
                tvPrice.setText(price[position]);

                return convertView;
            }
        });


        //请求获取钻石数量
        mUserDiamondsMgr.getDiamondsCount(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken());
    }

    @Override
    public void initView() {
        mTCActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击充值
        mLvRechargePrices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                onDiamondsItemClick(position);
            }
        });
    }


    //点击充值选择支付方式
    private void onDiamondsItemClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择支付方式");
        String[] payName = new String[]{"支付宝","微信"};
        builder.setItems(payName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                if(which == 0){
                    //showToast("支付宝正在开发中...");
                    alipayGetOrderId(String.valueOf(StringUtils.toInt(mDiamonds[position])/10));
                }else{

                    //showToast("微信正在开发中...");
                    wechatPay(String.valueOf(StringUtils.toInt(mDiamonds[position])/10));
                }
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();


    }




    @Override
    public void onSuccess(LinkedTreeMap<String,String> coin) {
        mTvCoin.setText(coin.get("coin"));
    }

    @Override
    public void onFailure(int code, String msg) {

    }


    //微信支付回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.WXPayRep event) {

        if(event.code == 0){
            //请求获取钻石数量
            mUserDiamondsMgr.getDiamondsCount(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken());
            showToast("支付成功");

        }else if(event.code == -1){
            showToast("支付失败");
        }else{
            showToast("支付已取消");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    //微信支付
    private void wechatPay(final String money) {

        WChatPay wChatPay = new WChatPay(UserDiamondsActivity.this);
        wChatPay.initPay(UserInfoMgr.getInstance().getUid(),/*String.valueOf(StringUtils.toInt(mDiamonds[position]) / 10)*/"0.01",money,
                new SimpleActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        showToast(msg);
                    }
                });
    }

    /*
    * 支付宝下单
    * */
    private void alipayGetOrderId(final String money) {

        mUserDiamondsMgr.requestAliPayOrderId(UserInfoMgr.getInstance().getUid(), money, new UserDiamondsMgr.AliPayCallback() {
            @Override
            public void onSuccess(String orderId) {

                AliPay aliPay = new AliPay(UserDiamondsActivity.this);
                //aliPayClient(orderId,money,"充值钻石" + (StringUtils.toInt(money) * 10));

                aliPay.initPay(orderId,money, String.valueOf(StringUtils.toInt(money) * 10));
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });

    }


}
