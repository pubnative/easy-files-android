package net.easynaps.easyfiles.advertising.ironsource;

import android.view.View;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class IronSourceMRectController implements AdPlacement, BannerListener {
    private final IronSourceBannerLayout mAdView;
    private final String mPlacementName;
    private final AdPlacementListener mListener;

    public IronSourceMRectController(IronSourceBannerLayout adView, String placementName, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setBannerListener(this);
        this.mPlacementName = placementName;

        this.mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        IronSource.loadBanner(mAdView, mPlacementName);
    }


    @Override
    public void destroy() {

    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onBannerAdLoaded() {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
        if (mListener != null) {
            mListener.onAdError(new Exception(ironSourceError.getErrorMessage()));
        }
    }

    @Override
    public void onBannerAdClicked() {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onBannerAdScreenPresented() {

    }

    @Override
    public void onBannerAdScreenDismissed() {

    }

    @Override
    public void onBannerAdLeftApplication() {

    }
}
