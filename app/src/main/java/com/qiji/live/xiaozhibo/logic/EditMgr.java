package com.qiji.live.xiaozhibo.logic;

import android.support.annotation.NonNull;

import com.qiji.live.xiaozhibo.bean.GlobalUserBean;

import java.util.List;

/**
 * Created by weipeng on 16/11/23.
 */

public class EditMgr {

    private final static String TAG = FansListMgr.class.getSimpleName();


    public final static String TITLE_TEXT = "KEY_NAME";
    public final static String HIDE_TEXT  = "HIDE_NAME";
    public final static String SIZE_TEXT  = "SIZE_NAME";
    public final static String FIELD_KEY  = "FIELD_KEY";

    private static class EditMgrHolder{
        static EditMgr instance = new EditMgr();
    }
    private EditCallback mEditCallback;


    public static EditMgr getInstance(){
        return EditMgrHolder.instance;
    }

    public EditMgr() {

    }

    public void setEditCallback(@NonNull EditCallback editCallback){
        this.mEditCallback = editCallback;
    }


    public interface EditCallback{

          /*
        * 编辑成功
        * */

        void onSuccess();

        /*
        * 搜索失败
        * */

        void onFailure(int code, String msg);
    }
}
