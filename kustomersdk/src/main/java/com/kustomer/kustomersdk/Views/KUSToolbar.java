package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.ArrayList;

/**
 * Created by Junaid on 1/30/2018.
 */

public class KUSToolbar extends Toolbar implements KUSObjectDataSourceListener, KUSChatMessagesDataSourceListener {
    //region Properties
    private String sessionId;
    private boolean showLabel;
    private boolean showBackButton;
    private boolean showDismissButton;
    private boolean extraLargeSize;

    KUSUserSession userSession;
    KUSChatMessagesDataSource chatMessagesDataSource;
    KUSUserDataSource userDataSource;

    TextView tvName;
    TextView tvGreetingMessage;
    TextView tvToolbarUnreadCount;
    KUSMultipleAvatarsView kusMultipleAvatarsView;
    View ivBack;
    View ivClose;
    ViewGroup toolbarInnerLayout;
    OnToolbarItemClickListener listener;
    //endregion

    //region Initializer
    public KUSToolbar(Context context) {
        super(context);
    }

    public KUSToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        setListeners();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (userSession != null && userSession.getChatSettingsDataSource() != null)
            userSession.getChatSettingsDataSource().removeListener(this);

        if (userSession != null && userSession.getChatSessionsDataSource() != null)
            userSession.getChatSessionsDataSource().removeListener(this);

        if (userDataSource != null)
            userDataSource.removeListener(this);

