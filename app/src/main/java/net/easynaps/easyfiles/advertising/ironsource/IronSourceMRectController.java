package net.easynaps.easyfiles.advertising.ironsource;

import android.view.View;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class IronSourceMRectController implements AdPlacement, BannerListener {
    private final IronSourceBannerLayout mAdView;
    private final String mPlacementName;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public IronSourceMRectController(IronSourceBannerLayout adView, String placementName, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setBannerListener(this);
        this.mPlacementName = placementName;

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.IRONSOURCE);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        IronSource.loadBanner(mAdView, mPlacementName);
    }


    @Override
    public void destroy() {
        mListener = null;
    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onBannerAdLoaded() {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(ironSourceError.getErrorMessage()));
        }
    }

    @Override
    public void onBannerAdClicked() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onBannerAdScreenPresented() {
        mAnalyticsSession.confirmOpened();
    }

    @Override
    public void onBannerAdScreenDismissed() {
        mAnalyticsSession.confirmClosed();
    }

    @Override
    public void onBannerAdLeftApplication() {
        mAnalyticsSession.confirmLeftApplication();
    }
}
