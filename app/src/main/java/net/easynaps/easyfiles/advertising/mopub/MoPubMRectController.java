package net.easynaps.easyfiles.advertising.mopub;

import android.view.View;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class MoPubMRectController implements AdPlacement, MoPubView.BannerAdListener {
    private final MoPubView mAdView;
    private final AdPlacementListener mListener;

    public MoPubMRectController(MoPubView adView, String adUnitId, AdPlacementListener listener) {
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
    public void destroy() {
        mAdView.destroy();
    }

    //------------------------------- BannerAdListener methods -------------------------------------
    @Override
    public void onBannerLoaded(MoPubView banner) {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        if (mListener != null) {
            mListener.onAdError(new Exception(errorCode.toString()));
        }
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }
}
