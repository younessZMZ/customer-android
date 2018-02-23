package com.kustomer.kustomersdk.Utils;

/**
 * Created by Junaid on 1/23/2018.
 */

public class KUSConstants {

    public interface URL{
        String HOST_NAME = "kustomerapp.com";
        String PUSHER_AUTH = "/c/v1/pusher/auth";
        String MESSAGES_LIST_ENDPOINT = "/c/v1/chat/sessions/%s/messages";
        String SEND_MESSAGE_ENDPOINT = "/c/v1/chat/messages";
        String CHAT_SESSIONS_ENDPOINT = "/c/v1/chat/sessions";
        String CHAT_CONVERSATIONS_ENDPOINT = "/c/v1/chat/conversations";
        String TRACKING_TOKEN_ENDPOINT = "/c/v1/tracking/tokens";
        String CURRENT_TRACKING_TOKEN_ENDPOINT = "/c/v1/tracking/tokens/current";
        String IDENTITY_ENDPOINT = "/c/v1/identity";
        String SETTINGS_ENDPOINT = "/c/v1/chat/settings";
        String TEAMS_ENDPOINT = "/c/v1/chat/teams/%s";
        String FORMS_ENDPOINT = "/c/v1/chat/forms/%s";
        String FORMS_RESPONSES_ENDPOINT = "/c/v1/chat/forms/%s/responses";
        String CURRENT_CUSTOMER_ENDPOINT = "/c/v1/customers/current";
    }

    public interface Keys {
       String K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY = "x-kustomer-tracking-token";
       String K_KUSTOMER_ORG_ID_KEY = "org";
       String K_KUSTOMER_ORG_NAME_KEY = "orgName";
    }

    public interface PusherEventNames{
        String SEND_MESSAGE_EVENT = "kustomer.app.chat.message.send";
    }

    public interface BundleName{
        String CHAT_SESSION_BUNDLE_KEY = "Chat_Session_bundle";
        String CHAT_SCREEN_BACK_BUTTON_KEY = "Chat_back_button_bundle";
        String USER_SESSION_BUNDLE__KEY = "User_Session_bundle";
    }
}
