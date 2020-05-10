package com.qiji.live.xiaozhibo.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.chat.adapter.ChatAdapter;
import com.qiji.live.xiaozhibo.chat.interf.ChatView;
import com.qiji.live.xiaozhibo.chat.model.CustomMessage;
import com.qiji.live.xiaozhibo.chat.model.Message;
import com.qiji.live.xiaozhibo.chat.model.MessageFactory;
import com.qiji.live.xiaozhibo.chat.model.TextMessage;
import com.qiji.live.xiaozhibo.chat.presenter.ChatPresenter;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TalkActivity extends TCBaseActivity implements UIInterface, ChatView {

    @Bind(R.id.input_panel)
    ChatInput mChatInput;

    @Bind(R.id.chat_list)
    ListView mLvChat;

    private ChatAdapter adapter;

    private List<Message> messageList = new ArrayList<>();

    private ChatPresenter presenter;

    private String identify;

    private TIMConversationType type;

    private Handler handler = new Handler();

    private String titleStr = "测试";

    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);


        ButterKnife.bind(this);

        initView();
        initData();
    }

    public static void navToChat(Context context, String identify,String avatar,String name, TIMConversationType type){
        Intent intent = new Intent(context, TalkActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        intent.putExtra("avatar",avatar);
        intent.putExtra("name",name);
        context.startActivity(intent);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

        identify = getIntent().getStringExtra("identify");
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        avatar =  getIntent().getStringExtra("avatar");
        titleStr = getIntent().getStringExtra("name");

        TCActivityTitle title = (TCActivityTitle) findViewById(R.id.chat_title);
        title.setTitle(titleStr);

        presenter = new ChatPresenter(this, identify, type);

        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        mLvChat.setAdapter(adapter);
        mLvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mLvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mChatInput.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        mChatInput.setChatView(this);

        mLvChat.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });

        presenter.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if (mChatInput.getText().length() > 0){
            TextMessage message = new TextMessage(mChatInput.getText());
            presenter.saveDraft(message.getMessage());
        }else{
            presenter.saveDraft(null);
        }
//        RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            mMessage.setAvatar(avatar);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage){
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType){
                        case TYPING:
                            TCActivityTitle title = (TCActivityTitle) findViewById(R.id.chat_title);
                            title.setTitle(getString(R.string.R_string_chat_typing));
                            handler.removeCallbacks(resetTitle);
                            handler.postDelayed(resetTitle,3000);
                            break;
                        default:
                            break;
                    }
                }else{
                    if (messageList.size()==0){
                        mMessage.setHasTime(null);
                    }else{
                        mMessage.setHasTime(messageList.get(messageList.size()-1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    mLvChat.setSelection(adapter.getCount()-1);
                }

            }

        }

    }

    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i){
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1){
                mMessage.setHasTime(messages.get(i+1));
                messageList.add(0, mMessage);
            }else{
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
            mMessage.setAvatar(avatar);
        }
        adapter.notifyDataSetChanged();
        mLvChat.setSelection(newMsgNum);
    }

    @Override
    public void clearAllMessage() {

    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList){
            if (msg.getMessage().getMsgUniqueId() == id){
                switch (code){
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }

    }

    @Override
    public void sendImage() {

    }

    @Override
    public void sendPhoto() {

    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(mChatInput.getText());
        presenter.sendMessage(message.getMessage());
        mChatInput.setText("");
    }

    @Override
    public void sendFile() {

    }

    @Override
    public void startSendVoice() {

    }

    @Override
    public void endSendVoice() {

    }

    @Override
    public void sendVideo(String fileName) {

    }

    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C){
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    @Override
    public void showDraft(TIMMessageDraft draft) {

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            TCActivityTitle title = (TCActivityTitle) findViewById(R.id.chat_title);
            title.setTitle(titleStr);
        }
    };
}
