package com.qiji.live.xiaozhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.LiveBean;
import com.qiji.live.xiaozhibo.logic.TCLiveInfo;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TDevice;
import com.qiji.live.xiaozhibo.widget.AvatarImageView;
import com.qiji.live.xiaozhibo.widget.LoadUrlImageView;

import java.util.ArrayList;

/**
 * Created by weipeng on 16/11/11.
 * 最新adapter
 */

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.NewsHolder> {

    private ArrayList<LiveBean> mLiveInfoArrayList;

    private Context mContext;

    private int mItemWidth;

    private NewAdapterItemClickInterface mNewAdapterItemClickInterface;

    public NewAdapter(Context context,ArrayList<LiveBean> liveInfoArrayList) {
        mLiveInfoArrayList = liveInfoArrayList;
        mContext = context;
        mItemWidth = (int) (TDevice.getScreenWidth()/2);
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NewsHolder newsHolder = new NewsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news,parent,false));


        return newsHolder;
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, final int position) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(mItemWidth,mItemWidth - 40);

        holder.itemView.setLayoutParams(params);

        LiveBean data = mLiveInfoArrayList.get(position);
        //昵称
        holder.tvName.setText(data.getUser_nicename());
        //头像
        holder.ivHead.setLoadImageUrl(data.getAvatar());
        //封面
        holder.ivPic.setDefaultBg(R.drawable.bg_default);
        holder.ivPic.setLoadImageUrl(data.getAvatar());
        //点击事件
        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNewAdapterItemClickInterface == null)return;

                mNewAdapterItemClickInterface.onItemClickListener(position);
            }
        });
        holder.liveLogo.setVisibility(View.GONE);
        if(StringUtils.toInt(data.getIslive()) == 1){
            holder.liveLogo.setVisibility(View.VISIBLE);
        }

    }

    public void setOnItemClickListener(NewAdapterItemClickInterface onItemClick){
        this.mNewAdapterItemClickInterface = onItemClick;
    };

    @Override
    public int getItemCount() {
        return mLiveInfoArrayList.size();
    }

    public LiveBean getItem(int i) {
        return mLiveInfoArrayList.get(i);
    }

    public void addAll(ArrayList<LiveBean> clone) {
        this.mLiveInfoArrayList = clone;
    }

    public void clear() {
        mLiveInfoArrayList.clear();
        notifyDataSetChanged();
    }

    public int getCount() {
        return mLiveInfoArrayList.size();
    }

    public interface NewAdapterItemClickInterface{
        void onItemClickListener(int pos);
    }

    class NewsHolder extends RecyclerView.ViewHolder{

        LoadUrlImageView ivPic;
        AvatarImageView ivHead;
        TextView tvName;
        ImageView liveLogo;
        public NewsHolder(View itemView) {
            super(itemView);

            ivPic = (LoadUrlImageView) itemView.findViewById(R.id.item_iv_news_pic);
            ivHead = (AvatarImageView) itemView.findViewById(R.id.item_iv_news_u_head);
            tvName = (TextView) itemView.findViewById(R.id.item_tv_news_u_name);
            liveLogo = (ImageView) itemView.findViewById(R.id.item_news_live);
        }
    }
}
