package com.qiji.live.xiaozhibo.logic;

import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.CarouselJson;
import com.qiji.live.xiaozhibo.bean.ResponseJson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/22.
 */

public class CarouseMgr {
    public static final String TAG = CarouseMgr.class.getSimpleName();

    public CarouseMgr() {
    }

    private static class CarouseMgrHolder{
        //private static CarouseMgr instance = new CarouseMgr();
    }

    public static CarouseMgr getInstance(){
        return new CarouseMgr();
    }

    private CarouseCallback mCarouseCallback;

    public CarouseCallback getCarouseCallback() {
        return mCarouseCallback;
    }

    public void setCarouseCallback(CarouseCallback carouseCallback) {
        mCarouseCallback = carouseCallback;
    }

    /*
    * 获取轮播图
    *
    * */
    public void getCarousePic(){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<CarouselJson>> call = apiStores.requestGetCarouse();
        call.enqueue(new Callback<ResponseJson<CarouselJson>>() {
            @Override
            public void onResponse(Call<ResponseJson<CarouselJson>> call, Response<ResponseJson<CarouselJson>> response) {
                if(mCarouseCallback != null)
                    if(AppClient.checkResult(response)){
                        mCarouseCallback.onSuccess(response.body().getData().getInfo());
                    }
            }

            @Override
            public void onFailure(Call<ResponseJson<CarouselJson>> call, Throwable t) {
                if(mCarouseCallback != null){
                    mCarouseCallback.onFailure(1,"获取轮播失败");
                }
            }
        });
    }

    /*
    * 获取轮播图回调
    *
    * */

    public interface CarouseCallback{

        /*
        * 获取成功
        *
        * */
        void onSuccess(List<CarouselJson> list);

        /*
        * 获取失败
        *
        * */
        void onFailure(int code, String msg);
    }

}
