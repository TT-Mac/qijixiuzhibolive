package com.qiji.live.xiaozhibo.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.base.TCConstants;
import com.qiji.live.xiaozhibo.logic.EditMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.inter.ITCUserInfoMgrListener;
import com.qiji.live.xiaozhibo.logic.TCUploadHelper;
import com.qiji.live.xiaozhibo.logic.TCUserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.ui.customviews.TCLineControllerView;
import com.qiji.live.xiaozhibo.ui.customviews.TCLineEditTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 用户信息编辑页面
 */
public class EditUseInfoActivity extends TCBaseActivity implements View.OnClickListener , TCUploadHelper.OnUploadListener {
    private String TAG = getClass().getName();

    private final static int REQ_EDIT_NICKNAME = 0x100;

    private static final int CROP_CHOOSE = 10;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int SAVE_INFO = 0x200;

    private TCUploadHelper uploadHelper;
    private CircleImageView ivHead;
    private TCActivityTitle atTitle;
    private TCLineEditTextView letvNickName;
    private TCLineControllerView lcvSelectSex;
    private TCLineControllerView mLcSign;
    private boolean bPermission = false;

    private Uri iconUrl, iconCrop;

    private void updateView(){
        mLcSign.setContent(UserInfoMgr.getInstance().getUserInfoBean().getSignature());
        letvNickName.setContent(UserInfoMgr.getInstance().getUserInfoBean().getUser_nicename());
        lcvSelectSex.setContent(TCUtils.getSexStr(StringUtils.toInt(UserInfoMgr.getInstance().getUserInfoBean().getSex())));
        TCUtils.showPicWithUrl(this,ivHead,UserInfoMgr.getInstance().getUserInfoBean().getAvatar(),R.drawable.face);
    }

    private void initView(){
        atTitle = (TCActivityTitle) findViewById(R.id.at_eui_edit);
        ivHead = (CircleImageView) findViewById(R.id.iv_eui_head);
        letvNickName = (TCLineEditTextView) findViewById(R.id.letv_eui_nickname);
        lcvSelectSex = (TCLineControllerView)findViewById(R.id.lcv_eui_sex);
        mLcSign = (TCLineControllerView) findViewById(R.id.lcv_eui_sign);
        atTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        initView();

        uploadHelper = new TCUploadHelper(this,this);

        bPermission = checkCropPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 创建封面图片的uri
     *
     * @param type 要创建的URI类型
     *             _icon ：通过相机拍摄图片
     *             _select_icon ： 从文件获取图片文件
     * @return  返回uri
     */
    private Uri createCoverUri(String type) {
        String filename = TCUserInfoMgr.getInstance().getUserId() + type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        if (ContextCompat.checkSelfPermission(EditUseInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditUseInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        if (outputImage.exists()) {
            outputImage.delete();
        }
        return Uri.fromFile(outputImage);
    }

    /**
     * 检查裁剪图像相关的权限
     *
     * @return 权限不足返回false，否则返回true
     */
    private boolean checkCropPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditUseInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditUseInfoActivity.this, Manifest.permission.READ_PHONE_STATE)){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0){
                ActivityCompat.requestPermissions(EditUseInfoActivity.this,
                        permissions.toArray(new String[0]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }


    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        if (!bPermission){
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                iconUrl = createCoverUri("_icon");
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, iconUrl);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                iconUrl = createCoverUri("_select_icon");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;
        }
    }

    /**
     * 图片选择对话框
     */
    private void showPhotoDialog() {
        final Dialog pickDialog = new Dialog(this, R.style.floag_dialog);
        pickDialog.setContentView(R.layout.dialog_pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = pickDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int)(display.getWidth()); //设置宽度

        pickDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) pickDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) pickDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) pickDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                pickDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                pickDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
            }
        });

        pickDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.rl_eui_head:
            showPhotoDialog();
            break;
        case R.id.letv_eui_nickname:
