package com.qiji.live.xiaozhibo.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.bean.SimpleUserInfo;
import com.qiji.live.xiaozhibo.inter.RecyclerViewItemClick;
import com.qiji.live.xiaozhibo.ui.fragment.UserInfoDialogFragment;
import com.qiji.live.xiaozhibo.utils.TCUtils;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by teckjiang on 2016/8/21.
 * 直播头像列表Adapter
 */
public class TCUserAvatarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<SimpleUserInfo> mUserAvatarList;
    Context mContext;
    //主播id
    private String mPusherId;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 50;

    //点击事件接口
    private RecyclerViewItemClick mRecyclerViewItemClick;


    public TCUserAvatarListAdapter(Context context, String pusherId) {
        this.mContext = context;
        this.mPusherId = pusherId;
        this.mUserAvatarList = new LinkedList<>();
    }


    public void setRecyclerViewItemClick(RecyclerViewItemClick recyclerViewItemClick) {
        mRecyclerViewItemClick = recyclerViewItemClick;
    }

    /**
     * 添加用户信息
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(SimpleUserInfo userInfo) {

        //去除主播头像
        if(userInfo.uid.equals(mPusherId))
            return false;

        //去重操作
        for (SimpleUserInfo tcSimpleUserInfo : mUserAvatarList) {
            if(tcSimpleUserInfo.uid.equals(userInfo.uid))
                return false;
        }

        //始终显示新加入item为第一位
        mUserAvatarList.add(0, userInfo);
        //超出时删除末尾项
        if(mUserAvatarList.size() > TOP_STORGE_MEMBER)
            mUserAvatarList.remove(TOP_STORGE_MEMBER);
        notifyItemInserted(0);
        return true;
    }

    public SimpleUserInfo getItem(int pos){
        return mUserAvatarList.get(pos);
    }

    public void removeItem(String userId) {
        SimpleUserInfo tempUserInfo = null;

        for(SimpleUserInfo userInfo : mUserAvatarList)
            if(userInfo.uid.equals(userId))
                tempUserInfo = userInfo;


        if(null != tempUserInfo) {
            mUserAvatarList.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_avatar, parent, false);

        final AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view);
        avatarViewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUserInfo userInfo = mUserAvatarList.get(avatarViewHolder.getAdapterPosition());

                if(mRecyclerViewItemClick == null)return;

                mRecyclerViewItemClick.onItemClick(avatarViewHolder.getAdapterPosition());
                //Toast.makeText(mContext.getApplicationContext(),"当前点击用户： " + userInfo.uid, Toast.LENGTH_SHORT).show();
            }
        });

        return avatarViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TCUtils.showPicWithUrl(mContext, ((AvatarViewHolder)holder).ivAvatar,mUserAvatarList.get(position).avatar,
                R.drawable.face);

    }

    @Override
    public int getItemCount() {
        return mUserAvatarList != null? mUserAvatarList.size(): 0;
    }

    private class AvatarViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }


}
