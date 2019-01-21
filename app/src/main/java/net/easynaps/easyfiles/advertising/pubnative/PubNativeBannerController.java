package net.easynaps.easyfiles.advertising.pubnative;

import android.view.View;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.pubnative.lite.sdk.views.HyBidBannerAdView;
import net.pubnative.lite.sdk.views.PNAdView;

public class PubNativeBannerController implements AdPlacement, PNAdView.Listener {
    private final HyBidBannerAdView mAdView;
    private final String mZoneId;
    private final AdPlacementListener mListener;

    public PubNativeBannerController(HyBidBannerAdView adView, String zoneId, AdPlacementListener listener) {
        this.mAdView = adView;
        this.mZoneId = zoneId;
        this.mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAdView.load(mZoneId, this);
    }

    @Override
    public void destroy() {
        mAdView.destroy();
    }

    //---------------------------- HyBidBannerAdView listener methods ------------------------------
    @Override
    public void onAdLoaded() {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        if (mListener != null) {
            mListener.onAdError(error);
        }
    }

    @Override
    public void onAdImpression() {

    }

    @Override
    public void onAdClick() {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
