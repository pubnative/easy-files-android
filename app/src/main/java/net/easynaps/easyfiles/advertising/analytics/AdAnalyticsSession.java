package net.easynaps.easyfiles.advertising.analytics;

import android.content.Context;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;

public class AdAnalyticsSession {
    private final AdAnalytics mAnalytics;
    private long mInitialTime = 0;
    private final AdType mType;
    private final AdNetwork mNetwork;

    public AdAnalyticsSession(Context context, AdType type, AdNetwork network) {
        mAnalytics = AdAnalytics.getInstance(context);
        mType = type;
        mNetwork = network;
    }

    public void start() {
        this.mInitialTime = System.currentTimeMillis();
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_START_LOADING, mType, mNetwork, 0));
    }

    public void confirmLoaded() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_LOADED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmError() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_LOAD_ERROR, mType, mNetwork, getElapsedTime()));
    }

    public void confirmImpression() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_IMPRESSION, mType, mNetwork, getElapsedTime()));
    }

    public void confirmClick() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_CLICK, mType, mNetwork, getElapsedTime()));
    }

    public void confirmOpened() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_OPENED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmClosed() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_CLOSED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmLeftApplication() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_LEFT_APP, mType, mNetwork, getElapsedTime()));
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - mInitialTime;
    }
}
