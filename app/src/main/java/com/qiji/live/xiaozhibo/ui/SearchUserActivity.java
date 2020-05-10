package com.qiji.live.xiaozhibo.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.adapter.SearchUserAdapter;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.bean.GlobalUserBean;
import com.qiji.live.xiaozhibo.bean.UserInfoBean;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.logic.SearchMgr;
import com.qiji.live.xiaozhibo.utils.TDevice;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
*
* 搜索界面
*
* */
public class SearchUserActivity extends TCBaseActivity implements UIInterface, SearchMgr.SearchCallback, TextView.OnEditorActionListener {


    @Bind(R.id.tv_search_cancel)
    TextView mTvBack;

    @Bind(R.id.et_search_input)
    EditText mEtSearchInput;

    @Bind(R.id.rv_search_data)
    RecyclerView mRvView;

    @Bind(R.id.ll_search_not_data)
    LinearLayout mLlBgNotData;

    //根布局
    @Bind(R.id.root_view)
    RelativeLayout mRoot;

    private SearchMgr mSearchMgr;
    private String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {
        mSearchMgr = SearchMgr.getInstance();
        mSearchMgr.setSearchCallback(this);

    }

    @Override
    public void initView() {


        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEtSearchInput.setOnEditorActionListener(this);

        mRvView.setLayoutManager(new LinearLayoutManager(this));


    }

    public static void startSearchUserActivity(Context context){
        Intent intent = new Intent(context,SearchUserActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onSuccess(List<GlobalUserBean> list) {
        if(list.size() == 0){
            mLlBgNotData.setVisibility(View.VISIBLE);
        }else{
            mLlBgNotData.setVisibility(View.GONE);
        }
        mRvView.setAdapter(new SearchUserAdapter(this,list));
    }

    @Override
    public void onFailure(int code, String msg) {
        showToast(msg);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH  ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
            if (TextUtils.isEmpty(mEtSearchInput.getText())){
                mEtSearchInput.requestFocus();
                mEtSearchInput.setError("不能为空");
            }else {
                //do reseach
                if(mSearchMgr.checkKeyWord(mKey = mEtSearchInput.getText().toString())){

                    mSearchMgr.searchUser(mKey);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //操
        if(!TextUtils.isEmpty(mKey)){
            mSearchMgr.searchUser(mKey);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
