package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;

import java.util.Locale;

public class KUSLocalization {
    //region properties

    private static KUSLocalization localizationManager;
    private Locale sLocale;

    //endregion

    //region LifeCycle
    private KUSLocalization(){}

    public static KUSLocalization getSharedInstance() {
        if (localizationManager == null) {
            localizationManager = new KUSLocalization();
        }
        return localizationManager;
    }
    //endregion

    //region Public Methods

    public void setLocale(Locale locale) {
        Locale locales[]= Locale.getAvailableLocales();
        for(Locale loc : locales) {
            if(loc.equals(locale)) {
                sLocale = locale;
                return;
            }
        }


    }

    public String localizedString(Context mContext, String key) {
        String packageName = mContext.getPackageName();
        int resId = mContext.getResources().getIdentifier(key, "string", packageName);
        if (resId == 0)
            return key;
        else {
            return mContext.getString(resId);
        }
    }

    public void updateConfig(ContextThemeWrapper wrapper) {
        if (sLocale != null) {
            Locale.setDefault(sLocale);
            Configuration configuration = new Configuration();
            configuration.setLocale(sLocale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }
    public void  updateConfig(Context context){
        if (sLocale != null) {
            Locale.setDefault(sLocale);
            Configuration configuration = context.getResources().getConfiguration();
            configuration.setLocale(sLocale);
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
    }

    public boolean isLTR(){
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())== View.LAYOUT_DIRECTION_LTR;
    }

    //endregion
}
