package com.kustomer.kustomersdk.API;

import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Helpers.KUSAudio;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSTrackingToken;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Views.KUSNotificationWindow;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPushClient implements Serializable, KUSObjectDataSourceListener, KUSPaginatedDataSourceListener {

    //region Properties
    private static long LAZY_POLLING_TIMER_INTERVAL = 30000;
    private static long Active_POLLING_TIMER_INTERVAL = 7500;
    private long currentPollingTimerInterval = 0;

    private Pusher pusherClient;
    private PresenceChannel pusherChannel;
    private HashMap<String, KUSChatSession> previousChatSessions;

    private KUSUserSession userSession;
    private boolean isSupportScreenShown = false;
    private Timer pollingTimer;
    //endregion

    //region LifeCycle
    public KUSPushClient(KUSUserSession userSession){
        this.userSession = userSession;

        userSession.getChatSessionsDataSource().addListener(this);
        userSession.getChatSettingsDataSource().addListener(this);
        userSession.getTrackingTokenDataSource().addListener(this);

        connectToChannelsIfNecessary();
    }

    @Override
    protected void finalize() throws Throwable {
        pusherClient.unsubscribe(getPusherChannelName());
        pusherClient.disconnect();
        super.finalize();
    }
    //endregion

    //region Private Methods
    private URL getPusherAuthURL(){
        return userSession.getRequestManager().urlForEndpoint(KUSConstants.URL.PUSHER_AUTH);
    }

    private String getPusherChannelName(){
        KUSTrackingToken trackingTokenObj = (KUSTrackingToken) userSession.getTrackingTokenDataSource().getObject();

        if(trackingTokenObj != null)
            return String.format("presence-external-%s-tracking-%s",userSession.getOrgId(),
                    trackingTokenObj.getTrackingId());
        return null;
    }

    private void connectToChannelsIfNecessary(){
        KUSChatSettings chatSettings = (KUSChatSettings) userSession.getChatSettingsDataSource().getObject();

        if(pusherClient == null && chatSettings != null) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY,
                    userSession.getTrackingTokenDataSource().getCurrentTrackingToken());
            headers.putAll(userSession.getRequestManager().genericHTTPHeaderValues);

            HttpAuthorizer authorizer = new HttpAuthorizer(getPusherAuthURL().toString());
            authorizer.setHeaders(headers);

            PusherOptions options = new PusherOptions().setEncrypted(true).setAuthorizer(authorizer);
            pusherClient = new Pusher(chatSettings.getPusherAccessKey(), options);
        }

        if(pusherClient != null && shouldBeConnectedToPusher()){
            pusherClient.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    if(change.getCurrentState() == ConnectionState.CONNECTED)
                        updatePollingTimer();
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    updatePollingTimer();
                }
            });

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    connectToChannelsIfNecessary();
                }
            };
            handler.postDelayed(runnable,LAZY_POLLING_TIMER_INTERVAL);

        }else{
            if(pusherClient != null)
             pusherClient.disconnect();
        }

        String pusherChannelName = getPusherChannelName();

        if(pusherClient!=null && pusherChannelName != null && pusherChannel == null) {
            try {
                pusherChannel = pusherClient.subscribePresence(pusherChannelName);
                pusherChannel.bind(KUSConstants.PusherEventNames.SEND_MESSAGE_EVENT, new PresenceChannelEventListener() {
                    @Override
                    public void onUsersInformationReceived(String channelName, Set<User> users) {

                    }

                    @Override
                    public void userSubscribed(String channelName, User user) {

                    }

                    @Override
                    public void userUnsubscribed(String channelName, User user) {

                    }

                    @Override
                    public void onAuthenticationFailure(String message, Exception e) {

                    }

                    @Override
                    public void onSubscriptionSucceeded(String channelName) {

                    }

                    @Override
                    public void onEvent(String channelName, String eventName, String data) {
                        JSONObject jsonObject = JsonHelper.stringToJson(data);

                        final List<KUSModel> chatMessages = JsonHelper.kusChatModelsFromJSON(
                                Kustomer.getContext(),JsonHelper.jsonObjectFromKeyPath(jsonObject, "data"));

                        final KUSChatMessage chatMessage = (KUSChatMessage) chatMessages.get(0);
                        final KUSChatMessagesDataSource messagesDataSource = userSession.chatMessageDataSourceForSessionId(chatMessage.getSessionId());

                        final boolean doesNotAlreadyContainMessage = messagesDataSource.findById(chatMessage.getId()) == null;
                        messagesDataSource.upsertNewMessages(chatMessages);

                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (doesNotAlreadyContainMessage)
                                    notifyForUpdatedChatSession(chatMessage.getSessionId());
                            }
                        };
                        handler.post(runnable);

                    }
                });
            }catch (IllegalArgumentException ignore){}
        }

        updatePollingTimer();
    }

    private void updatePollingTimer(){
        if(shouldBeConnectedToPusher()){
            if(pusherClient != null && pusherClient.getConnection().getState() == ConnectionState.CONNECTED){
                //Stop Polling
                if(pollingTimer != null) {
                    pollingTimer.cancel();
                    pollingTimer = null;
                }
            }else{
                // We are not yet connected to pusher, setup an active polling pollingTimer
                // (in the event that connecting to pusher fails)
                if(pollingTimer == null || currentPollingTimerInterval != Active_POLLING_TIMER_INTERVAL){
                    if(pollingTimer != null)
                        pollingTimer.cancel();

                    startTimer(Active_POLLING_TIMER_INTERVAL);
                }
            }
        }else {
            // Make sure we're polling lazily
            if(pollingTimer == null || currentPollingTimerInterval != LAZY_POLLING_TIMER_INTERVAL){
                if(pollingTimer != null)
                    pollingTimer.cancel();

                startTimer(LAZY_POLLING_TIMER_INTERVAL);

                // Tick immediately
                onPollTick();
            }
        }
    }

    private void startTimer(long time) {
        try {
            currentPollingTimerInterval = time;
            final Handler handler = new Handler();
            pollingTimer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            onPollTick();
                        }
                    });
                }
            };
            pollingTimer.schedule(doAsynchronousTask, 0, time);
        }catch (Exception ignore){}
    }

    private void onPollTick(){
        userSession.getChatSessionsDataSource().fetchLatest();
    }

    private void notifyForUpdatedChatSession(String sessionId){

        if(isSupportScreenShown()) {
            KUSAudio.playMessageReceivedSound();
        }
        else{
            KUSChatMessagesDataSource chatMessagesDataSource = userSession.chatMessageDataSourceForSessionId(sessionId);
            KUSChatMessage latestMessage = chatMessagesDataSource.getLatestMessage();
            KUSChatSession chatSession = (KUSChatSession) userSession.getChatSessionsDataSource().findById(sessionId);
            if(chatSession == null){
                try {
                    chatSession = KUSChatSession.tempSessionFromChatMessage(latestMessage);
                } catch (KUSInvalidJsonException e) {
                    e.printStackTrace();
                }
                userSession.getChatSessionsDataSource().fetchLatest();
            }

            if( userSession.getDelegateProxy().shouldDisplayInAppNotification() && chatSession != null){
                boolean shouldAutoDismiss = latestMessage.getCampaignId() == null
                        || latestMessage.getCampaignId().length() == 0;

                //Sound is played by the notification itself
                KUSNotificationWindow.getSharedInstance().showNotification(chatSession,Kustomer.getContext(),shouldAutoDismiss);
            }
        }
    }

    private boolean shouldBeConnectedToPusher(){
        if(isSupportScreenShown())
            return true;

        Date lastMessageAt = userSession.getChatSessionsDataSource().getLastMessageAt();
        return lastMessageAt!=null && Calendar.getInstance().getTimeInMillis() - lastMessageAt.getTime()  > LAZY_POLLING_TIMER_INTERVAL;
    }
    //endregion

    //region Accessors

    public boolean isSupportScreenShown() {
        return isSupportScreenShown;
    }

    public void setSupportScreenShown(boolean supportScreenShown) {
        isSupportScreenShown = supportScreenShown;
        connectToChannelsIfNecessary();
    }
    //endregion

    //region Callbacks
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        if(!userSession.getChatSessionsDataSource().isFetched())
            userSession.getChatSessionsDataSource().fetchLatest();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connectToChannelsIfNecessary();
            }
        };
        handler.post(runnable);

    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {

    }

    private void updatePreviousChatSessions(){
        previousChatSessions = new HashMap<>();
        for(KUSModel model : userSession.getChatSessionsDataSource().getList()){
            KUSChatSession chatSession = (KUSChatSession) model;
            previousChatSessions.put(chatSession.getId(),chatSession);
        }
    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        updatePreviousChatSessions();
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
                if(dataSource == userSession.getChatSessionsDataSource())
                    connectToChannelsIfNecessary();

                // Only consider new messages here if we're actively polling
                if(pollingTimer == null) {
                    //But update the state of previousChatSessions
                    updatePreviousChatSessions();
                    return;
                }

                String updatedSessionId = null;
                List<KUSModel> newChatSessions = userSession.getChatSessionsDataSource().getList();
                for(KUSModel model : newChatSessions){
                    KUSChatSession chatSession = (KUSChatSession) model;

                    KUSChatSession previousChatSession = null;

                    if(previousChatSessions != null)
                        previousChatSession = previousChatSessions.get(chatSession.getId());

                    KUSChatMessagesDataSource messagesDataSource = userSession.chatMessageDataSourceForSessionId(chatSession.getId());
                    if(previousChatSession!=null){

                        try {
                            KUSChatMessage latestChatMessage = (KUSChatMessage) messagesDataSource.getList().get(0);
                            boolean isUpdatedSession = chatSession.getLastMessageAt().after(previousChatSession.getLastMessageAt());
                            Date sessionLastSeenAt = userSession.getChatSessionsDataSource().lastSeenAtForSessionId(chatSession.getId());
                            boolean lastSeenBeforeMessage = chatSession.getLastMessageAt().after(sessionLastSeenAt);
                            boolean lastMessageAtNewerThanLocalLastMessage = latestChatMessage == null
                                    || chatSession.getLastMessageAt().after(latestChatMessage.getCreatedAt());

                            if (isUpdatedSession && lastSeenBeforeMessage && lastMessageAtNewerThanLocalLastMessage) {
                                updatedSessionId = chatSession.getId();
                                messagesDataSource.fetchLatest();
                            }
                        }catch (Exception ignore){}

                    }else if (previousChatSessions != null){
                        updatedSessionId = chatSession.getId();
                        messagesDataSource.fetchLatest();
                    }
                }

                updatePreviousChatSessions();

                if(updatedSessionId != null){
                    notifyForUpdatedChatSession(updatedSessionId);
                    updatedSessionId = null;
                }
            }
        };
        handler.post(runnable);

    }
    //endregion
}
