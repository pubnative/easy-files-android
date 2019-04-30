package net.easynaps.easyfiles.advertising.admob;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AdmobBannerController implements AdPlacement {
    private final AdView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public AdmobBannerController(AdView adView, String adUnitId, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdListener(mAdListener);

        mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.BANNER, AdNetwork.ADMOB);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
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
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        mListener.onAdError(new Exception("Admob - Internal error"));
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        mListener.onAdError(new Exception("Admob - Invalid request"));
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        mListener.onAdError(new Exception("Admob - Network error"));
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        mListener.onAdError(new Exception("Admob - No fill"));
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
            super.onAdLeftApplication();
            mAnalyticsSession.confirmLeftApplication();
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            mAnalyticsSession.confirmClosed();
        }
    };
}
