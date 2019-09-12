package net.easynaps.easyfiles.advertising.appodeal;

import android.app.Activity;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AppodealInterstitialController implements InterstitialPlacement, InterstitialCallbacks {
    private final Activity mActivity;

    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public AppodealInterstitialController(Activity context, InterstitialPlacementListener listener ){
        this.mListener = listener;
        this.mActivity = context;

        Appodeal.setInterstitialCallbacks(this);
        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.APPODEAL);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        Appodeal.cache(mActivity, Appodeal.INTERSTITIAL);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        Appodeal.show(mActivity, Appodeal.INTERSTITIAL);
    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return Appodeal.isLoaded(Appodeal.INTERSTITIAL);
    }



    //------------------------------ InterstitialCallbacks methods ---------------------------------

    @Override
    public void onInterstitialLoaded(boolean b) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialFailedToLoad() {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception("Error loading Appodeal Interstitial"));
        }
    }

    @Override
    public void onInterstitialShown() {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void onInterstitialClicked() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onInterstitialClosed() {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void onInterstitialExpired() {

    }
}
