package net.easynaps.easyfiles.advertising.mopub;

import android.view.View;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class MoPubBannerController implements AdPlacement, MoPubView.BannerAdListener {
    private final MoPubView mAdView;
    private final AdPlacementListener mListener;

    public MoPubBannerController(MoPubView adView, String adUnitId, AdPlacementListener listener) {
        mAdView = adView;
        mAdView.setAdUnitId(adUnitId);
        mAdView.setBannerAdListener(this);
        mAdView.setAutorefreshEnabled(false);

        mListener = listener;
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

    //------------------------------- BannerAdListener methods -------------------------------------
    @Override
    public void onBannerLoaded(MoPubView banner) {

    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

    }

    @Override
    public void onBannerClicked(MoPubView banner) {

    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }
}
