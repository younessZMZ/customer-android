package com.kustomer.kustomersdk.Utils;

/**
 * Created by Junaid on 1/23/2018.
 */

public class KUSConstants {

    public interface Pattern{
        String URL_PATTERN = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    }

    public interface URL{
        String HOST_NAME = "kustomerapp.com";
        String PUSHER_AUTH = "/c/v1/pusher/auth";
        String MESSAGES_LIST_ENDPOINT = "/c/v1/chat/sessions/%s/messages";
        String SEND_MESSAGE_ENDPOINT = "/c/v1/chat/messages";
        String CHAT_SESSIONS_ENDPOINT = "/c/v1/chat/sessions";
        String CONVERSATIONS_ENDPOINT = "/c/v1/conversations";
        String TRACKING_TOKEN_ENDPOINT = "/c/v1/tracking/tokens";
        String CURRENT_TRACKING_TOKEN_ENDPOINT = "/c/v1/tracking/tokens/current";
        String IDENTITY_ENDPOINT = "/c/v1/identity";
        String SETTINGS_ENDPOINT = "/c/v1/chat/settings";
        String TEAMS_ENDPOINT = "/c/v1/chat/teams/%s";
        String FORMS_ENDPOINT = "/c/v1/chat/forms/%s";
        String FORMS_RESPONSES_ENDPOINT = "/c/v1/chat/forms/%s/responses";
        String CURRENT_CUSTOMER_ENDPOINT = "/c/v1/customers/current";
        String ATTACHMENT_ENDPOINT = "https://%s.api.%s/c/v1/chat/messages/%s/attachments/%s?redirect=true";
        String CHAT_ATTACHMENT_ENDPOINT = "/c/v1/chat/attachments";
        String CLIENT_ACTIVITY_ENDPOINT = "/c/v1/client-activity";
    }

    public interface Keys {
       String K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY = "x-kustomer-tracking-token";
       String K_KUSTOMER_ORG_ID_KEY = "org";
       String K_KUSTOMER_ORG_NAME_KEY = "orgName";
        String K_KUSTOMER_URL_KEY= "url";
    }

    public interface PusherEventNames{
        String SEND_MESSAGE_EVENT = "kustomer.app.chat.message.send";
    }

    public interface BundleName{
        String CHAT_SESSION_BUNDLE_KEY = "Chat_Session_bundle";
        String CHAT_SCREEN_BACK_BUTTON_KEY = "Chat_back_button_bundle";
        String NOTIFICATION_ID_BUNDLE_KEY = "Notification_ID";
    }
}
