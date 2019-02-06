package net.easynaps.easyfiles.advertising.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class FacebookRewardedVideoController implements RewardedVideoPlacement, RewardedVideoAdListener {
    private final RewardedVideoAd mAd;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public FacebookRewardedVideoController(Activity context, String placementId, RewardedVideoPlacementListener listener) {
        this.mAd = new RewardedVideoAd(context, placementId);
        mAd.setAdListener(this);
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.FACEBOOK);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAd.loadAd();
    }

    @Override
    public void show() {
        mAd.show();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void destroy() {
        mAd.destroy();
    }

    @Override
    public boolean isReady() {
        return mAd.isAdLoaded();
    }

    //------------------------------ RewardedVideoAdListener methods --------------------------------
    @Override
    public void onAdLoaded(Ad ad) {
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        if (mListener != null) {
            mListener.onVideoError(new Exception(adError.getErrorMessage()));
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        if (mListener != null) {
            mListener.onVideoOpened();
        }
    }

    @Override
    public void onRewardedVideoCompleted() {
        if (mListener != null) {
            mListener.onVideoCompleted();
        }
    }

    @Override
    public void onRewardedVideoClosed() {
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
