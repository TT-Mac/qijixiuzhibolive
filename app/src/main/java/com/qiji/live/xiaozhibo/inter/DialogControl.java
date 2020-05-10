package com.qiji.live.xiaozhibo.inter;

import android.app.ProgressDialog;

/**
 * Created by weipeng on 16/10/11.
 */
public interface DialogControl {

    public abstract void hideWaitDialog();

    public abstract ProgressDialog showWaitDialog();

    public abstract ProgressDialog showWaitDialog(int resid);

    public abstract ProgressDialog showWaitDialog(String text);
}