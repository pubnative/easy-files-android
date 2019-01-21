package net.easynaps.easyfiles.advertising.admob;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class AdmobBannerController implements AdPlacement {
    private final AdView mAdView;
    private final AdPlacementListener mListener;

    public AdmobBannerController(AdView adView, String adUnitId, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdListener(mAdListener);

        mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void destroy() {
        mAdView.destroy();
    }

    //----------------------------------- AdListener methods ---------------------------------------
    private final AdListener mAdListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
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
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
        }
    };
}
