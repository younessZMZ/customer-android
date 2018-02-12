package com.kustomer.kustomersdk.Utils;

/**
 * Created by Junaid on 1/23/2018.
 */

public class KUSConstants {

    public interface MockedData {
        String PUSHER_API_KEY = "55af7c7423b80da74f5d";

        String CHAT_SESSION_OID = "5a62e560f3fbb800014f7a27";
        String CHAT_SESSION_ORG_ID = "5a5f6ca3b573fd0001af73dd";
        String CHAT_SESSION_CUSTOMER_ID = "5a62e55cf3fbb800014f7a11";
        String CHAT_SESSION_PREVIEW = "Yt";
        String CHAT_SESSION_TRACKING_ID = "5a62e559a64a1d0010b2ced5";
        String CHAT_SESSION_SESSION_ID = "5a6083aff105640001c8f96c";

        String TRACKING_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhNjJlNTU5YTY0YTFkMDAxMGIyY2VkNSIsIm9yZyI6IjVhNWY2Y2EzYjU3M2ZkMDAwMWFmNzNkZCIsImV4cCI6MTUxOTAyMjY4MSwiYXVkIjoidXJuOmNvbnN1bWVyIiwiaXNzIjoidXJuOmFwaSJ9.qjTqfCcAFwk8OlK0rWbalcp1i667-vFLPcgudnIRcK4";
    }

    public interface URL{
        String HOST_NAME = "kustomerapp.com";
        String PUSHER_AUTH = "/c/v1/pusher/auth";
        String MESSAGES_LIST_ENDPOINT = "/c/v1/chat/sessions/%s/messages";
        String SEND_MESSAGE_ENDPOINT = "/c/v1/chat/messages";
        String CHAT_SESSION_LIST_ENDPOINT = "/c/v1/chat/sessions";
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
        String CHAT_SESSION_BUNDLE__KEY = "Chat_Session_bundle";
        String USER_SESSION_BUNDLE__KEY = "User_Session_bundle";
    }
}
