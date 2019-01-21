package net.easynaps.easyfiles.advertising.mopub;

import android.app.Activity;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class MoPubInterstitialController implements InterstitialPlacement, MoPubInterstitial.InterstitialAdListener {
    private final MoPubInterstitial mInterstitial;
    private final InterstitialPlacementListener mListener;

    public MoPubInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
        this.mInterstitial = new MoPubInterstitial(context, adUnitId);
        mInterstitial.setInterstitialAdListener(this);
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mInterstitial.load();
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
        return mInterstitial.isReady();
    }

    //----------------------------- MoPubInterstitial listener methods -----------------------------
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        if (mListener != null) {
            mListener.onAdError(new Exception(errorCode.toString()));
        }
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
