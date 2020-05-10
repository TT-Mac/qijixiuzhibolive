package com.qiji.live.xiaozhibo.api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.qiji.live.xiaozhibo.BuildConfig;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.bean.CarouselJson;
import com.qiji.live.xiaozhibo.bean.ConversationUserJson;
import com.qiji.live.xiaozhibo.bean.GiftJson;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.bean.HomePageJson;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.LiveRecordBean;
import com.qiji.live.xiaozhibo.bean.AdminJson;
import com.qiji.live.xiaozhibo.bean.MembersJson;
import com.qiji.live.xiaozhibo.bean.PushUrl;
import com.qiji.live.xiaozhibo.bean.ResponseJson;
import com.qiji.live.xiaozhibo.bean.UserDialogInfoJson;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.logic.UserDiamondsMgr;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.utils.TLog;

import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 接口类,包含网络请求初始化操作,参数配置等
 */

public class AppClient{

    static Retrofit mRetrofit;

    public static Retrofit retrofit(){
        if(mRetrofit == null){
            /*
            * 设置cookie
            * */
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            builder.cookieJar(new JavaNetCookieJar(cookieManager));


            if (BuildConfig.DEBUG) {
                // Log信息拦截器
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        TLog.analytics(message);
                    }
                });

                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //设置 Debug Log 模式
                builder.addInterceptor(loggingInterceptor);
            }

            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(TCConstants.SVR_POST_URL + "/api/public/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return mRetrofit;
    }

    public static <T> boolean checkResult(Response<ResponseJson<T>> response){
        if(response.code() != 200){
            Toast.makeText(TCApplication.getInstance(),"服务器出了问题"+ response.code() +"...",Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtils.toInt(response.body().getData().getCode()) == 700){
            LocalBroadcastManager.getInstance(TCApplication.getInstance()).sendBroadcast(new Intent(TCConstants.EXIT_APP));
            return false;
        }

        if(StringUtils.toInt(response.body().getRet()) == 200){
            if(response.body().getData().getCode() != 0){
                TCUtils.getInstanceToast(response.body().getData().getMsg()).show();

                return false;
            }else{
                return true;
            }
        }else{
            TCUtils.getInstanceToast(response.body().getMsg()).show();
            return false;
        }


    }

    public interface ApiStores{


        /**
        * @dw 注册
        * @param name 用户名
        * @param pass 密码
        * @param pass2 确认密码
        * @param code 验证码
        * */
        @GET("?service=Login.userReg")
        Call<ResponseJson<UserInfoBean>> requestRegister(@Query("user_login") String name, @Query("user_pass") String pass, @Query("user_pass2") String pass2, @Query("code") String code);

        /**
        * @dw 注册获取验证码
        * @param phone 手机号
        * */

        @GET("?service=Login.getCode")
        Call<ResponseJson<String>> requestGetCode(@Query("mobile") String phone);

        /**
         * @dw 找回密码获取验证码
         * @param phone 手机号
         * */

        @GET("?service=Login.getForgetCode")
        Call<ResponseJson<String>> requestGetCodeRP(@Query("mobile") String phone);


        /**
        * @dw 登录
        * @param name 账号
        * @param pass 密码
        *
        * */

        @GET("?service=Login.userLogin")
        Call<ResponseJson<UserInfoBean>> requestLogin(@Query("user_login") String name, @Query("user_pass") String pass);


        /**
        * @dw 热门
        * @param page 页数
        * @param uid 用户id
        * */

        @GET("?service=Home.getHot")
        Call<ResponseJson<LiveBean>> requestHot(@Query("uid") String uid);

        /**
         * @dw 关注
         * @param page 页数
         * @param uid 用户id
         * */

        @GET("?service=Home.getFollow")
        Call<ResponseJson<LiveBean>> requestAttention(@Query("uid") String uid);

        /**
         * @dw 最新
         * @param page 页数
         * @param uid 用户id
         * */

        @GET("?service=Home.getNew")
        Call<ResponseJson<LiveBean>> requestNews(@Query("uid") String uid);


        /**
        * @dw 找回密码
        * @param pass 新密码
        * @param code 验证码
        * @param username 用户账号
        * */
        @GET("?service=Login.userFindPass")
        Call<ResponseJson<Object>> requestChangePass(@Query("user_login") String username, @Query("user_pass") String pass, @Query("user_pass2") String pass2, @Query("code") String code);

        /**
        * @dw 首页搜索用户
        * @param key 关键词
        * @param uid
        * */
        @GET("?service=Home.search")
        Call<ResponseJson<GlobalUserBean>> requestSearchUser(@Query("uid") String uid, @Query("key") String key, @Query("p") String page);

        /**
        * @dw 获取用户最新信息
        * @param uid
        * @param token
        * */
        @FormUrlEncoded
        @POST("?service=User.getBaseInfo")
        Call<ResponseJson<UserInfoBean>> requestBaseInfo(@Field("uid") String uid, @Field("token") String token);

        /**
        * @dw 获取钻石数量
        * @param uid 用户id
        * @param token
        * */
        @GET("?service=User.getBalance")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestCoinCount(@Query("uid") String uid, @Query("token") String token);

        /**
        * @dw 修改资料
        * @param uid 用户id
        * @param token
        * @param fields 修改资料组装的json
        * */
        @GET("?service=User.updateFields")
        Call<ResponseJson<Object>> requestChangeInfo(@Query("uid") String uid, @Query("token") String token, @Query("fields") String fields);


        /**
        * @dw 关注
        * @param uid 用户id
        * @param token
        * @param attuid 关注id
        * */
        @GET("?service=User.setAttent")
        Call<ResponseJson<Object>> requestAttentionUser(@Query("uid") String uid, @Query("touid") String attuid, @Query("token") String token);

        /**
        * @dw 请求推流地址
        * @param uid id
        * @param token
        * @param user_nickname 用户昵称
        * @param avatar 头像
        * @param title 标题
        * @param groupid 群组id
        * @param address 地址
        * @param province 省份
        * @param city 城市
        * */

        @FormUrlEncoded
        @POST("?service=Live.createRoom")
        Call<ResponseJson<PushUrl>> requestCreateRoom(@Field("uid") String uid, @Field("token") String token, @Field("user_nicename") String user_nickname
                , @Field("avatar") String avatar, @Field("title") String title, @Field("groupid") String groupid, @Field("address") String address,
                                                      @Field("province") String province, @Field("city") String city, @Field("avatar_thumb") String thumb);

        /**
        * @dw 加入房间
        * @param uid 用户id
        * @param token
        * @param liveuid 主播id
        * @param groupid 群组id
        * @param user_nicename 用户昵称
        * @param avatar 用户头像
        * */

        @FormUrlEncoded
        @POST("?service=Live.enterRoom")
        Call<ResponseJson<Object>> requestJoinRoom(@Field("uid") String uid, @Field("token") String token, @Field("liveuid") String liveuid, @Field("groupid") String groupid, @Field("user_nicename") String user_nicename, @Field("avatar") String avatar);


        /**
        * @dw 获取用户列表
        * @param liveuid 直播id
        * @param groupid 分组id
        * @param p 分页
        * */

        @FormUrlEncoded
        @POST("?service=Live.getUserList")
        Call<ResponseJson<MembersJson>> requestGetMembers(@Field("liveuid") String liveuid, @Field("groupid") String groupid, @Field("p") String p);


        /**
        * @dw 获取轮播图
        * */

        @GET("?service=Home.getSlide")
        Call<ResponseJson<CarouselJson>> requestGetCarouse();

        /**
        * @dw 修改直播状态
        * @param uid 用户id
        * @param token
        * @param status
        * */
        @GET("?service=Live.changeStatus")
        Call<ResponseJson<Object>> requestChangState(@Query("uid") String uid, @Query("token") String token, @Query("status") String status);

        /**
        * @dw 点赞
        * @param liveuid 主播id
        * */
        @GET("?service=Live.updateLight")
        Call<ResponseJson<Object>> requestInternalSend(@Query("liveuid") String liveuid);


        /**
        * @dw 离开房间
        * @param uid 用户id
        * @param token
        * @param liveuid 主播id
        * @param groupid 分组id
        * */
        @FormUrlEncoded
        @POST("?service=Live.leaveRoom")
        Call<ResponseJson<Object>> requestLeaveRoom(@Field("uid") String uid, @Field("token") String token, @Field("liveuid") String liveuid, @Field("groupid") String groupid);


        /**
        * @dw 上传头像
        * @param uid 用户id
        * @param token
        * @param picture 头像文件
        * */

        @Multipart
        @POST("?service=User.updateAvatar")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestChangeHead(@Part("uid") RequestBody uid, @Part("token") RequestBody token, @Part MultipartBody.Part picture);


        /**
         * @dw 粉丝列表
         * @param uid
         * @param touid
         * */
        @GET("?service=User.getFansList")
        Call<ResponseJson<GlobalUserBean>> requestFansList(@Query("uid") String uid, @Query("touid") String touid, @Query("p") String page);


        /**
         * @dw 粉丝列表
         * @param uid
         * @param touid
         * */
        @GET("?service=User.getFollowsList")
        Call<ResponseJson<GlobalUserBean>> requestAttentionList(@Query("uid") String uid, @Query("touid") String touid, @Query("p") String page);

        /**
         * @dw 直播记录
         * @param uid
         * @param touid
         * */
        @GET("?service=User.getLiverecord")
        Call<ResponseJson<LiveRecordBean>> requestLiveRecordList(@Query("uid") String uid, @Query("touid") String touid, @Query("p") String page);

         /**
         * @dw 三方登录
         * @openid
         * @param type 平台名称
         * @param nicename 昵称
         * @param avatar 头像
         * */
        @FormUrlEncoded
        @POST("?service=Login.userLoginByThird")
        Call<ResponseJson<UserInfoBean>> requestOtherLogin(@Field("openid") String openid, @Field("type") String type, @Field("nicename") String nicename, @Field("avatar") String avatar);

        /**
        * @dw 礼物列表
        *
        * */

        @GET("?service=Live.getGiftList")
        Call<ResponseJson<GiftJson>> requestGiftList();

        /**
        * @dw 赠送礼物
        * @param token 用户验证token
        * @param uid   用户id
        * @param liveuid 赠送用户id
        * @param giftid 礼物id
        * @param giftcount 礼物数量
        * @param groupid 分组id
        * */

        @FormUrlEncoded
        @POST("?service=Live.sendGift")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestSendGift(@Field("uid") String uid, @Field("token") String token, @Field("liveuid") String liveuid, @Field("giftid") String giftid, @Field("giftcount") String giftcount, @Field("groupid") String groupid);

        /**
        * @dw 直播间用户弹窗接口
        * @param uid 当前用户id
        * @param touid 点击用户id
        * @param liveuid 主播id
        * */
        @GET("?service=Live.getPop")
        Call<ResponseJson<UserDialogInfoJson>> requestGetDialogInfo(@Query("uid") String uid, @Query("touid") String touid, @Query("liveuid") String liveuid);


        /**
        * @dw 我的收益
        * @param uid 用户id
        * @param token 用户token
        * */
        @FormUrlEncoded
        @POST("?service=User.getProfit")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetProfit(@Field("uid") String uid, @Field("token") String token);

        /**
         * @dw 提现操作
         * @param uid 用户id
         * @param token 用户token
         * */
        @FormUrlEncoded
        @POST("?service=User.setCash")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestProfit(@Field("uid") String uid, @Field("token") String token);

        /**
         * @dw 判断是否关注某人
         * @param uid 当前用户id
         * @param touid 第二者
         *
         * */
        @GET("?service=User.isAttent")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestIsFollow(@Query("uid") String uid, @Query("touid") String touid);


        /**
        * @dw 其他用户主页
        * @param uid 当前用户id
        * @param touid 查看用户id
        * */
        @GET("?service=User.getUserHome")
        Call<ResponseJson<HomePageJson>> requestGetHomePageInfo(@Query("uid") String uid, @Query("touid") String touid);

        /**
        * @dw 拉黑取消拉黑
        * @param uid 当前用户id
        * @param touid 拉黑用户id
        * */
        @GET("?service=User.setBlack")
        Call<ResponseJson<HomePageJson>> requestAddBlackUser(@Query("uid") String uid, @Query("touid") String touid);


        /**
        * @dw 支付宝下单
        * @param uid 用户id
        * @param money 充值金额
        * */
        @GET("?service=Charge.getAliOrder")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetAliPayOrderId(@Query("uid") String uid, @Query("money") String money);

        /**
         * @dw 举报
         * @param uid 用户id
         * @param token
         * @param touid 举报人id
         * @param content 举报内容
         * */
        @FormUrlEncoded
        @POST("?service=Live.setReport")
        Call<ResponseJson<Object>> requestReport(@Field("uid") String uid, @Field("touid") String touid, @Field("token") String token, @Field("content") String content);


        /**
        * @dw 设置管理员
        * @param uid
        * @param token
        * @param liveuid 主播id
        * @param touid 设置为管理员的用户
        * */
        @FormUrlEncoded
        @POST("?service=Live.setAdmin")
        Call<ResponseJson<Object>> requestSetManage(@Field("uid") String uid, @Field("token") String token, @Field("liveuid") String liveuid, @Field("touid") String touid);

        /**
         * @dw 发送弹幕
         * @param uid 用户Id
         * @param token
         * @param liveuid 主播id
         * @param groupid 房间号码
         * @param giftid 礼物id
         * @param giftcount 礼物数量
         * */

        @FormUrlEncoded
        @POST("?service=Live.sendBarrage")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestDanmu(@Field("uid") String uid, @Field("token") String token, @Field("liveuid") String liveuid, @Field("giftid") String giftid, @Field("giftcount") String giftcount, @Field("groupid") String groupid);


        /**
        * @dw 获取管理员列表
        * @param liveuid 主播id
        *
        * */
        @GET("?service=Live.getAdminList")
        Call<ResponseJson<AdminJson>> requestGetManageList(@Query("liveuid") String liveuid);


        /**
         * @dw 获取直播间收入
         * @param liveuid 直播间主播id
         * */
        @GET("?service=Live.getVotes")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetIncome(@Query("liveuid") String liveuid);

        /**
        * @dw 私信对会话列表分类
        * @uid 当前用户id
        * @uids 会话列表用户id以逗号分隔
        * @type 关注类型，0 未关注 1 已关注
        * */
        @GET("?service=User.getMultiInfo")
        Call<ResponseJson<ConversationUserJson>> requestConversationClass(@Query("uid") String uid,@Query("uids") String uids,@Query("type") String type);


        /**
         * @dw 修改密码
         * @param uid	用户ID
         * @param token	用户token
         * @param oldpass	旧密码
         * @param pass	新密码
         * @param pass2
         */
        @FormUrlEncoded
        @POST("?service=User.updatePass")
        Call<ResponseJson<Object>> requestChangePass(@Field("uid") String uid,@Field("token") String token,@Field("oldpass") String oldpass,@Field("pass") String pass,@Field("pass2") String pass2);


        /**
        * @dw 分享链接
        * */
        @GET("?service=Home.getconfig")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetConfig();


        /**
         * @dw 微信支付下单
         * @param price 价格
         * @param num 数量
         * */
        @GET("?service=Charge.getWxOrder")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetWxParam(@Query("uid") String uid,@Query("money") String price,@Query("num") String num);


        /**
        * @dw 获取房间基本信息进行初始化操作
        * @param uid 用户ID
        * @param liveuid 房间主播id
        * */
        @GET("?service=Live.checkStatus")
        Call<ResponseJson<LinkedTreeMap<String,String>>> requestGetRoomBaseInfo(@Query("uid") String uid,@Query("liveuid") String liveuid);

    }
}
