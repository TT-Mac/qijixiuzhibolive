package com.qiji.live.xiaozhibo.logic;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.api.AppClient;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.inter.ITCUserInfoMgrListener;
import com.qiji.live.xiaozhibo.inter.SimpleActionListener;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by weipeng on 16/11/18.
 * 回忆是抓不到的月光握紧就变黑暗
 */

public class UserInfoMgr {
    private String TAG = getClass().getName();

    private UserInfoBean mUserInfoBean;

    private UserInfoMgr() {
        updateUserInfo();
    }

    public UserInfoBean getUserInfoBean(){
        return mUserInfoBean;
    }

    public void saveUserInfo(){
        TCApplication.getInstance().saveUserInfo(mUserInfoBean);
    }

    public String getUid(){
        return mUserInfoBean.getId();
    }

    public String getToken(){
        return mUserInfoBean.getToken();
    }

    public String getLevel(){
        return mUserInfoBean.getLevel();
    }

    private static UserInfoMgr instance = new UserInfoMgr();

    public static UserInfoMgr getInstance() { return instance; }

    public void updateUserInfo(){
        mUserInfoBean = TCApplication.getApplication().getLoginUser();
    }

    /**
     * 查询用户资料
     *
     * @param listener  查询结果的回调
     */
    public void queryUserInfo(final ITCUserInfoMgrListener listener){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<UserInfoBean>> call = apiStores.requestBaseInfo(mUserInfoBean.getId(),mUserInfoBean.getToken());
        call.enqueue(new Callback<ResponseJson<UserInfoBean>>() {
            @Override
            public void onResponse(Call<ResponseJson<UserInfoBean>> call, Response<ResponseJson<UserInfoBean>> response) {
                if(AppClient.checkResult(response)){
                    mUserInfoBean = (UserInfoBean) response.body().getData().getInfo().get(0);
                    listener.OnSetUserInfo(1,"success");
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<UserInfoBean>> call, Throwable t) {
                listener.OnSetUserInfo(0,"获取用户信息失败");
            }
        });
    }

    /**
     * 设置昵称
     *
     * @param nickName 昵称
     * @param listener 设置结果回调
     */
    public void setUserNickName(final String nickName, final ITCUserInfoMgrListener listener){
        if (mUserInfoBean.getUser_nicename().equals(nickName)) {
            if (null != listener) listener.OnSetUserInfo(0, null);
            return;
        }

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        String info = "{\"user_nicename\":\"" + nickName + "\"}";
        Call<ResponseJson<Object>> call = apiStores.requestChangeInfo(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken(),info);

        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                mUserInfoBean.setUser_nicename(nickName);
                if (null != listener) listener.OnSetUserInfo(0,null);
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if (null != listener) listener.OnSetUserInfo(1,t.getMessage());
            }
        });
    }

    /**
     * 设置签名
     *
     * @param sign  签名
     * @param listener 设置结果回调
     */
    public void setUserSign(final String sign, final ITCUserInfoMgrListener listener){
        if (mUserInfoBean.getSignature() == sign) {
            if (null != listener) listener.OnSetUserInfo(0,null);
            return;
        }

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        String info = "{\"signature\":\"" + sign + "\"}";
        Call<ResponseJson<Object>> call = apiStores.requestChangeInfo(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken(),info);

        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                mUserInfoBean.setSignature(sign);
                if (null != listener) listener.OnSetUserInfo(0,null);
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if (null != listener) listener.OnSetUserInfo(1,t.getMessage());
            }
        });
    }

    /**
     * 设置性别
     *
     * @param sex 性别
     * @param listener 设置结果回调
     */
    public void setUserSex(final String sex, final ITCUserInfoMgrListener listener){
        if (mUserInfoBean.getSex() == sex) {
            if (null != listener) listener.OnSetUserInfo(0,null);
            return;
        }

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        String info = "{\"sex\":\"" + sex + "\"}";
        Call<ResponseJson<Object>> call = apiStores.requestChangeInfo(UserInfoMgr.getInstance().getUid(),UserInfoMgr.getInstance().getToken(),info);

        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                mUserInfoBean.setSex(sex);
                if (null != listener) listener.OnSetUserInfo(0,null);
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if (null != listener) listener.OnSetUserInfo(1,t.getMessage());
            }
        });

    }

    /**
     * 设置头像
     *      设置头像前，首先会将该图片上传到服务器存储，之后服务器返回图片的存储URL，
     *    再调用setUserHeadPic将URL存储到服务器，以后查询头像就使用该URL到服务器下载。
     *
     * @param url 头像的存储URL
     * @param listener 设置结果回调
     */
    public void setUserHeadPic(final String url, final ITCUserInfoMgrListener listener){

        File file = new File(url);

        //"multipart/form-data"
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + Bitmap.CompressFormat.JPEG.toString()),file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);


        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"),UserInfoMgr.getInstance().getUid());
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"),UserInfoMgr.getInstance().getToken());

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<LinkedTreeMap<String,String>>> call = apiStores.requestChangeHead(uid,token, body);
        call.enqueue(new Callback<ResponseJson<LinkedTreeMap<String,String>>>() {
            @Override
            public void onResponse(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Response<ResponseJson<LinkedTreeMap<String,String>>> response) {

                if(AppClient.checkResult(response)){
                    LinkedTreeMap<String,String> res = (LinkedTreeMap<String, String>) response.body().getData().getInfo().get(0);

                    UserInfoMgr.getInstance().getUserInfoBean().setAvatar(res.get("avatar"));
                    UserInfoMgr.getInstance().getUserInfoBean().setAvatar_thumb(res.get("avatar_thumb"));
                    UserInfoMgr.getInstance().saveUserInfo();
                    if (null != listener) listener.OnSetUserInfo(0,"成功");


                }

            }

            @Override
            public void onFailure(Call<ResponseJson<LinkedTreeMap<String,String>>> call, Throwable t) {

                if (null != listener) listener.OnSetUserInfo(0, null);
            }
        });
    }

    /**
     * 设置直播封面
     *      设置直播封面前，首先会将该图片上传到服务器存储，之后服务器返回图片的存储URL，
     *    再调用setUserCoverPic将URL存储到服务器，以后要查询直播封面就使用该URL到服务器下载
     *
     * @param url 直播封面的存储URL
     * @param listener 设置结果回调
     */
    public void setUserCoverPic(final String url, final ITCUserInfoMgrListener listener){

    }


    /**
     * 设置用户定位信息
     *
     * @param location 详细定位信息
     * @param latitude 纬度
     * @param longitude 经度
     * @param listener 设置结果回调
     */
    public void setLocation(@NonNull final String location, final double latitude, final double longitude, final ITCUserInfoMgrListener listener) {
        if (mUserInfoBean.getCity() != null && mUserInfoBean.getCity().equals(location)) {
            if (null != listener) listener.OnSetUserInfo(0, null);
            return;
        }

        TIMFriendshipManager.getInstance().setLocation(location, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG,"setLocation failed:" + i + "," + s);
                if (null != listener) listener.OnSetUserInfo(i,s);
            }

            @Override
            public void onSuccess() {
                //mUserInfoBean.latitude = latitude;
                //mUserInfoBean.longitude = longitude;
                mUserInfoBean.setCity(location);
                if (null != listener) listener.OnSetUserInfo(0, null);
            }
        });
    }

    /**
     * 设置用户ID， 并使用ID向服务器查询用户信息
     *      setUserId一般在登录成功之后调用，用户信息需要提供给其他类使用，或者展示给用户，因此登录成功之后需要立即向服务器查询用户信息，
     *
     * @param userId
     * @param listener 设置结果回调
     */
    public void setUserId(final String userId, final ITCUserInfoMgrListener listener) {
        try {
            queryUserInfo(new ITCUserInfoMgrListener() {
                @Override
                public void OnQueryUserInfo(int error, String errorMsg) {
                    if (0 == error) {
                        mUserInfoBean.setId(userId);
                    } else {
                        Log.e(TAG,"setUserId failed:" + error + "," + errorMsg);
                    }
                    if (null != listener)
                        listener.OnSetUserInfo(error, errorMsg);
                }

                @Override
                public void OnSetUserInfo(int error, String errorMsg) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * @dw 关注
    * @param listener 回调
    * @param attid 关注用户id
    * */
    public void attentionOrCancelUser(final SimpleActionListener listener, String attid){

        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestAttentionUser(mUserInfoBean.getId(),attid,mUserInfoBean.getToken());
        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(listener != null)
                    if(AppClient.checkResult(response)){
                        listener.onSuccess();
                    }

            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if(listener == null)return;

                listener.onFail(0,t.getMessage());
            }
        });
    }

    /**
    * @dw 修改密码
    * @param uid	 用户ID
    * @param token	 用户token
    * @param oldpass 旧密码
    * @param pass	 新密码
    * @param pass2
    */

    public void requestChangePass(String uid, String token, String oldpass, String pass, String pass2, final SimpleActionListener listener){
        AppClient.ApiStores apiStores = AppClient.retrofit().create(AppClient.ApiStores.class);
        Call<ResponseJson<Object>> call = apiStores.requestChangePass(uid,token,oldpass,pass,pass2);

        call.enqueue(new Callback<ResponseJson<Object>>() {
            @Override
            public void onResponse(Call<ResponseJson<Object>> call, Response<ResponseJson<Object>> response) {
                if(AppClient.checkResult(response)){
                    if(listener == null)return;

                    listener.onSuccess();
                }else{
                    listener.onFail(0,"修改失败");
                }
            }

            @Override
            public void onFailure(Call<ResponseJson<Object>> call, Throwable t) {
                if(listener == null)return;

                listener.onFail(0,"修改失败");
            }
        });

    }

    //获取昵称
    public String getNickname() {
        return mUserInfoBean.getUser_nicename();
    }

    //头像封面
    public String getHeadPic() {
        return mUserInfoBean.getAvatar_thumb();
    }

    public String getCoin() {
        return mUserInfoBean.getCoin();
    }
}
