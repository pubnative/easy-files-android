package net.easynaps.easyfiles.advertising.startapp;

import android.view.View;

import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.ads.banner.BannerListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class StartAppBannerController implements AdPlacement, BannerListener {
    private final Banner mAdView;
    private final AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppBannerController(Banner adView, AdPlacementListener listener) {
        mAdView = adView;

        mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.BANNER, AdNetwork.STARTAPP);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mAdView.loadAd();
    }

    @Override
    public void destroy() {
    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onReceiveAd(View view) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onFailedToReceiveAd(View view) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception("Error loading StartApp MRect"));
        }
    }

    @Override
    public void onClick(View view) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
