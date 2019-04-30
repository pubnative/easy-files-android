package net.easynaps.easyfiles.advertising.googleadmanager;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class GoogleMRectController implements AdPlacement {
    private final PublisherAdView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public GoogleMRectController(PublisherAdView adView, String adUnitId, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdListener(mAdListener);

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.GOOGLE_ADS_MANAGER);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAnalyticsSession.start();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void destroy() {
        mAdView.destroy();
        mListener = null;
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
            mAnalyticsSession.confirmOpened();
        }

        @Override
        public void onAdLeftApplication() {
            mAnalyticsSession.confirmLeftApplication();
            super.onAdLeftApplication();
        }

        @Override
        public void onAdClosed() {
            mAnalyticsSession.confirmClosed();
            super.onAdClosed();
        }
    };
}
