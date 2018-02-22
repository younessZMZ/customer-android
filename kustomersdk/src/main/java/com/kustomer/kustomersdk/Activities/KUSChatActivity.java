package com.kustomer.kustomersdk.Activities;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSEmailInputViewListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.kustomer.kustomersdk.Views.KUSEmailInputView;
import com.kustomer.kustomersdk.Views.KUSToolbar;

import butterknife.BindView;
import butterknife.OnClick;

public class KUSChatActivity extends BaseActivity implements KUSChatMessagesDataSourceListener, TextWatcher, KUSToolbar.OnToolbarItemClickListener, TextView.OnEditorActionListener, KUSEmailInputViewListener {

    //region Properties
    @BindView(R2.id.etTypeMessage) EditText etTypeMessage;
    @BindView(R2.id.rvMessages) RecyclerView rvMessages;
    @BindView(R2.id.fabSendMessage) FloatingActionButton fabSendMessage;
    @BindView(R2.id.emailInputView)
    KUSEmailInputView emailInputView;

    KUSChatSession kusChatSession;
    KUSUserSession userSession;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(userSession != null && chatSessionId != null)
            userSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId,null);
    }


    @Override
    protected void onPause() {
        if(userSession != null && chatSessionId != null)
            userSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId,null);

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if(chatMessagesDataSource != null)
            chatMessagesDataSource.removeListener(this);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        checkShouldShowEmailInput();

    }

    //endregion

    //region Initializer
    private void initData(){
        userSession = Kustomer.getSharedInstance().getUserSession();
        kusChatSession = (KUSChatSession) getIntent().getSerializableExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE__KEY);
        shouldShowBackButton = getIntent().getBooleanExtra(KUSConstants.BundleName.CHAT_SCREEN_BACK_BUTTON_KEY,true);

        if(kusChatSession != null) {
            chatSessionId = kusChatSession.getId();
            chatMessagesDataSource = userSession.chatMessageDataSourceForSessionId(chatSessionId);
        }else{
            chatMessagesDataSource = new KUSChatMessagesDataSource(userSession,true);
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
        etTypeMessage.setOnEditorActionListener(this);
        etTypeMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etTypeMessage.setRawInputType(InputType.TYPE_CLASS_TEXT);

        setupToolbar();
    }

    private void setupToolbar(){
        kusToolbar = (KUSToolbar)toolbar;
        kusToolbar.initWithUserSession(userSession);
        kusToolbar.setSessionId(chatSessionId);
        kusToolbar.setShowLabel(true);
        kusToolbar.setListener(this);
        kusToolbar.setShowBackButton(shouldShowBackButton);
        kusToolbar.setShowDismissButton(true);
        kusToolbar.setExtraLargeSize(chatMessagesDataSource.getSize() == 0);

        checkShouldShowEmailInput();
    }

    private void checkShouldShowEmailInput(){
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if(isLandscape && KUSUtils.isPhone(this)) {
            emailInputView.setVisibility(View.GONE);
        }else{
            boolean shouldShowEmailInput = userSession.isShouldCaptureEmail() && chatSessionId != null;
            if (shouldShowEmailInput) {
                emailInputView.setVisibility(View.VISIBLE);
                emailInputView.setListener(this);
            } else {
                emailInputView.setVisibility(View.GONE);
            }
        }
    }

    private void setupAdapter(){
        adapter = new MessageListAdapter(chatMessagesDataSource, userSession, chatMessagesDataSource);
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
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(final KUSPaginatedDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(dataSource == chatMessagesDataSource){

                    if(dataSource.getSize() > 1)
                        initViews();

                    adapter.notifyDataSetChanged();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onCreateSessionId(final KUSChatMessagesDataSource source, final String sessionId) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                chatSessionId = sessionId;
                kusToolbar.setSessionId(chatSessionId);
                shouldShowBackButton = true;
                kusToolbar.setShowBackButton(true);
                initViews();
                checkShouldShowEmailInput();
            }
        };
        handler.post(runnable);
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

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == EditorInfo.IME_ACTION_SEND){
           fabSendMessageClick();
        }
        return false;
    }

    @Override
    public void onSubmitEmail(String email) {
        userSession.submitEmail(email);
        checkShouldShowEmailInput();
    }
    //endregion
}