//            letvNickName.setEditTextFocus();
            break;
        case R.id.lcv_eui_sex:
            ShowSelectSexDialog();
            break;
        case R.id.lcv_eui_sign:
            Intent intent = new Intent(this,EditActivity.class);
            intent.putExtra(EditMgr.FIELD_KEY,"signature");
            intent.putExtra(EditMgr.TITLE_TEXT,"个性签名");
            intent.putExtra(EditMgr.SIZE_TEXT,"20");
            intent.putExtra(EditMgr.HIDE_TEXT,"介绍一下自己");
            startActivityForResult(intent,SAVE_INFO);
            break;

        default:
            break;
        }
    }


    /**
     * 打开图片裁剪页面
     *
     * @param uri 裁剪图片的URI
     */
    public void startPhotoZoom(Uri uri) {
        iconCrop = createCoverUri("_icon_crop");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 200);
        intent.putExtra("aspectY", 200);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            Log.e(TAG,"onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode){
            case SAVE_INFO:

                mLcSign.setContent(UserInfoMgr.getInstance().getUserInfoBean().getSignature());
                break;

            case REQ_EDIT_NICKNAME:
                UserInfoMgr.getInstance().setUserNickName(data.getStringExtra(TCTextEditActivity.RETURN_EXTRA), new ITCUserInfoMgrListener() {

                    @Override
                    public void OnQueryUserInfo(int error, String errorMsg) {

                    }

                    @Override
                    public void OnSetUserInfo(int error, String errorMsg) {
                        if (0 != error) Toast.makeText(getApplicationContext(),"昵称不合法，请更换" + errorMsg,Toast.LENGTH_LONG);
                    }
                });
                break;
            case CAPTURE_IMAGE_CAMERA:
                startPhotoZoom(iconUrl);
                break;
            case IMAGE_STORE:
                String path = TCUtils.getPath(this, data.getData());
                if (null != path){
                    Log.d(TAG,"startPhotoZoom->path:" + path);
                    File file = new File(path);
                    startPhotoZoom(Uri.fromFile(file));
                }
                break;
            case CROP_CHOOSE:
                //uploadHelper.uploadCover(iconCrop.getPath());


                UserInfoMgr.getInstance().setUserHeadPic(iconCrop.getPath(), new ITCUserInfoMgrListener() {
                    @Override
                    public void OnQueryUserInfo(int error, String errorMsg) {


                    }

                    @Override
                    public void OnSetUserInfo(int error, String errorMsg) {
                        if (0 != error) Toast.makeText(getApplicationContext(),"设置头像URL失败" + errorMsg,Toast.LENGTH_LONG).show();

                        TCUtils.showPicWithUrl(EditUseInfoActivity.this, ivHead,UserInfoMgr.getInstance().getHeadPic(), R.color.transparent);
                    }
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults){
                    if (ret != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            Toast.makeText(this,"上传头像成功",Toast.LENGTH_LONG).show();
            TCUtils.showPicWithUrl(this, ivHead, url, R.color.transparent);
            UserInfoMgr.getInstance().setUserHeadPic(url, new ITCUserInfoMgrListener() {
                @Override
                public void OnQueryUserInfo(int error, String errorMsg) {
                }

                @Override
                public void OnSetUserInfo(int error, String errorMsg) {
                    if (0 != error) Toast.makeText(getApplicationContext(),"设置头像URL失败" + errorMsg,Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Log.w(TAG, "onUploadResult->failed: "+code);
            Toast.makeText(this, "上传头像失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选择性别对话框
     */
    private void ShowSelectSexDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();


        View view_ss = getLayoutInflater().inflate(R.layout.view_selet_sex, null);
        dialog.setView(view_ss, 0, 0, 0, 0);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width;
        params.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        Button btn_male = (Button) view_ss.findViewById(R.id.btn_ss_male);
        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoMgr.getInstance().setUserSex("1", new ITCUserInfoMgrListener() {
                    @Override
                    public void OnQueryUserInfo(int error, String errorMsg) {

                    }

                    @Override
                    public void OnSetUserInfo(int error, String errorMsg) {
                        if (0 != error)
                            Toast.makeText(getApplicationContext(),"设置性别失败" + errorMsg,Toast.LENGTH_LONG).show();
                        else
                            lcvSelectSex.setContent(TCUtils.getSexStr(StringUtils.toInt(UserInfoMgr.getInstance().getUserInfoBean().getSex())));
                    }
                });
                dialog.dismiss();
            }
        });

        Button btn_female = (Button) view_ss.findViewById(R.id.btn_ss_female);
        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoMgr.getInstance().setUserSex("2", new ITCUserInfoMgrListener() {
                    @Override
                    public void OnQueryUserInfo(int error, String errorMsg) {

                    }

                    @Override
                    public void OnSetUserInfo(int error, String errorMsg) {
                        if (0 != error)
                            Toast.makeText(getApplicationContext(),"设置性别失败" + errorMsg,Toast.LENGTH_LONG).show();
                        else
                            lcvSelectSex.setContent(TCUtils.getSexStr(StringUtils.toInt(UserInfoMgr.getInstance().getUserInfoBean().getSex())));
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
