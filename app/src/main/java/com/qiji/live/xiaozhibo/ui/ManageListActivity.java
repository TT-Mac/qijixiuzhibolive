package com.qiji.live.xiaozhibo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.BaseViewHolder;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.bean.AdminJson;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.ManageListMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;
import com.qiji.live.xiaozhibo.widget.GlobalUserItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ManageListActivity extends TCBaseActivity implements UIInterface, ManageListMgr.ManageListMgrCallback {

    private ManageListMgr mManageListMgr;

    @Bind(R.id.view_title)
    TCActivityTitle mTCActivityTitle;
    //管理员列表空间
    @Bind(R.id.rv_manage_list)
    RecyclerView mRvManageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {

        mManageListMgr = ManageListMgr.getInstance();

        mManageListMgr.setManageListMgrCallback(this);

        mManageListMgr.requestGetManageList(UserInfoMgr.getInstance().getUid());

    }

    @Override
    public void initView() {

        mRvManageList.setLayoutManager(new LinearLayoutManager(this));

        mTCActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void fillUI(final List<AdminJson> mManageList) {

        mRvManageList.setAdapter(new RecyclerView.Adapter<BaseViewHolder>() {
            @Override
            public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(ManageListActivity.this).inflate(R.layout.item_global_user,parent,false);

                return new BaseViewHolder(ManageListActivity.this,view);
            }

            @Override
            public void onBindViewHolder(BaseViewHolder holder, int position) {

                AdminJson data = mManageList.get(position);

                holder.setVisible(R.id.item_iv_att,false);

                holder.setText(R.id.item_tv_sign,data.userinfo.getSignature());

                holder.setText(R.id.item_tv_u_name,data.userinfo.getUser_nicename());

                holder.setImageUrl(R.id.item_iv_head,data.userinfo.getAvatar_thumb());

                holder.setImageRes(R.id.item_iv_level, TCUtils.getLevelRes(StringUtils.toInt(data.userinfo.getLevel())));

                holder.setImageRes(R.id.item_iv_sex,TCUtils.getSexRes(StringUtils.toInt(data.userinfo.getSex())));


            }

            @Override
            public int getItemCount() {
                return mManageList.size();
            }
        });

    }

    @Override
    public void onSuccess(List<AdminJson> adminlist) {

        fillUI(adminlist);
    }



    @Override
    public void onFail(int errorCode, String msg) {

    }
}
