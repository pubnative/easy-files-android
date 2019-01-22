package net.easynaps.easyfiles.advertising.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class IronSourceInterstitialController implements InterstitialPlacement {
    private final String mPlacementName;
    private final InterstitialPlacementListener mListener;

    public IronSourceInterstitialController(String placementName, InterstitialPlacementListener listener) {
        this.mPlacementName = placementName;
        this.mListener = listener;
        IronSource.setInterstitialListener(mInterstitialListener);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        IronSource.loadInterstitial();
    }

    @Override
    public void show() {
        IronSource.showInterstitial(mPlacementName);
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isReady() {
        return IronSource.isInterstitialReady();
    }

    //------------------------------ InterstitialListener methods ----------------------------------
    private final InterstitialListener mInterstitialListener = new InterstitialListener() {
        @Override
        public void onInterstitialAdReady() {
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }

        @Override
        public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
            if (mListener != null) {
                mListener.onAdError(new Exception(ironSourceError.getErrorMessage()));
            }
        }

        @Override
        public void onInterstitialAdOpened() {

        }

        @Override
        public void onInterstitialAdClosed() {
            if (mListener != null) {
                mListener.onAdDismissed();
            }
        }

        @Override
        public void onInterstitialAdShowSucceeded() {
            if (mListener != null) {
                mListener.onAdShown();
            }
        }

        @Override
        public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

        }

        @Override
        public void onInterstitialAdClicked() {
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }
    };
}
