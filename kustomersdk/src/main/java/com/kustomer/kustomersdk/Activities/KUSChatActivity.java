package com.kustomer.kustomersdk.Activities;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import com.kustomer.kustomersdk.API.KUSUserSession;
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
        chatMessagesDataSource.fetchLatest();
    }
    //endregion

    //region Initializer
    private void setupAdapter(){

    }
    //endregion

    //region Click Listener
    @OnClick(R2.id.fabSendMessage)
    void fabSendMessageClick(){
        Toast.makeText(this,"Fab Clicked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {

    }
    //endregion

}
