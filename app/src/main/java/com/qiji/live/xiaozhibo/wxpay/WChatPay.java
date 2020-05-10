package com.qiji.live.xiaozhibo.wxpay;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 微信支付
 */
public class WChatPay {
    IWXAPI msgApi;
    //appid
    final String appId = TCConstants.WECHAT_ID;

    private Activity context;

    public WChatPay(Activity context) {
        this.context = context;
        // 将该app注册到微信
        msgApi = WXAPIFactory.createWXAPI(context,null);
        msgApi.registerApp(appId);
    }

    /**
     * @dw 初始化微信支付
     * @param price 价格
     * @param num 数量
     * */
    public void initPay(String uid,String price, String num,final SimpleActionListener listener) {

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestGetWxParam(uid,price,num);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String, String>>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseJson<LinkedTreeMap<String, String>>> call, Response<ResponseJson<LinkedTreeMap<String, String>>> response) {

                if(AppClient.checkResult(response)){

                    if(listener != null){
                        listener.onSuccess();
                    }
                    callWxPay((LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0));
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseJson<LinkedTreeMap<String, String>>> call, Throwable t) {

                if(listener != null){
                    listener.onFail(0,"获取订单失败");
                }
            }
        });

    }

    private void callWxPay(LinkedTreeMap<String,String> param) {

        PayReq req = new PayReq();
        req.appId        = param.get("appid");
        req.partnerId    = param.get("partnerid");
        req.prepayId     = param.get("prepayid");//预支付会话ID
        req.packageValue = "Sign=WXPay";
        req.nonceStr     = param.get("noncestr");
        req.timeStamp    = param.get("timestamp");
        req.sign         = param.get("sign");
        if(msgApi.sendReq(req)){
            Toast.makeText(context,"微信支付",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"请查看您是否安装微信",Toast.LENGTH_SHORT).show();
        }

    }
}
