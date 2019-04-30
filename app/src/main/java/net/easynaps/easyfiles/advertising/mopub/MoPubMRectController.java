package net.easynaps.easyfiles.advertising.mopub;

import android.view.View;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class MoPubMRectController implements AdPlacement, MoPubView.BannerAdListener {
    private final MoPubView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public MoPubMRectController(MoPubView adView, String adUnitId, AdPlacementListener listener) {
        mAdView = adView;
        mAdView.setAdUnitId(adUnitId);
        mAdView.setBannerAdListener(this);
        mAdView.setAutorefreshEnabled(false);

        mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.MOPUB);
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
        mAdView.destroy();
        mListener = null;
    }

    //------------------------------- BannerAdListener methods -------------------------------------
    @Override
    public void onBannerLoaded(MoPubView banner) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(errorCode.toString()));
        }
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
        mAnalyticsSession.confirmOpened();
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
        mAnalyticsSession.confirmClosed();
    }
}
