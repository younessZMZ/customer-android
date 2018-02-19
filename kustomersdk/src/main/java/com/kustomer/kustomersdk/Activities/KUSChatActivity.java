package com.kustomer.kustomersdk.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Views.KUSToolbar;

import butterknife.BindView;
import butterknife.OnClick;

public class KUSChatActivity extends BaseActivity implements KUSChatMessagesDataSourceListener, TextWatcher, KUSToolbar.OnToolbarItemClickListener {

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
    KUSToolbar kusToolbar;
    boolean shouldShowBackButton = true;
    boolean backPressed = false;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kuschat,R.id.toolbar_main,null,false);
        super.onCreate(savedInstanceState);

        initData();
        initViews();
        setupAdapter();
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(kusUserSession != null && chatSessionId != null)
            kusUserSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId,null);
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(kusUserSession != null && chatSessionId != null)
            kusUserSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(shouldShowBackButton) {
            backPressed = true;
            super.onBackPressed();
        }else{
            clearAllLibraryActivities();
        }
    }

    @Override
    public void finish() {
        super.finish();

        if(backPressed)
            overridePendingTransition(R.anim.stay, R.anim.kus_slide_right);
        else
            overridePendingTransition(R.anim.stay, R.anim.kus_slide_down);
    }

    //endregion

    //region Initializer
    private void initData(){
        kusUserSession = Kustomer.getSharedInstance().getUserSession();
        kusChatSession = (KUSChatSession) getIntent().getSerializableExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE__KEY);
        shouldShowBackButton = getIntent().getBooleanExtra(KUSConstants.BundleName.CHAT_SCREEN_BACK_BUTTON_KEY,true);

        if(kusChatSession != null) {
            chatSessionId = kusChatSession.getId();
            chatMessagesDataSource = kusUserSession.chatMessageDataSourceForSessionId(chatSessionId);
        }else{
            chatMessagesDataSource = new KUSChatMessagesDataSource(kusUserSession,true);
        }

        chatMessagesDataSource.addListener(this);
        chatMessagesDataSource.fetchLatest();
        if(!chatMessagesDataSource.isFetched()){
            progressDialog.show();
        }
    }

    private void initViews(){
        fabSendMessage.setEnabled(false);
        fabSendMessage.setAlpha(0.5f);

        etTypeMessage.addTextChangedListener(this);
    }

    private void setupToolbar(){
        kusToolbar = (KUSToolbar)toolbar;
        kusToolbar.initWithUserSession(kusUserSession);
        kusToolbar.setSessionId(chatSessionId);
        kusToolbar.setShowLabel(true);
        kusToolbar.setListener(this);
        kusToolbar.setShowBackButton(shouldShowBackButton);
        kusToolbar.setShowDismissButton(true);
    }

    private void setupAdapter(){
        adapter = new MessageListAdapter(chatMessagesDataSource, kusUserSession, chatMessagesDataSource);
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
        progressDialog.hide();
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {
        if(dataSource == chatMessagesDataSource){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {
        chatSessionId = sessionId;
        //TODO: incomplete
        kusToolbar.setSessionId(chatSessionId);
        shouldShowBackButton = true;
        kusToolbar.setShowBackButton(true);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.toString().trim().length() > 0) {
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

    @Override
    public void onToolbarBackPressed() {
        onBackPressed();
    }

    @Override
    public void onToolbarClosePressed() {
        clearAllLibraryActivities();
    }
    //endregion
}
