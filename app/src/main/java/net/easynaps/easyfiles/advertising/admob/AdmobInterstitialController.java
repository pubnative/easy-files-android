package net.easynaps.easyfiles.advertising.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class AdmobInterstitialController implements InterstitialPlacement {
    private final InterstitialAd mInterstitial;
    private final InterstitialPlacementListener mListener;

    public AdmobInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
        this.mInterstitial = new InterstitialAd(context);
        mInterstitial.setAdUnitId(adUnitId);
        mInterstitial.setAdListener(mAdListener);
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitial.loadAd(adRequest);
    }

    @Override
    public void show() {
        mInterstitial.show();
    }

    @Override
    public void destroy() {

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
                        mListener.onAdError(new Exception("Google Ad Manager - Internal error"));
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        mListener.onAdError(new Exception("Google Ad Manager - Invalid request"));
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        mListener.onAdError(new Exception("Google Ad Manager - Network error"));
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        mListener.onAdError(new Exception("Google Ad Manager - No fill"));
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
