package com.kustomer.kustomersdk.API;

import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.Helpers.KUSAudio;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPushClient implements Serializable {

    //region Properties
    private Pusher pusherClient;
    private PresenceChannel pusherChannel;

    private KUSUserSession userSession;
    private boolean isSupportScreenShown = false;
    //endregion

    //region LifeCycle
    public KUSPushClient(KUSUserSession userSession){
        this.userSession = userSession;

        //TODO: Incomplete

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
        //TODO: Incomplete
        return String.format("presence-external-%s-tracking-%s",userSession.getOrgId(),
                userSession.getTrackingTokenDataSource().getCurrentTrackingId());
    }

    private void connectToChannelsIfNecessary(){

        //TODO: Needs to be enhanced

        if(pusherClient == null) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY,
                    userSession.getTrackingTokenDataSource().getCurrentTrackingToken());
            headers.putAll(userSession.getRequestManager().genericHTTPHeaderValues);

            HttpAuthorizer authorizer = new HttpAuthorizer(getPusherAuthURL().toString());
            authorizer.setHeaders(headers);

            PusherOptions options = new PusherOptions().setEncrypted(true).setAuthorizer(authorizer);
            pusherClient = new Pusher(KUSConstants.MockedData.PUSHER_API_KEY, options);
        }

        if(pusherClient != null && shouldBeConnectedToPusher()){
            pusherClient.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {

                    if(change.getCurrentState() == ConnectionState.CONNECTED){
                        String pusherChannelName = getPusherChannelName();
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

                                List<KUSModel> chatMessages = JsonHelper.kusChatModelsFromJSON(JsonHelper.jsonObjectFromKeyPath(jsonObject,"data"));

                                KUSChatMessage chatMessage = (KUSChatMessage) chatMessages.get(0);
                                KUSChatMessagesDataSource messagesDataSource = userSession.chatMessageDataSourceForSessionId(chatMessage.getSessionId());

                                boolean doesNotAlreadyContainMessage = messagesDataSource.findById(chatMessage.getOrgId()) == null;
                                messagesDataSource.upsertAll(chatMessages);

                                if(doesNotAlreadyContainMessage)
                                    notifyForUpdatedChatSession(chatMessage.getSessionId());
                            }
                        });
                    }
                }

                @Override
                public void onError(String message, String code, Exception e) {

                }
            });
        }else{
            pusherClient.disconnect();
        }

    }

    private void notifyForUpdatedChatSession(String sessionId){
        //TODO: Incomplete
        KUSAudio.playMessageReceivedSound();
    }

    private boolean shouldBeConnectedToPusher(){
        //TODO: Enhance
        if(isSupportScreenShown())
            return true;
        else
            return false;
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
}
