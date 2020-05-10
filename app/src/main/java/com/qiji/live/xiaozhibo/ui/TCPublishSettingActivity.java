package com.qiji.live.xiaozhibo.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.utils.CameraInterface;
import com.qiji.live.xiaozhibo.utils.ShareUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.inter.ITCUserInfoMgrListener;
import com.qiji.live.xiaozhibo.logic.TCLocationHelper;
import com.qiji.live.xiaozhibo.logic.TCUploadHelper;
import com.qiji.live.xiaozhibo.logic.TCUserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCCustomSwitch;
import com.qiji.live.xiaozhibo.utils.TDevice;
import com.qiji.live.xiaozhibo.widget.CameraSurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class TCPublishSettingActivity extends TCBaseActivity implements UIInterface,View.OnClickListener, TCUploadHelper.OnUploadListener, TCLocationHelper.OnLocationListener, PlatformActionListener {
    private TextView BtnBack, BtnPublish;
    private Dialog mPicChsDialog;
    private ImageView cover;
    private Uri fileUri, cropUri;
    private TextView tvPicTip;
    private TextView tvLBS;
    private TCCustomSwitch btnLBS;
    private TextView tvTitle;
    private TCUploadHelper mUploadHelper;


    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final String TAG = TCPublishSettingActivity.class.getSimpleName();

    private static final int CROP_CHOOSE = 10;
    private boolean mUploading = false;
    private boolean mPermission = false;

    private RadioGroup mRGBitrate;
    private RadioGroup mRGRecordType;
//    private RadioGroup mRGOrientation;
    private RelativeLayout mRLBitrate;
    private int mRecordType = TCConstants.RECORD_TYPE_CAMERA;
    private int mBitrateType = TCConstants.BITRATE_NORMAL;

    private ImageView mBgImageView;
    private ImageView mIvBack;

//    private int mOrientation = TCConstants.ORIENTATION_LANDSCAPE;

    private int[] shareIcon =  new int[]{R.id.iv_publish_setting_qq,R.id.iv_publish_setting_wx,R.id.iv_publish_setting_kj,R.id.iv_publish_setting_pyq};

    private Handler mHandler = new Handler();


    //分享平台
    private String mShareName;
    private int mSelectShareBtnId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_setting);
        mUploadHelper = new TCUploadHelper(this, this);


        initView();
        tvTitle.setAlpha(0.7f);
        mPermission = checkPublishPermission();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //定位
        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                btnLBS.setChecked(true, true);
                tvLBS.setText(R.string.text_live_location);
                if (TCLocationHelper.checkLocationPermission(TCPublishSettingActivity.this)) {
                    if (!TCLocationHelper.getMyLocation(TCPublishSettingActivity.this,TCPublishSettingActivity. this)) {
                        tvLBS.setText(getString(R.string.text_live_lbs_fail));
                        tvLBS.setTextColor(Color.parseColor("#80FFFFFF"));
                        //Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
                        btnLBS.setChecked(false, false);
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_publish:
                //trim避免空格字符串
                /*if (TextUtils.isEmpty(tvTitle.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "请输入非空直播标题", Toast.LENGTH_SHORT).show();
                } else */if(TCUtils.getCharacterNum(tvTitle.getText().toString()) > TCConstants.TV_TITLE_MAX_LEN){
                    Toast.makeText(getApplicationContext(), "直播标题过长 ,最大长度为"+TCConstants.TV_TITLE_MAX_LEN/2, Toast.LENGTH_SHORT).show();
                } else if (mUploading) {
                    Toast.makeText(getApplicationContext(), getString(R.string.publish_wait_uploading), Toast.LENGTH_SHORT).show();
                } else if(!TCUtils.isNetworkAvailable(this)) {
                    Toast.makeText(getApplicationContext(), "当前网络环境不能发布直播", Toast.LENGTH_SHORT).show();
                } else {

                    if (mRecordType == TCConstants.RECORD_TYPE_SCREEN) {
                        //录屏
                        Intent intent = new Intent(this, TCScreenRecordActivity.class);
                        intent.putExtra(TCConstants.ROOM_TITLE,
                                TextUtils.isEmpty(tvTitle.getText().toString()) ? TCUserInfoMgr.getInstance().getNickname() : tvTitle.getText().toString());
                        intent.putExtra(TCConstants.USER_ID, UserInfoMgr.getInstance().getUid());
                        intent.putExtra(TCConstants.USER_TOKEN,UserInfoMgr.getInstance().getToken());
                        intent.putExtra(TCConstants.USER_NICK, UserInfoMgr.getInstance().getUserInfoBean().getUser_nicename());
                        intent.putExtra(TCConstants.USER_HEADPIC, UserInfoMgr.getInstance().getUserInfoBean().getAvatar());
                        intent.putExtra(TCConstants.COVER_PIC, UserInfoMgr.getInstance().getUserInfoBean().getAvatar());
//                        intent.putExtra(TCConstants.SCR_ORIENTATION, mOrientation);
                        intent.putExtra(TCConstants.BITRATE, mBitrateType);
                        intent.putExtra(TCConstants.USER_LOC,
                                tvLBS.getText().toString().equals(getString(R.string.text_live_lbs_fail)) ||
                                        tvLBS.getText().toString().equals(getString(R.string.text_live_location)) ?
                                        getString(R.string.text_live_close_lbs) : tvLBS.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {

                        if(!checkToShare()){
                            startLivePublisherActivity();
                        }

                    }

                }
                break;
            case R.id.cover:
                mPicChsDialog.show();
                break;
            case R.id.btn_lbs:
                if (btnLBS.getChecked()) {
                    btnLBS.setChecked(false, true);
                    tvLBS.setText(R.string.text_live_close_lbs);
                } else {
                    btnLBS.setChecked(true, true);
                    tvLBS.setText(R.string.text_live_location);
                    if (TCLocationHelper.checkLocationPermission(this)) {
                        if (!TCLocationHelper.getMyLocation(this, this)) {
                            tvLBS.setText(getString(R.string.text_live_lbs_fail));
                            //Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
                            btnLBS.setChecked(false, false);
                        }
                    }
                }
                break;
            case R.id.iv_publish_setting_back:
                finish();

                break;
            //qq分享
            case R.id.iv_publish_setting_qq:

                changeShareIconState(R.id.iv_publish_setting_qq);
                break;
            //qq空间分享
            case R.id.iv_publish_setting_kj:
                changeShareIconState(R.id.iv_publish_setting_kj);

                break;
            //微信分享
            case R.id.iv_publish_setting_wx:
                changeShareIconState(R.id.iv_publish_setting_wx);
                break;
            //朋友圈分享
            case R.id.iv_publish_setting_pyq:
                changeShareIconState(R.id.iv_publish_setting_pyq);
                break;
        }
    }

    //修改分享图标点击状态
    private void changeShareIconState(int vid) {

        int[] icon = new int[]{
                R.drawable.q1,
                R.drawable.w1,
                R.drawable.k1,
                R.drawable.p1,
                R.drawable.q2,
                R.drawable.w2,
                R.drawable.k2,
                R.drawable.p2
        };
        for(int i = 0; i < 4; i ++){
            if(vid != shareIcon[i]){
                ((ImageView)findViewById(shareIcon[i])).setImageResource(icon[i]);
            }else{
                if(shareIcon[i] != mSelectShareBtnId){
                    ((ImageView)findViewById(shareIcon[i])).setImageResource(icon[i + 4]);
                }else{
                    ((ImageView)findViewById(shareIcon[i])).setImageResource(icon[i]);

                    if(mSelectShareBtnId == vid){
                        mSelectShareBtnId = 0;
                    }
                }

            }
        }


        if(mSelectShareBtnId != 0){
            mSelectShareBtnId = vid;
        }

        switch (vid){
            case R.id.iv_publish_setting_qq:

                if(mShareName == QQ.NAME){
                    mShareName = null;
                    return;
                }
                mShareName = QQ.NAME;
                break;
            case R.id.iv_publish_setting_kj:
                if(mShareName == QZone.NAME){
                    mShareName = null;
                    return;
                }
                mShareName = QZone.NAME;

                break;
            case R.id.iv_publish_setting_wx:
                if(mShareName == Wechat.NAME){
                    mShareName = null;
                    return;
                }
                mShareName = Wechat.NAME;

                break;
            case R.id.iv_publish_setting_pyq:
                if(mShareName == WechatMoments.NAME){
                    mShareName = null;
                    return;
                }
                mShareName = WechatMoments.NAME;

                break;
        }
        mSelectShareBtnId = vid;
    }

    private void startLivePublisherActivity() {


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //摄像头
                if(mHandler == null)return;

                Intent intent = new Intent(TCPublishSettingActivity.this, TCLivePublisherActivity.class);
                intent.putExtra(TCConstants.ROOM_TITLE,
                        TextUtils.isEmpty(tvTitle.getText().toString()) ? TCUserInfoMgr.getInstance().getNickname() : tvTitle.getText().toString());
                intent.putExtra(TCConstants.USER_ID, UserInfoMgr.getInstance().getUid());
                intent.putExtra(TCConstants.USER_TOKEN,UserInfoMgr.getInstance().getToken());
                intent.putExtra(TCConstants.USER_NICK, UserInfoMgr.getInstance().getNickname());
                intent.putExtra(TCConstants.USER_HEADPIC,  UserInfoMgr.getInstance().getNickname());
                intent.putExtra(TCConstants.COVER_PIC, UserInfoMgr.getInstance().getHeadPic());
                intent.putExtra(TCConstants.USER_LOC,
                        tvLBS.getText().toString().equals(getString(R.string.text_live_lbs_fail)) ||
                                tvLBS.getText().toString().equals(getString(R.string.text_live_location)) ?
                                getString(R.string.text_live_close_lbs) : tvLBS.getText().toString());
                startActivity(intent);
                finish();
            }
        },500);


    }

    //分享到三方平台
    private boolean checkToShare(){

        if(mShareName != null){
            ShareUtils.share2(this,mShareName,UserInfoMgr.getInstance().getUserInfoBean(),this);
            return true;
        }
        return false;

    }

    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.dialog_pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }


    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        if (!mPermission) {
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("");
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                fileUri = createCoverUri("_select");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TCPublishSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TCPublishSettingActivity.this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TCPublishSettingActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(TCPublishSettingActivity.this,
                        permissions.toArray(new String[0]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    private boolean checkScrRecordPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private Uri createCoverUri(String type) {
        String filename = TCUserInfoMgr.getInstance().getUserId() + type + ".jpg";
        String path = Environment.getExternalStorageDirectory()+ "/xiaozhibo";

        File outputImage = new File(path, filename);
        if (ContextCompat.checkSelfPermission(TCPublishSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TCPublishSettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        try {
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (outputImage.exists()) {
                outputImage.delete();
            }
//            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "生成封面失败", Toast.LENGTH_SHORT).show();
        }

        return Uri.fromFile(outputImage);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA:
                    startPhotoZoom(fileUri);
                    break;
                case IMAGE_STORE:
                    String path = TCUtils.getPath(this, data.getData());
                    if (null != path) {
                        Log.d(TAG, "startPhotoZoom->path:" + path);
                        File file = new File(path);
                        startPhotoZoom(Uri.fromFile(file));
                    }
                    break;
                case CROP_CHOOSE:
                    mUploading = true;
                    tvPicTip.setVisibility(View.GONE);
//                    cover.setImageBitmap(null);
//                    cover.setImageURI(cropUri);
                    mUploadHelper.uploadCover(cropUri.getPath());

                    break;

            }
        }

    }

    public void startPhotoZoom(Uri uri) {
        cropUri = createCoverUri("_crop");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 750);
        intent.putExtra("aspectY", 550);
        intent.putExtra("outputX", 750);
        intent.putExtra("outputY", 550);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TCConstants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!TCLocationHelper.getMyLocation(this, this)) {
                        tvLBS.setText(getString(R.string.text_live_lbs_fail));
                        btnLBS.setChecked(false, false);
                    }
                }
                break;
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(int code, double lat1, double long1, String location) {
        if (btnLBS.getChecked()) {
            if (0 == code) {
                tvLBS.setText(location);
                UserInfoMgr.getInstance().setLocation(location, lat1, long1, new ITCUserInfoMgrListener() {
                    @Override
                    public void OnQueryUserInfo(int error, String errorMsg) {
                        // TODO: 16/8/10
                    }

                    @Override
                    public void OnSetUserInfo(int error, String errorMsg) {
                        if (0 != error) Toast.makeText(getApplicationContext(),"设置位置失败" + errorMsg,Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                tvLBS.setText(getString(R.string.text_live_lbs_fail));
            }
        } else {
            UserInfoMgr.getInstance().setLocation("", 0, 0, new ITCUserInfoMgrListener() {
                @Override
                public void OnQueryUserInfo(int error, String errorMsg) {
                    // TODO: 16/8/10
                }

                @Override
                public void OnSetUserInfo(int error, String errorMsg) {
                    if (0 != error) Toast.makeText(getApplicationContext(),"设置位置失败" + errorMsg,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUploadResult(int code, String url) {

        if (0 == code) {
            TCUserInfoMgr.getInstance().setUserCoverPic(url, new ITCUserInfoMgrListener() {
                @Override
                public void OnQueryUserInfo(int error, String errorMsg) {
                    // TODO: 16/8/10
                }

                @Override
                public void OnSetUserInfo(int error, String errorMsg) {

                }
            });
            RequestManager req = Glide.with(this);
            req.load(url).into(cover);
            Toast.makeText(this, "上传封面成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "上传封面失败，错误码 "+code, Toast.LENGTH_SHORT).show();
        }
        mUploading = false;
    }

    @Override
    public void initData() {



    }



    @Override
    public void initView() {


        mIvBack = (ImageView) findViewById(R.id.iv_publish_setting_back);
        tvTitle = (TextView) findViewById(R.id.live_title);
        BtnBack = (TextView) findViewById(R.id.btn_cancel);
        tvPicTip = (TextView) findViewById(R.id.tv_pic_tip);
        BtnPublish = (TextView) findViewById(R.id.btn_publish);
        cover = (ImageView) findViewById(R.id.cover);
        tvLBS = (TextView) findViewById(R.id.address);
        btnLBS = (TCCustomSwitch) findViewById(R.id.btn_lbs);
        mBgImageView = (ImageView) findViewById(R.id.iv_publish_setting_bg);


        //模糊封面
        //getIntent().getStringExtra(TCConstants.COVER_PIC)
        //TCUtils.blurBgPic(this, mBgImageView,"", R.drawable.bg_dark);
        mBgImageView.setImageResource(R.drawable.bg_dark);

        mRGRecordType = (RadioGroup) findViewById(R.id.rg_record_type);
        mRGBitrate = (RadioGroup) findViewById(R.id.rg_bitrate);
        mRLBitrate= (RelativeLayout) findViewById(R.id.rl_bitrate);

        mRGRecordType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_record_camera:
                        mRecordType = TCConstants.RECORD_TYPE_CAMERA;
                        mRLBitrate.setVisibility(View.GONE);
                        break;
                    case R.id.rb_record_screen:
                        if (!checkScrRecordPermission()) {
                            Toast.makeText(getApplicationContext(), "当前安卓系统版本过低，仅支持5.0及以上系统", Toast.LENGTH_SHORT).show();
                            mRGRecordType.check(R.id.rb_record_camera);
                            return;
                        }
                        try {
                            TCUtils.checkFloatWindowPermission(TCPublishSettingActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mRLBitrate.setVisibility(View.VISIBLE);
                        mRecordType = TCConstants.RECORD_TYPE_SCREEN;
                        break;
                    default:
                        break;
                }
            }
        });

        mRGBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_bitrate_slow:
                        mBitrateType = TCConstants.BITRATE_SLOW;
                        break;
                    case R.id.rb_bitrate_normal:
                        mBitrateType = TCConstants.BITRATE_NORMAL;
                        break;
                    case R.id.rb_bitrate_fast:
                        mBitrateType = TCConstants.BITRATE_FAST;
                        break;
                    default:
                        break;
                }
            }
        });
        mIvBack.setOnClickListener(this);
        cover.setOnClickListener(this);
        BtnBack.setOnClickListener(this);
        BtnPublish.setOnClickListener(this);
        btnLBS.setOnClickListener(this);

        initPhotoDialog();


        String strCover = UserInfoMgr.getInstance().getHeadPic();
        if(!TextUtils.isEmpty(strCover)) {
            RequestManager req = Glide.with(this);
            req.load(strCover).into(cover);
            tvPicTip.setVisibility(View.GONE);
        } else {
            cover.setImageResource(R.drawable.publish_background);
        }
    }



    //分享回调
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

         startLivePublisherActivity();

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

        startLivePublisherActivity();
    }

    @Override
    public void onCancel(Platform platform, int i) {


        startLivePublisherActivity();
    }
}
