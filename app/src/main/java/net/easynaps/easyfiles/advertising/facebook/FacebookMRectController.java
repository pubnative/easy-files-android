package net.easynaps.easyfiles.advertising.facebook;

import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class FacebookMRectController implements AdPlacement, AdListener {
    private final AdView mAdView;
    private final AdPlacementListener mListener;

    public FacebookMRectController(AdView adView, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdListener(this);

        this.mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAdView.loadAd();
    }

    @Override
    public void show() {
        mAdView.setVisibility(View.VISIBLE);
    }

    @Override
    public void destroy() {
        mAdView.setVisibility(View.GONE);
        mAdView.destroy();
    }

    //----------------------------------- AdListener methods ---------------------------------------
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
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
