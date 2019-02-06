package net.easynaps.easyfiles.advertising.pubnative;

import android.app.Activity;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;

public class PubNativeInterstitialController implements InterstitialPlacement, HyBidInterstitialAd.Listener {
    private final HyBidInterstitialAd mInterstitial;
    private final InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public PubNativeInterstitialController(Activity context, String zoneId, InterstitialPlacementListener listener) {
        this.mInterstitial = new HyBidInterstitialAd(context, zoneId, this);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.PUBNATIVE);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mInterstitial.load();
    }

    @Override
    public void show() {
        mInterstitial.show();
    }

    @Override
    public void destroy() {
        mInterstitial.destroy();
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isReady();
    }

    //---------------------------- HyBidInterstitialAd listener methods ----------------------------
    @Override
    public void onInterstitialLoaded() {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialLoadFailed(Throwable error) {
        if (mListener != null) {
            mListener.onAdError(error);
        }
    }

    @Override
    public void onInterstitialImpression() {
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void onInterstitialDismissed() {
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void onInterstitialClick() {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
