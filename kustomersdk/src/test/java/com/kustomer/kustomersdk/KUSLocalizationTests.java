package com.kustomer.kustomersdk;


import android.content.Context;

import com.kustomer.kustomersdk.Helpers.KUSLocalization;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSLocalizationTests {

    Context mContext;

    @Before
    public void setUp(){
        mContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testRTL(){

        boolean systemIsLRT= KUSLocalization.getSharedInstance().isLTR();
        KUSLocalization.getSharedInstance().setLocale(null);
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(systemIsLRT, KUSLocalization.getSharedInstance().isLTR());

        KUSLocalization.getSharedInstance().setLocale( new Locale("en"));
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(true, KUSLocalization.getSharedInstance().isLTR());

        KUSLocalization.getSharedInstance().setLocale( new Locale("ar"));
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(false, KUSLocalization.getSharedInstance().isLTR());

    }

    @Test
    public void testLocale(){

        Locale expectedLocale = Locale.getDefault();
        KUSLocalization.getSharedInstance().setLocale(null);
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(expectedLocale, Locale.getDefault());

        expectedLocale =new Locale("en");
        KUSLocalization.getSharedInstance().setLocale(expectedLocale);
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(expectedLocale, Locale.getDefault());

        expectedLocale =new Locale("ar");
        KUSLocalization.getSharedInstance().setLocale(expectedLocale);
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(expectedLocale, Locale.getDefault());

    }


    @Test
    public void testLocalizedString(){

        KUSLocalization.getSharedInstance().setLocale(null);
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext, "attachment"), "Attachment");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"just_now"), "Just now");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"gallery"), "Gallery");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"cancel"), "Cancel");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"camera"), "Camera");

        KUSLocalization.getSharedInstance().setLocale(new Locale("en"));
        KUSLocalization.getSharedInstance().updateConfig(mContext);
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"attachment"), "Attachment");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"just_now"), "Just now");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"gallery"), "Gallery");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"cancel"), "Cancel");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"camera"), "Camera");

    }

    /* We have to use qualifiers in roboelectric as urdu is not supported directly in testing*/
    @Config(qualifiers="ur")
    @Test
    public void testLocalizedUrduString(){

        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"cancel"), "منسوخ کریں");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"attachment"), "منسلکہ");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"just_now"), "ابھی ابھی");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"camera"), "کیمرے");
        assertEquals(KUSLocalization.getSharedInstance().localizedString(mContext,"gallery"), "نگارخانہ");

    }

}
