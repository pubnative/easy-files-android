package net.easynaps.easyfiles.advertising.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AdAnalytics {
    private static final String KEY_AD_TYPE = "ad_type";
    private static final String KEY_SDK_NAME = "sdk_name";
    private static final String KEY_TIME_ELAPSED = "time_elapsed";
    private static final String KEY_CATEGORY = "category";

    private static final String CATEGORY_ADVERTISING = "advertising";


    private static volatile AdAnalytics instance;
    private static final Object mutex = new Object();

    private final FirebaseAnalytics mAnalytics;

    private AdAnalytics(Context context) {
        mAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static AdAnalytics getInstance(Context context) {
        AdAnalytics result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new AdAnalytics(context);
                }
            }
        }
        return result;
    }

    public void sendEvent(AdEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY, CATEGORY_ADVERTISING);
        bundle.putString(KEY_AD_TYPE, event.getAdType());
        bundle.putString(KEY_SDK_NAME, event.getSdkName());

        if (event.getElapsedMilliseconds() > 0) {
            bundle.putLong(KEY_TIME_ELAPSED, event.getElapsedMilliseconds());
        }

        mAnalytics.logEvent(event.getName(), bundle);
    }
}
