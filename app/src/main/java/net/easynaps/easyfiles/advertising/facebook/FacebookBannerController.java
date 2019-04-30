package net.easynaps.easyfiles.advertising.facebook;

import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class FacebookBannerController implements AdPlacement, AdListener {
    private final AdView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public FacebookBannerController(AdView adView, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdListener(this);

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.BANNER, AdNetwork.FACEBOOK);
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

    //----------------------------------- AdListener methods ---------------------------------------
    @Override
    public void onAdLoaded(Ad ad) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception(adError.getErrorMessage()));
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        mAnalyticsSession.confirmImpression();
    }

    @Override
    public void onAdClicked(Ad ad) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
