package net.easynaps.easyfiles.advertising.pubnative;

import android.view.View;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;
import net.pubnative.lite.sdk.views.HyBidBannerAdView;
import net.pubnative.lite.sdk.views.PNAdView;

public class PubNativeBannerController implements AdPlacement, PNAdView.Listener {
    private final HyBidBannerAdView mAdView;
    private final String mZoneId;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public PubNativeBannerController(HyBidBannerAdView adView, String zoneId, AdPlacementListener listener) {
        this.mAdView = adView;
        this.mZoneId = zoneId;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.BANNER, AdNetwork.PUBNATIVE);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mAdView.load(mZoneId, this);
    }

    @Override
    public void destroy() {
        mAdView.destroy();
        mListener = null;
    }

    //---------------------------- HyBidBannerAdView listener methods ------------------------------
    @Override
    public void onAdLoaded() {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(Throwable error) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(error);
        }
    }

    @Override
    public void onAdImpression() {
        mAnalyticsSession.confirmImpression();
    }

    @Override
    public void onAdClick() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
