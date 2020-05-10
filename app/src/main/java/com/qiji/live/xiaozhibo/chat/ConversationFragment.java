package com.qiji.live.xiaozhibo.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tencent.TIMConversation;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.TCApplication;
import com.qiji.live.xiaozhibo.base.BaseFragment;
import com.qiji.live.xiaozhibo.bean.ConversationUserJson;
import com.qiji.live.xiaozhibo.chat.adapter.ConversationAdapter;
import com.qiji.live.xiaozhibo.chat.interf.ConversationView;
import com.qiji.live.xiaozhibo.chat.interf.FriendshipMessageView;
import com.qiji.live.xiaozhibo.chat.model.Conversation;
import com.qiji.live.xiaozhibo.chat.model.CustomMessage;
import com.qiji.live.xiaozhibo.chat.model.FriendshipConversation;
import com.qiji.live.xiaozhibo.chat.model.MessageFactory;
import com.qiji.live.xiaozhibo.chat.model.NomalConversation;
import com.qiji.live.xiaozhibo.chat.presenter.ConversationPresenter;
import com.qiji.live.xiaozhibo.chat.presenter.FriendshipManagerPresenter;
import com.qiji.live.xiaozhibo.logic.PrivateChatMgr;
import com.qiji.live.xiaozhibo.logic.UserInfoMgr;
import com.qiji.live.xiaozhibo.utils.StringUtils;
import com.qiji.live.xiaozhibo.utils.TCUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weipeng on 2016/12/3.
 * 私信页面
 */

public class ConversationFragment extends BaseFragment implements  ConversationView {

    @Bind(R.id.lv_private_chat_list)
    ListView mLvChatList;
    private PrivateChatMgr mPrivateChatMgr;

    private List<Conversation> conversationList = new LinkedList<>();

    private List<ConversationUserJson> mConversationUser = new LinkedList<>();

    private ConversationAdapter adapter;

    private ConversationPresenter presenter;
    private String mType;

    /*private FriendshipConversation friendshipConversation;

    private FriendshipManagerPresenter friendshipManagerPresenter;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_private_chat,null);

        ButterKnife.bind(this,view);

        initView(view);
        initData();


        return view;
    }

    @Override
    public void initView(View view) {

        adapter = new ConversationAdapter(getActivity(), R.layout.item_conversation, mConversationUser);
        mLvChatList.setAdapter(adapter);
        mLvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conversationList.get(position).navToDetail(getActivity());

            }
        });
    }

    @Override
    public void initData() {

        mType = getArguments().getString("type");

        mPrivateChatMgr = PrivateChatMgr.getInstance();
        presenter = new ConversationPresenter(this);
        presenter.getConversation();

        //friendshipManagerPresenter = new FriendshipManagerPresenter(this);

    }


    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    @Override
    public void initView(final List<TIMConversation> conversationList) {
        this.conversationList.clear();

        for (TIMConversation item:conversationList){
            switch (item.getType()){
                case C2C:

            }
        }
    }


    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null){
            adapter.notifyDataSetChanged();
            return;
        }
        /*if (message.getConversation().getType() == TIMConversationType.System){
            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }*/

        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()){
            Conversation c = iterator.next();
            if (conversation.equals(c)){
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        Collections.sort(conversationList);
        refresh();
    }


    @Override
    public void updateFriendshipMessage() {

    }

    @Override
    public void removeConversation(String identify) {

    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {

    }



    /**
     * 刷新
     */
    @Override
    public void refresh() {
        Collections.sort(conversationList);


        String uids = "";
        for(Conversation conversation : conversationList){
            uids += conversation.getIdentify() + ",";
        }

        mPrivateChatMgr.requestConversationClass(UserInfoMgr.getInstance().getUid(), uids, mType, new PrivateChatMgr.OnConversationCallback() {
            @Override
            public void onSuccess(List<ConversationUserJson> conversationUserJsonList) {

                for(int i = 0; i < conversationUserJsonList.size(); i ++){

                    for(int j = 0; j < conversationList.size(); j ++){
                        //判断是不是当前用户的会话
                        if(StringUtils.toInt(conversationList.get(j).getIdentify()) == StringUtils.toInt(conversationUserJsonList.get(i).id)){

                            conversationUserJsonList.get(i).conversation = conversationList.get(j);
                            conversationUserJsonList.get(i).conversation.setAvatarUrl(conversationUserJsonList.get(i).avatar_thumb);
                            conversationUserJsonList.get(i).conversation.setName(conversationUserJsonList.get(i).user_nicename);
                            break;
                        }
                    }
                }
                mConversationUser.clear();

                mConversationUser.addAll(conversationUserJsonList);

                adapter.notifyDataSetChanged();
               /* if (getActivity() instanceof  HomeActivity)
                    ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);*/
            }

            @Override
            public void onFail() {

            }
        });


    }

    /**
    * 获取好友关系链管理系统最后一条消息的回调
    *
    * @param message 最后一条消息
    * @param unreadCount 未读数
    *//*
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
        if (friendshipConversation == null){
            friendshipConversation = new FriendshipConversation(message);
            conversationList.add(friendshipConversation);
        }else{
            friendshipConversation.setLastMessage(message);
        }
        friendshipConversation.setUnreadCount(unreadCount);
        Collections.sort(conversationList);
        refresh();
    }

    *//**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     *//*
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }*/
}
