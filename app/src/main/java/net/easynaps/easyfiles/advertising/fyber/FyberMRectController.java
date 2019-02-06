package net.easynaps.easyfiles.advertising.fyber;

import android.view.View;

import com.heyzap.sdk.ads.BannerAdView;
import com.heyzap.sdk.ads.HeyzapAds;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class FyberMRectController implements AdPlacement, HeyzapAds.BannerListener {
    private final BannerAdView mAdView;
    private final AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public FyberMRectController(BannerAdView adView, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setBannerListener(this);

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.FYBER);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mAdView.load();
    }

    @Override
    public void destroy() {
        mAdView.destroy();
    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onAdLoaded(BannerAdView bannerAdView) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onAdError(BannerAdView bannerAdView, HeyzapAds.BannerError bannerError) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(bannerError.getErrorMessage()));
        }
    }

    @Override
    public void onAdClicked(BannerAdView bannerAdView) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
