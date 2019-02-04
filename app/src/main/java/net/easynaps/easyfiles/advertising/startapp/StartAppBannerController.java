package net.easynaps.easyfiles.advertising.startapp;

import android.view.View;

import com.startapp.android.publish.ads.banner.Banner;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class StartAppBannerController implements AdPlacement {
    private final Banner mAdView;
    private final AdPlacementListener mListener;

    public StartAppBannerController(Banner adView, String adUnitId, AdPlacementListener listener) {
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
        //mAdView.load;
    }

    @Override
    public void destroy() {
        //mAdView.destroy();
    }
}