        if (chatMessagesDataSource != null)
            chatMessagesDataSource.removeListener(this);
    }

    //endregion

    //region Public Methods
    public void initWithUserSession(KUSUserSession userSession) {
        this.userSession = userSession;
        kusMultipleAvatarsView.initWithUserSession(userSession);

        this.userSession.getChatSettingsDataSource().addListener(this);
        this.userSession.getChatSessionsDataSource().addListener(this);

        updateTextLabel();
        updateBackButtonBadge();

    }
    //endregion

    //region Private Methods
    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvGreetingMessage = findViewById(R.id.tvGreetingMessage);
        kusMultipleAvatarsView = findViewById(R.id.multipleAvatarViews);
        ivBack = findViewById(R.id.ivBack);
        ivClose = findViewById(R.id.ivClose);
        tvToolbarUnreadCount = findViewById(R.id.tvToolbarUnreadCount);
        toolbarInnerLayout = findViewById(R.id.toolbarInnerLayout);

        if (showLabel) {
            tvName.setVisibility(VISIBLE);
            tvGreetingMessage.setVisibility(VISIBLE);
        } else {
            tvName.setVisibility(GONE);
            tvGreetingMessage.setVisibility(GONE);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams avatarlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        avatarlp.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        vlp.gravity = Gravity.CENTER;

        if (extraLargeSize) {
            tvName.setTextSize(15f);
            tvGreetingMessage.setTextSize(13f);

            lp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 40),
                    0, (int) KUSUtils.dipToPixels(getContext(), 40));

            avatarlp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 4),
                    0, (int) KUSUtils.dipToPixels(getContext(), 4));

            vlp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 4),
                    0, (int) KUSUtils.dipToPixels(getContext(), 4));


            kusMultipleAvatarsView.setLayoutParams(avatarlp);
            tvName.setLayoutParams(vlp);
            tvGreetingMessage.setLayoutParams(vlp);
            toolbarInnerLayout.setLayoutParams(lp);

        } else {
            tvName.setTextSize(13f);
            tvGreetingMessage.setTextSize(11f);

            lp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 10),
                    0, (int) KUSUtils.dipToPixels(getContext(), 5));

            avatarlp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 2),
                    0, (int) KUSUtils.dipToPixels(getContext(), 2));

            vlp.setMargins(0, 0,
                    0, 0);

            kusMultipleAvatarsView.setLayoutParams(avatarlp);
            tvName.setLayoutParams(vlp);
            tvGreetingMessage.setLayoutParams(vlp);
            toolbarInnerLayout.setLayoutParams(lp);
        }

    }

    private void setListeners() {
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onToolbarBackPressed();
            }
        });

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onToolbarClosePressed();
            }
        });
    }

    private void updateTextLabel() {
        if (userDataSource != null) {
            userDataSource.removeListener(this);
        }

        if (chatMessagesDataSource != null)
            userDataSource = userSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId());

        KUSUser firstOtherUser = null;
        if (userDataSource != null) {
            userDataSource.addListener(this);
            firstOtherUser = (KUSUser) userDataSource.getObject();
        }

        String responderName = "";
        if (firstOtherUser != null) {
            responderName = firstOtherUser.getDisplayName();
        }

        KUSChatSettings chatSettings = (KUSChatSettings) userSession.getChatSettingsDataSource().getObject();

        if (chatSettings != null) {
            if (responderName == null || responderName.length() == 0) {
                if (chatSettings.getTeamName() != null && chatSettings.getTeamName().length() != 0)
                    responderName = chatSettings.getTeamName();
                else
                    responderName = userSession.getOrganizationName();
            }

            if (chatSettings.isVolumeControlEnabled()) {
                if (chatSettings.isUseDynamicWaitMessage())
                    tvGreetingMessage.setText(chatSettings.getWaitMessage());
                else
                    tvGreetingMessage.setText(chatSettings.getCustomWaitMessage());
            } else {
                tvGreetingMessage.setText(chatSettings.getGreeting());
            }
        }
        tvName.setText(responderName);
    }

    private void updateBackButtonBadge() {
        int unreadCount = userSession.getChatSessionsDataSource().totalUnreadCountExcludingSessionId(sessionId);
        if (unreadCount > 0) {
            tvToolbarUnreadCount.setText(String.valueOf(unreadCount));
            tvToolbarUnreadCount.setVisibility(VISIBLE);
        } else {
            tvToolbarUnreadCount.setVisibility(INVISIBLE);
        }
    }
    //endregion

    //region Accessors & Mutators
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (this.sessionId != null && this.sessionId.equals(sessionId))
            return;

        if (sessionId == null)
            return;

        this.sessionId = sessionId;

        if (chatMessagesDataSource != null)
            chatMessagesDataSource.removeListener(this);

        chatMessagesDataSource = userSession.getChatMessagesDataSources().get(sessionId);
        chatMessagesDataSource.addListener(this);

        kusMultipleAvatarsView.setUserIds((ArrayList<String>) chatMessagesDataSource.getOtherUserIds());

        updateTextLabel();
        updateBackButtonBadge();
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        initViews();
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;

        if (showBackButton)
            ivBack.setVisibility(VISIBLE);
        else
            ivBack.setVisibility(INVISIBLE);
    }

    public boolean isShowDismissButton() {
        return showDismissButton;
    }

    public void setShowDismissButton(boolean showDismissButton) {
        this.showDismissButton = showDismissButton;
    }

    public boolean isExtraLargeSize() {
        return extraLargeSize;
    }

    public void setExtraLargeSize(boolean extraLargeSize) {
        this.extraLargeSize = extraLargeSize;

        initViews();
    }

    public void setListener(OnToolbarItemClickListener listener) {
        this.listener = listener;
    }

    //endregion

    //region Listeners
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateTextLabel();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {

    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {

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
                if (dataSource == chatMessagesDataSource) {
                    kusMultipleAvatarsView.setUserIds((ArrayList<String>) chatMessagesDataSource.getOtherUserIds());
                    updateTextLabel();
                } else if (dataSource == userSession.getChatSessionsDataSource()) {
                    updateBackButtonBadge();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, final String mSessionId) {


        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (sessionId.equals(mSessionId))
                    return;

                sessionId = mSessionId;
                chatMessagesDataSource.removeListener(KUSToolbar.this);
                chatMessagesDataSource = userSession.chatMessageDataSourceForSessionId(sessionId);
                chatMessagesDataSource.addListener(KUSToolbar.this);
                kusMultipleAvatarsView.setUserIds(chatMessagesDataSource.getOtherUserIds());

                updateTextLabel();
                updateBackButtonBadge();

            }
        };
        handler.post(runnable);


    }
    //endregion

    //region Interface
    public interface OnToolbarItemClickListener {
        void onToolbarBackPressed();

        void onToolbarClosePressed();
    }
    //endregion


}
