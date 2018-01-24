package com.kustomer.kustomersdk.Activities;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import butterknife.BindView;
import butterknife.OnClick;

public class KUSChatActivity extends BaseActivity implements KUSChatMessagesDataSourceListener {

    //region Properties
    @BindView(R2.id.etTypeMessage) EditText etTypeMessage;
    @BindView(R2.id.rvMessages) RecyclerView rvMessages;
    @BindView(R2.id.fabSendMessage) FloatingActionButton fabSendMessage;

    KUSChatSession kusChatSession;
    KUSUserSession kusUserSession;
    KUSChatMessagesDataSource chatMessagesDataSource;
    String chatSessionId;
    MessageListAdapter adapter;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kuschat,R.id.toolbar_main,null,true);
        super.onCreate(savedInstanceState);

        kusUserSession = (KUSUserSession) getIntent().getSerializableExtra(KUSConstants.BundleName.USER_SESSION_BUNDLE__KEY);
        kusChatSession = (KUSChatSession) getIntent().getSerializableExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE__KEY);

        chatSessionId = kusChatSession.oid;
        chatMessagesDataSource = kusUserSession.chatMessageDataSourceForSessionId(chatSessionId);

        chatMessagesDataSource.addListener(this);

        progressDialog.show();
        chatMessagesDataSource.fetchLatest();

        setupAdapter();
    }
    //endregion

    //region Initializer
    private void setupAdapter(){
        adapter = new MessageListAdapter(chatMessagesDataSource);
        rvMessages.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        adapter.notifyDataSetChanged();
    }
    //endregion

    //region Click Listener
    @OnClick(R2.id.fabSendMessage)
    void fabSendMessageClick(){
        chatMessagesDataSource.sendMessageWithText(etTypeMessage.getText().toString(),null);
        etTypeMessage.setText("");
    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
            }
        };
        mainHandler.post(myRunnable);

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {

        if(dataSource == chatMessagesDataSource){
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {

    }
    //endregion

}
