package net.easynaps.easyfiles.advertising.fyber;

import android.view.View;

import com.heyzap.sdk.ads.BannerAdView;
import com.heyzap.sdk.ads.HeyzapAds;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class FyberBannerController implements AdPlacement, HeyzapAds.BannerListener {
    private final BannerAdView mAdView;
    private final AdPlacementListener mListener;

    public FyberBannerController(BannerAdView adView, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setBannerListener(this);

        this.mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAdView.load();
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

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onAdLoaded(BannerAdView bannerAdView) {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onAdError(BannerAdView bannerAdView, HeyzapAds.BannerError bannerError) {
        if (mListener != null) {
            mListener.onAdError(new Exception(bannerError.getErrorMessage()));
        }
    }

    @Override
    public void onAdClicked(BannerAdView bannerAdView) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
