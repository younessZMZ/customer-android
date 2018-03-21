package com.kustomer.kustomersdk;

import com.kustomer.kustomersdk.API.KUSUserSession;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_ID;
import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_NAME;
import static org.junit.Assert.assertNotNull;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSUserSessionTests {


    @Test
    public void testValidAPIKeySuccess() {
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        assertNotNull(userSession.getChatSessionsDataSource());
        assertNotNull(userSession.getChatSettingsDataSource());
        assertNotNull(userSession.getTrackingTokenDataSource());
        assertNotNull(userSession.getRequestManager());
        assertNotNull(userSession.getPushClient());
        assertNotNull(userSession.getDelegateProxy());
    }


    @Test
    public void test100UserSessionsPerformance(){
        for(int i = 0; i<100; i++){
            KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        }
    }

}