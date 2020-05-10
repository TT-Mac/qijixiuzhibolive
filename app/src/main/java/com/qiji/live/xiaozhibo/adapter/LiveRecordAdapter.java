package com.qiji.live.xiaozhibo.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.bean.LiveRecordBean;
import com.qiji.live.xiaozhibo.logic.TCLiveListAdapter;
import com.qiji.live.xiaozhibo.utils.TCUtils;

import java.util.ArrayList;

/**
 * Created by weipeng on 16/11/23.
 */

public class LiveRecordAdapter extends ArrayAdapter<LiveRecordBean> {
    private int resourceId;
    private class ViewHolder{
        TextView tvTitle;
        TextView tvNum;
        TextView tvTime;
    }

    public LiveRecordAdapter(Activity activity, ArrayList<LiveRecordBean> objects) {
        super(activity, R.layout.item_live_record, objects);
        resourceId = R.layout.item_live_record;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder)convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_item_live_record_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_item_live_record_time);
            holder.tvNum = (TextView) convertView.findViewById(R.id.tv_item_live_record_num);

            convertView.setTag(holder);
        }

        LiveRecordBean data = getItem(position);

        holder.tvTitle.setText(TextUtils.isEmpty(data.getTitle().trim()) ? "无标题" : data.getTitle());
        holder.tvNum.setText(data.getNums());
        holder.tvTime.setText(data.dateendtime);
        return convertView;
    }
}
