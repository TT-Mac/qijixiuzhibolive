package com.qiji.live.xiaozhibo.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.utils.TCUtils;

/**
 * Created by weipeng on 16/12/1.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views;
    private final Context context;
    private View convertView;

    public BaseViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        this.views = new SparseArray<View>();
        convertView = view;
    }

    public View getConvertView() {
        return convertView;
    }



    protected <T extends View> T retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = retrieveView(viewId);
        view.setText(value);
        return this;
    }
    public BaseViewHolder setImageUrl(int viewId, String imageUrl) {
        ImageView view = retrieveView(viewId);
        TCUtils.showPicWithUrl(context,view,imageUrl,0);
        return this;
    }

    public BaseViewHolder setImageRes(int viewId, int res) {
        ImageView view = retrieveView(viewId);
        view.setImageResource(res);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = retrieveView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }
    public BaseViewHolder linkify(int viewId) {
        TextView view = retrieveView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }
}
