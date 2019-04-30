package net.easynaps.easyfiles.advertising.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class IronSourceInterstitialController implements InterstitialPlacement {
    private final String mPlacementName;
    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public IronSourceInterstitialController(Activity context, String placementName, InterstitialPlacementListener listener) {
        this.mPlacementName = placementName;
        this.mListener = listener;
        IronSource.setInterstitialListener(mInterstitialListener);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.IRONSOURCE);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        IronSource.loadInterstitial();
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        IronSource.showInterstitial(mPlacementName);
    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return IronSource.isInterstitialReady();
    }

    //------------------------------ InterstitialListener methods ----------------------------------
    private final InterstitialListener mInterstitialListener = new InterstitialListener() {
        @Override
        public void onInterstitialAdReady() {
            mAnalyticsSession.confirmLoaded();
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }

        @Override
        public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
            mAnalyticsSession.confirmError();
            if (mListener != null) {
                mListener.onAdError(new Exception(ironSourceError.getErrorMessage()));
            }
        }

        @Override
        public void onInterstitialAdOpened() {
            mAnalyticsSession.confirmOpened();
        }

        @Override
        public void onInterstitialAdClosed() {
            mAnalyticsSession.confirmInterstitialDismissed();
            if (mListener != null) {
                mListener.onAdDismissed();
            }
        }

        @Override
        public void onInterstitialAdShowSucceeded() {
            mAnalyticsSession.confirmImpression();
            mAnalyticsSession.confirmInterstitialShown();
            if (mListener != null) {
                mListener.onAdShown();
            }
        }

        @Override
        public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
            mAnalyticsSession.confirmInterstitialShowError();
        }

        @Override
        public void onInterstitialAdClicked() {
            mAnalyticsSession.confirmClick();
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }
    };
}
