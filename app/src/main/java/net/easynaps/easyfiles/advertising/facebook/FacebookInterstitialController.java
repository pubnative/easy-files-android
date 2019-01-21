package net.easynaps.easyfiles.advertising.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class FacebookInterstitialController implements InterstitialPlacement, InterstitialAdListener {
    private final InterstitialAd mInterstitial;
    private final InterstitialPlacementListener mListener;

    public FacebookInterstitialController(Activity context, String placementId, InterstitialPlacementListener listener) {
        this.mInterstitial = new InterstitialAd(context, placementId);
        mInterstitial.setAdListener(this);
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mInterstitial.loadAd();
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
        return mInterstitial.isAdLoaded();
    }

    //------------------------------ InterstitialAdListener methods --------------------------------
    @Override
    public void onAdLoaded(Ad ad) {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        if (mListener != null) {
            mListener.onAdError(new Exception(adError.getErrorMessage()));
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
