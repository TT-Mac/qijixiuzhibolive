package com.qiji.live.xiaozhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.widget.GlobalUserItem;

import java.util.List;

/**
 * Created by weipeng on 16/11/23.
 */

public class AttentionAdapter extends RecyclerView.Adapter<AttentionAdapter.ViewHolder> {
    private Context mContext;
    private List<GlobalUserBean> mUserInfoBeanList;

    public AttentionAdapter(Context context, List<GlobalUserBean> userInfoBeanList) {
        mContext = context;
        mUserInfoBeanList = userInfoBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(new GlobalUserItem(mContext));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mGlobalUserItem.setUserInfoAndFillUI(mUserInfoBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserInfoBeanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        GlobalUserItem mGlobalUserItem;
        public ViewHolder(View itemView) {
            super(itemView);
            mGlobalUserItem = (GlobalUserItem) itemView;
        }
    }
}
