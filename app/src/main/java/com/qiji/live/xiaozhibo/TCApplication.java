package com.qiji.live.xiaozhibo;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.tencent.bugly.crashreport.CrashReport;
import com.qiji.live.xiaozhibo.base.BaseApplication;
import com.qiji.live.xiaozhibo.base.TCHttpEngine;
import com.qiji.live.xiaozhibo.base.TCLog;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.logic.TCIMInitMgr;
import com.qiji.live.xiaozhibo.utils.CyptoUtils;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLivePusher;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Locale;
import java.util.Properties;

import okhttp3.JavaNetCookieJar;
import okhttp3.logging.HttpLoggingInterceptor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * 小直播应用类，用于全局的操作，如
 * sdk初始化,全局提示框
 */
public class TCApplication extends BaseApplication {

//    private RefWatcher mRefWatcher;

    private static final String BUGLY_APPID = "1400012894";

    private static TCApplication instance;

    private String loginUid;

    private String Token;

    private boolean login = false;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initLogin();
        initSDK();


        //字体
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/dq.ttc")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );



//        mRefWatcher =
//        LeakCanary.install(this);
    }

    public static TCApplication getApplication() {
        return instance;
    }



//    public static RefWatcher getRefWatcher(Context context) {
//        TCApplication application = (TCApplication) context.getApplicationContext();
//        return application.mRefWatcher;
//    }

    /**
     * 初始化SDK，包括Bugly，IMSDK，RTMPSDK等
     */
    public void initSDK() {


        //注册crash上报 bugly组件
        int[] sdkVer = TXLivePusher.getSDKVersion(); //这里调用TXLivePlayer.getSDKVersion()也是可以的
        if (sdkVer != null && sdkVer.length >= 3) {
            if (sdkVer[0] > 0 && sdkVer[1] > 0) {
                //启动bugly组件，bugly组件为腾讯提供的用于crash上报和分析的开放组件，如果您不需要该组件，可以自行移除
                CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
                strategy.setAppVersion(String.format(Locale.US, "%d.%d.%d",sdkVer[0],sdkVer[1],sdkVer[2]));
                CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, true, strategy);
            }
        }

        TCIMInitMgr.init(getApplicationContext());

        //设置rtmpsdk log回调，将log保存到文件
        TXLiveBase.getInstance().listener = new TCLog(getApplicationContext());

        //初始化httpengine
        TCHttpEngine.getInstance().initContext(getApplicationContext());

        Log.w("TCLog","app init sdk");
    }

    private void initLogin() {
        UserInfoBean user = getLoginUser();
        if (null != user && StringUtils.toInt(user.getId()) > 0) {
            login = true;
            loginUid = user.getId();
            Token = user.getToken();
        } else {
            this.cleanLoginInfo();
        }
    }
    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static TCApplication getInstance() {
        return instance;
    }
    public static void showToast(String s) {
        Toast.makeText(getInstance(),s,Toast.LENGTH_LONG).show();
    }
    public static void showToast(int s) {
        Toast.makeText(getInstance(),getInstance().getResources().getString(s),Toast.LENGTH_LONG).show();
    }
    public boolean isLogin() {
        return login;
    }
    public String getLoginUid() {
        return loginUid;
    }
    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final UserInfoBean user) {
        this.loginUid = user.getId();
        this.Token = user.getToken();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getUser_nicename());
                setProperty("user.token", user.getToken());
                setProperty("user.sign", user.getSignature());
                setProperty("user.avatar", user.getAvatar());
                setProperty("user.pwd",
                        CyptoUtils.encode("PhoneLiveApp", user.getUser_pass()));


                setProperty("user.city", user.getCity() == null ? "" : user.getCity());
                setProperty("user.coin",user.getCoin());
                setProperty("user.sex", String.valueOf(user.getSex()));
                setProperty("user.signature",user.getSignature());
                setProperty("user.avatar_thumb",user.getAvatar_thumb());
                setProperty("user.level", String.valueOf(user.getLevel()));
                setProperty("user.isrz", String.valueOf(user.getIsrz() == null ? "0":user.getIsrz()));
                setProperty("user.birthday",user.getBirthday() == null ? "" : user.getBirthday());

            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final UserInfoBean user) {
        setProperties(new Properties() {
            {
                setProperty("user.uid",user.getId());
                setProperty("user.name", user.getUser_nicename() == null ? "" : user.getUser_nicename());
                setProperty("user.token", user.getToken());
                setProperty("user.sign", user.getSignature() == null ? "" : user.getSignature());
                setProperty("user.avatar", user.getAvatar() == null ? "" : user.getAvatar());
                setProperty("user.pwd",
                        CyptoUtils.encode("PhoneLiveApp", user.getUser_pass()));

                setProperty("user.city", user.getCity() == null ? "" : user.getCity());

                setProperty("user.coin",user.getCoin());
                setProperty("user.sex", String.valueOf(user.getSex()));
                setProperty("user.signature",user.getSignature() == null ? "" : user.getSignature());
                setProperty("user.avatar_thumb",user.getAvatar_thumb() == null ? "" : user.getAvatar_thumb());
                setProperty("user.level", String.valueOf(user.getLevel()));
                setProperty("user.isrz",String.valueOf(user.getIsrz() == null ? "0":user.getIsrz()));
                setProperty("user.birthday",user.getBirthday() == null ? "" : user.getBirthday());
            }
        });
    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public UserInfoBean getLoginUser() {
        UserInfoBean user = new UserInfoBean();
        user.setId(getProperty("user.uid"));
        user.setAvatar(getProperty("user.avatar"));
        user.setUser_nicename(getProperty("user.name"));
        user.setUser_pass(getProperty("user.pwd"));
        user.setSignature(getProperty("user.sign"));
        user.setToken(getProperty("user.token"));
        user.setVotes(getProperty("user.votes")); //HHH 2016-09-13
        user.setCity(getProperty("user.city"));
        user.setCoin(getProperty("user.coin"));
        String sex = getProperty("user.sex");
        user.setSex(sex);
        user.setSignature(getProperty("user.signature"));
        user.setAvatar(getProperty("user.avatar"));
        String level = getProperty("user.level");
        user.setLevel(level);
        user.setAvatar_thumb(getProperty("user.avatar_thumb"));
        user.setBirthday(getProperty("user.birthday"));
        user.setIsrz(getProperty("user.isrz"));
        user.setBirthday(getProperty("user.birthday"));
        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = "0";
        this.login = false;
        removeProperty("user.birthday","user.isrz","user.birthday","user.avatar_thumb","user.uid", "user.token", "user.name", "user.pwd", "user.avatar","user.sign","user.city","user.coin","user.sex","user.signature","user.signature","user.avatar","user.level");
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }
}
