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

    public void confirmInterstitialShow() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_INTERSTITIAL_SHOW, mType, mNetwork, getElapsedTime()));
    }

    public void confirmInterstitialShown() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_INTERSTITIAL_SHOWN, mType, mNetwork, getElapsedTime()));
    }

    public void confirmInterstitialShowError() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_INTERSTITIAL_SHOW_ERROR, mType, mNetwork, getElapsedTime()));
    }

    public void confirmInterstitialDismissed() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_INTERSTITIAL_DISMISSED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmAudioStarted() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_AUDIO_STARTED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmAudioFinished() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_AUDIO_FINISHED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmVideoStarted() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_VIDEO_STARTED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmVideoIncomplete() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_VIDEO_INCOMPLETE, mType, mNetwork, getElapsedTime()));
    }

    public void confirmVideoFinished() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_VIDEO_FINISHED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmVideoError() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_VIDEO_ERROR, mType, mNetwork, getElapsedTime()));
    }

    public void confirmReward() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_REWARD, mType, mNetwork, getElapsedTime()));
    }

    public void confirmRewardRejected() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_REWARD_REJECTED, mType, mNetwork, getElapsedTime()));
    }

    public void confirmUserOverQuota() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_USER_OVER_QUOTA, mType, mNetwork, getElapsedTime()));
    }

    public void confirmDeclinedToViewAd() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_DECLINED_TO_VIEW_AD, mType, mNetwork, getElapsedTime()));
    }

    public void confirmValidationRequestFailed() {
        mAnalytics.sendEvent(new AdEvent(AdEvent.EVENT_VALIDATION_REQUEST_FAILED, mType, mNetwork, getElapsedTime()));
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - mInitialTime;
    }
}
