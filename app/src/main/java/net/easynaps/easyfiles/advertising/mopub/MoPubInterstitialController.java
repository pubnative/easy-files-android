package net.easynaps.easyfiles.advertising.mopub;

import android.app.Activity;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class MoPubInterstitialController implements InterstitialPlacement, MoPubInterstitial.InterstitialAdListener {
    private final MoPubInterstitial mInterstitial;
    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public MoPubInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
        this.mInterstitial = new MoPubInterstitial(context, adUnitId);
        mInterstitial.setInterstitialAdListener(this);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.MOPUB);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mInterstitial.load();
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.show();
    }

    @Override
    public void destroy() {
        mInterstitial.destroy();
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isReady();
    }

    //----------------------------- MoPubInterstitial listener methods -----------------------------
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(errorCode.toString()));
        }
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
