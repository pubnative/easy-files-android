package net.easynaps.easyfiles.advertising.startapp;

import android.app.Activity;

import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class StartAppInterstitialController implements InterstitialPlacement, AdEventListener, AdDisplayListener {
    private final StartAppAd mInterstitial;
    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppInterstitialController(Activity context, InterstitialPlacementListener listener) {
        this.mInterstitial = new StartAppAd(context);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.STARTAPP);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mInterstitial.loadAd(this);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.showAd(this);
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
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(ad.getErrorMessage()));
        }
    }

    //-------------------------------- AdDisplayListener methods -----------------------------------
    @Override
    public void adHidden(Ad ad) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void adDisplayed(Ad ad) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onAdShown();
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
}
