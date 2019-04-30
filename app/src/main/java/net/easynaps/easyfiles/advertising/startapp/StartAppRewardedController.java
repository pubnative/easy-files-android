package net.easynaps.easyfiles.advertising.startapp;

import android.app.Activity;

import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class StartAppRewardedController implements RewardedVideoPlacement, AdEventListener, AdDisplayListener, VideoListener {
    private final StartAppAd mInterstitial;
    private RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppRewardedController(Activity context, RewardedVideoPlacementListener listener) {
        this.mInterstitial = new StartAppAd(context);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.STARTAPP);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mInterstitial.setVideoListener(this);
        mInterstitial.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, this);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.showAd(this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isReady();
    }

    //--------------------------------- AdEventListener methods ------------------------------------
    @Override
    public void onReceiveAd(Ad ad) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onVideoError(new Exception(ad.getErrorMessage()));
        }
    }

    //-------------------------------- AdDisplayListener methods -----------------------------------
    @Override
    public void adHidden(Ad ad) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    @Override
    public void adDisplayed(Ad ad) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onVideoOpened();
            mListener.onVideoStarted();
        }
    }

    @Override
    public void adClicked(Ad ad) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void adNotDisplayed(Ad ad) {
        mAnalyticsSession.confirmInterstitialShowError();
    }

    @Override
    public void onVideoCompleted() {
        if (mListener != null) {
            mListener.onVideoCompleted();
            mListener.onReward(null);
        }
    }
}
