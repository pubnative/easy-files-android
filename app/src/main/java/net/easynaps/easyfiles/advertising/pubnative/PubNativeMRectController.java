package net.easynaps.easyfiles.advertising.pubnative;

import android.view.View;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;
import net.pubnative.lite.sdk.views.HyBidMRectAdView;
import net.pubnative.lite.sdk.views.PNAdView;

public class PubNativeMRectController implements AdPlacement, PNAdView.Listener {
    private final HyBidMRectAdView mAdView;
    private final String mZoneId;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public PubNativeMRectController(HyBidMRectAdView adView, String zoneId, AdPlacementListener listener) {
        this.mAdView = adView;
        this.mZoneId = zoneId;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.PUBNATIVE);
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
