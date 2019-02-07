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
    private final InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
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
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isReady();
    }

    //--------------------------------- AdEventListener methods ------------------------------------
    @Override
    public void onReceiveAd(Ad ad) {
        mAnalyticsSession.confirmLoaded();
    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {
        mAnalyticsSession.confirmError();
    }

    //-------------------------------- AdDisplayListener methods -----------------------------------
    @Override
    public void adHidden(Ad ad) {
        mAnalyticsSession.confirmInterstitialDismissed();
    }

    @Override
    public void adDisplayed(Ad ad) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
    }

    @Override
    public void adClicked(Ad ad) {
        mAnalyticsSession.confirmClick();
    }

    @Override
    public void adNotDisplayed(Ad ad) {
        mAnalyticsSession.confirmInterstitialShowError();
    }
}
