package com.qiji.live.xiaozhibo.logic;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;

import java.util.ArrayList;

/**
 * 直播列表的Adapter
 * 列表项布局格式: R.layout.listview_video_item
 * 列表项数据格式: TCLiveInfo
 */
public class TCLiveListAdapter extends ArrayAdapter<LiveBean> {
    private int resourceId;
    private Activity mActivity;
    private class ViewHolder{
        TextView tvTitle;
        TextView tvHost;
        TextView tvMembers;
        TextView tvAdmires;
        TextView tvLbs;
        ImageView ivCover;
        AvatarImageView ivAvatar;
        ImageView ivLogo;
        LinearLayout title;
    }

    public TCLiveListAdapter(Activity activity, ArrayList<LiveBean> objects) {
        super(activity, R.layout.listview_video_item, objects);
        resourceId = R.layout.listview_video_item;
        mActivity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder)convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            holder = new ViewHolder();
            holder.ivCover = (ImageView) convertView.findViewById(R.id.cover);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.live_title);
            holder.tvHost = (TextView) convertView.findViewById(R.id.host_name);
            holder.tvMembers = (TextView) convertView.findViewById(R.id.live_members);
            holder.tvAdmires = (TextView) convertView.findViewById(R.id.praises);
            holder.tvLbs = (TextView) convertView.findViewById(R.id.live_lbs);
            holder.ivAvatar = (AvatarImageView) convertView.findViewById(R.id.avatar);
            holder.ivLogo = (ImageView) convertView.findViewById(R.id.live_logo);
            holder.title = (LinearLayout) convertView.findViewById(R.id.ll_video_item_title);

            convertView.setTag(holder);
        }

        LiveBean data = getItem(position);

        //直播封面
        String cover = data.getAvatar();
        if (TextUtils.isEmpty(cover)){
            holder.ivCover.setImageResource(R.drawable.bg_default);
        }else{
            RequestManager req = Glide.with(mActivity);
            req.load(cover).placeholder(R.drawable.bg_default).into(holder.ivCover);
        }

        //主播头像
        holder.ivAvatar.setLoadImageUrl(data.getAvatar());

        //主播昵称
        if (TextUtils.isEmpty(data.getUser_nicename())){
            holder.tvHost.setText(TCUtils.getLimitString(data.getUid(), 10));
        }else{
            holder.tvHost.setText( TCUtils.getLimitString(data.getUser_nicename(), 10));
        }
        //主播地址
        if (TextUtils.isEmpty(data.getCity())) {
            holder.tvLbs.setText(getContext().getString(R.string.live_unknown));
        }else{
            holder.tvLbs.setText(TCUtils.getLimitString(data.getCity(), 9));
        }

        //直播标题
        holder.tvTitle.setText(TCUtils.getLimitString(data.getTitle(), 10));
        //直播观看人数
        holder.tvMembers.setText(""+data.getNums());
        //直播点赞数
        holder.tvAdmires.setText(""+data.getLight());
        //视频类型，直播或者回放
        if (StringUtils.toInt(data.getIslive()) == 1) {
            holder.ivLogo.setImageResource(R.drawable.live);
            holder.ivLogo.setVisibility(View.VISIBLE);
        } else {
            //holder.ivLogo.setImageResource(R.drawable.playback);
            holder.ivLogo.setVisibility(View.GONE);
        }
        holder.title.setVisibility(View.GONE);
        //标题
        if(!TextUtils.isEmpty(data.getTitle())){
            holder.title.setVisibility(View.VISIBLE);
            ((TextView)holder.title.getChildAt(0)).setText(data.getTitle());
        }

        return convertView;
    }

}
