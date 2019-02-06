package net.easynaps.easyfiles.advertising.startapp;

import android.view.View;

import com.startapp.android.publish.ads.banner.BannerListener;
import com.startapp.android.publish.ads.banner.Mrec;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class StartAppMRectController implements AdPlacement, BannerListener {
    private final Mrec mAdView;
    private final AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppMRectController(Mrec adView, String adUnitId, AdPlacementListener listener) {
        mAdView = adView;

        mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.STARTAPP);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        //mAdView.loadAd();
    }

    @Override
    public void destroy() {
        //mAdView.destroy();
    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onReceiveAd(View view) {
        mAnalyticsSession.confirmLoaded();
    }

    @Override
    public void onFailedToReceiveAd(View view) {
        mAnalyticsSession.confirmError();
    }

    @Override
    public void onClick(View view) {
        mAnalyticsSession.confirmClick();
    }
}
