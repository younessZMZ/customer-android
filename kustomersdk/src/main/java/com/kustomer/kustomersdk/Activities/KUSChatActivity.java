package com.kustomer.kustomersdk.Activities;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Views.KUSToolbar;

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
    KUSUserDataSource userDataSource;
    String chatSessionId;
    MessageListAdapter adapter;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kuschat,R.id.toolbar_main,null,true);
        super.onCreate(savedInstanceState);

        initData();
        initViews();
        setupAdapter();
        setupToolbar();
    }

    @Override
    protected void onDestroy() {
        kusUserSession.getPushClient().setSupportScreenShown(false);
        super.onDestroy();
    }
    //endregion

    //region Initializer
    private void initData(){
        kusUserSession = Kustomer.getSharedInstance().getUserSession();
//      kusUserSession = (KUSUserSession) getIntent().getSerializableExtra(KUSConstants.BundleName.USER_SESSION_BUNDLE__KEY);
        kusChatSession = (KUSChatSession) getIntent().getSerializableExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE__KEY);

        chatSessionId = kusChatSession.getId();
        chatMessagesDataSource = kusUserSession.chatMessageDataSourceForSessionId(chatSessionId);

        kusUserSession.getPushClient().setSupportScreenShown(true);
        chatMessagesDataSource.addListener(this);

        progressDialog.show();
        chatMessagesDataSource.fetchLatest();
    }

    private void initViews(){
        fabSendMessage.setEnabled(false);
        fabSendMessage.setAlpha(0.5f);

        etTypeMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    fabSendMessage.setEnabled(true);
                    fabSendMessage.setAlpha(1.0f);
                }
                else {
                    fabSendMessage.setEnabled(false);
                    fabSendMessage.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupToolbar(){
        KUSToolbar kusToolbar = (KUSToolbar)toolbar;
        kusToolbar.initWithUserSession(kusUserSession);
        kusToolbar.setSessionId(chatSessionId);
        kusToolbar.setShowBackButton(true);
        kusToolbar.setShowDismissButton(true);
    }

    private void setupAdapter(){
        adapter = new MessageListAdapter(chatMessagesDataSource, kusUserSession);
        rvMessages.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        rvMessages.setLayoutManager(layoutManager);

        adapter.notifyDataSetChanged();
    }
    //endregion

    //region Listeners
    @OnClick(R2.id.fabSendMessage)
    void fabSendMessageClick(){
        String textToSend = etTypeMessage.getText().toString();
        if(!textToSend.trim().isEmpty())
            chatMessagesDataSource.sendMessageWithText(textToSend.trim(),null);
        else
            Toast.makeText(this, R.string.please_provide_a_message_body,Toast.LENGTH_SHORT).show();

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
                    //TODO: change
                    //userDataSource.fetch();
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
