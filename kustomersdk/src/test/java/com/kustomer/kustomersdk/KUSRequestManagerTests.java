package com.kustomer.kustomersdk;

import android.app.PendingIntent;
import android.content.Context;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSDelegateProxy;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URL;

import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_API_KEY;
import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_NAME;
import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSRequestManagerTests {

    @Test
    public void testUserSessionCreatesDefaultRequestManager(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        assertNotNull(userSession.getRequestManager());
    }

    @Test
    public void testRequestManagerBaseUrl(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        KUSRequestManager requestManager = new KUSRequestManager(userSession);
        URL baseURL = requestManager.urlForEndpoint("");
        assertEquals(baseURL.toString(),"https://testOrgName.api.kustomerapp.com");
    }

    @Test
    public void testRequestManagerEndpointURLs(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        KUSRequestManager requestManager = new KUSRequestManager(userSession);
        assertEquals(requestManager.urlForEndpoint("/c/v1/customers/current").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/customers/current");
        assertEquals(requestManager.urlForEndpoint("/c/v1/identity").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/identity");
        assertEquals(requestManager.urlForEndpoint("/c/v1/pusher/auth").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/pusher/auth");
        assertEquals(requestManager.urlForEndpoint("/c/v1/chat/messages").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/messages");
        assertEquals(requestManager.urlForEndpoint("/c/v1/chat/sessions").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/sessions");
        assertEquals(requestManager.urlForEndpoint("/c/v1/chat/settings").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/settings");
        assertEquals(requestManager.urlForEndpoint("/c/v1/tracking/tokens/current").toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/tracking/tokens/current");
    }

    @Test
    public void test100RequestManagerInitPerformance(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        for(int i = 0; i<100; i++){
            KUSRequestManager requestManager = new KUSRequestManager(userSession);
        }
    }


}