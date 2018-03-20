package com.kustomer.kustomersdk.Views;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Size;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Helpers.KUSImage;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;


import java.net.URL;
import java.util.Locale;

import butterknife.ButterKnife;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Junaid on 3/19/2018.
 */

public class KUSNotificationWindow {

    //region Properties
    private static final int FONT_SIZE = 12;
    private static final int IMAGE_SIZE_IN_DP = 40;
    private static final int NOTIFICATION_ID = 123;
    private static final int DISMISS_DURATION_MILLISECOND = 4000;
    private static final String NOTIFICATION_CHANNEL_ID = "Default";

    private static KUSNotificationWindow notificationWindow;

    private Context mContext;
    private KUSUserSession mUserSession;
    private KUSChatMessagesDataSource chatMessagesDataSource;
    private KUSChatSession chatSession;
    //endregion

    //region Initializer
    public static KUSNotificationWindow getSharedInstance(){
        if(notificationWindow == null){
            notificationWindow = new KUSNotificationWindow();
        }

        return notificationWindow;
    }
    //endregion

    //region Public Methods
    public void showNotification(KUSChatSession mChatSession, Context context, final boolean shouldAutoDismiss){
        mContext = context;
        chatSession = mChatSession;

        mUserSession = Kustomer.getSharedInstance().getUserSession();
        chatMessagesDataSource = mUserSession.chatMessageDataSourceForSessionId(chatSession.getId());
        KUSUserDataSource userDataSource = mUserSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId());

        KUSUser user = null;
        if(userDataSource != null) {
            user = (KUSUser) userDataSource.getObject();
            if (user == null && !userDataSource.isFetching()) {
                userDataSource.fetch();
            }
        }

        KUSChatSettings chatSettings = (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject();
        if(mUserSession.getChatSettingsDataSource() != null && chatSettings == null && !this.mUserSession.getChatSettingsDataSource().isFetching()){
            mUserSession.getChatSettingsDataSource().fetch();
        }

        String name = "";
        if (user != null && user.getDisplayName() != null)
            name = user.getDisplayName();
        else if (chatSettings != null && chatSettings.getTeamName() != null)
            name = chatSettings.getTeamName();
        else if (mUserSession.getOrganizationName() != null)
            name = mUserSession.getOrganizationName();

        final Bitmap placeHolderImage = KUSImage.defaultAvatarBitmapForName(mContext,
                new Size((int) KUSUtils.dipToPixels(mContext,IMAGE_SIZE_IN_DP),
                        (int)KUSUtils.dipToPixels(mContext,IMAGE_SIZE_IN_DP)),
                name,
                0,
                FONT_SIZE);


        if(chatSettings != null) {
            URL iconURL = user != null && user.getAvatarURL() != null ? user.getAvatarURL() : chatSettings.getTeamIconURL();

            if (iconURL != null)
                try {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(iconURL.toString())
                            .apply(RequestOptions.circleCropTransform())
                            .apply(RequestOptions.noAnimation())
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    displayNotification(placeHolderImage,shouldAutoDismiss);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    displayNotification(resource,shouldAutoDismiss);
                                    return false;
                                }
                            })
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                }
                            });

                } catch (IllegalArgumentException ignore) {
                }
        }

    }
    //endregion

    //region Private Methods
    private void createNotificationChannelForOreoAndAbove(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "Message Notification";
            String description = "Shows messages received from support";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                    NOTIFICATION_SERVICE);

            if(notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }
    }

    private String getResponderName(){
        KUSUserDataSource userDataSource = mUserSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId());
        KUSUser firstOtherUser = userDataSource != null ? (KUSUser) userDataSource.getObject() : null;

        String responderName = firstOtherUser != null ? firstOtherUser.getDisplayName() : null ;

        if (responderName == null || responderName.length() == 0) {
            KUSChatSettings chatSettings = (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject();
            responderName = chatSettings != null && chatSettings.getTeamName().length() > 0 ?
                    chatSettings.getTeamName() : mUserSession.getOrganizationName();
        }

        return responderName;
    }

    private String getSubtitleText(){
        //Subtitle text (from last message, or preview text)
        KUSChatMessage latestTextMessage = null;
        for(KUSModel model : chatMessagesDataSource.getList()){
            KUSChatMessage message = (KUSChatMessage) model;
            if(message.getType() == KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT){
                latestTextMessage = message;
                break;
            }
        }

        String subtitleText = null;
        if (latestTextMessage != null) {
            subtitleText = latestTextMessage.getBody() != null ?
                    latestTextMessage.getBody() : chatSession.getPreview();
        }

        return subtitleText;
    }

    private void displayNotification(Bitmap bitmap, boolean shouldAutoDismiss){
        createNotificationChannelForOreoAndAbove();

        //Title text (from last responder, chat settings or organization name)
        String responderName = getResponderName();

        //Subtitle text (from last message, or preview text)
        String subtitleText = getSubtitleText();

        // Create an explicit intent for an Activity in your app
        PendingIntent pendingIntent = Kustomer.getSharedInstance().getUserSession()
                .getDelegateProxy().getPendingIntent(mContext);

        //Create Sound Uri
        Uri soundUri = Uri.parse(String.format(Locale.getDefault(),
                "android.resource://%s/%d",mContext.getPackageName(),R.raw.message_received));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(String.format(mContext.getString(R.string.chat_with)+" %s",responderName))
                .setContentText(subtitleText)
                .setLargeIcon(bitmap)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[0])
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        if(shouldAutoDismiss) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            };
            handler.postDelayed(runnable, DISMISS_DURATION_MILLISECOND);
        }

    }
    //endregion
}
