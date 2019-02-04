package net.easynaps.easyfiles.advertising.startapp;

import android.view.View;

import com.startapp.android.publish.ads.banner.BannerListener;
import com.startapp.android.publish.ads.banner.Mrec;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class StartAppMRectController implements AdPlacement, BannerListener {
    private final Mrec mAdView;
    private final AdPlacementListener mListener;

    public StartAppMRectController(Mrec adView, String adUnitId, AdPlacementListener listener) {
        mAdView = adView;

        mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        //mAdView.loadAd();
    }

    @Override
    public void destroy() {
        //mAdView.destroy();
    }

    //-------------------------------- BannerListener methods --------------------------------------
    @Override
    public void onReceiveAd(View view) {

    }

    @Override
    public void onFailedToReceiveAd(View view) {

    }

    @Override
    public void onClick(View view) {

    }
}
