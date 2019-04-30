package net.easynaps.easyfiles.advertising.googleadmanager;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class GoogleInterstitialController implements InterstitialPlacement {
    private final PublisherInterstitialAd mInterstitial;
    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public GoogleInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
        this.mInterstitial = new PublisherInterstitialAd(context);
        mInterstitial.setAdUnitId(adUnitId);
        mInterstitial.setAdListener(mAdListener);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.GOOGLE_ADS_MANAGER);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAnalyticsSession.start();
        mInterstitial.loadAd(adRequest);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.show();
    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isLoaded();
    }

    //----------------------------------- AdListener methods ---------------------------------------
    private final AdListener mAdListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            mAnalyticsSession.confirmLoaded();
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            mAnalyticsSession.confirmError();
            if (mListener != null) {
                switch (errorCode) {
                    case PublisherAdRequest.ERROR_CODE_INTERNAL_ERROR:
                        mListener.onAdError(new Exception("Google Ad Manager - Internal error"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_INVALID_REQUEST:
                        mListener.onAdError(new Exception("Google Ad Manager - Invalid request"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_NETWORK_ERROR:
                        mListener.onAdError(new Exception("Google Ad Manager - Network error"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_NO_FILL:
                        mListener.onAdError(new Exception("Google Ad Manager - No fill"));
                        break;
                }
            }
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
            mAnalyticsSession.confirmClick();
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            mAnalyticsSession.confirmImpression();
            mAnalyticsSession.confirmInterstitialShown();
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            mAnalyticsSession.confirmLeftApplication();
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            mAnalyticsSession.confirmInterstitialDismissed();
        }
    };
}
