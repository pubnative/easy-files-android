package net.easynaps.easyfiles.advertising.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdReward;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class IronSourceRewardedVideoController implements RewardedVideoPlacement {
    private final String mPlacementName;
    private RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public IronSourceRewardedVideoController(Activity context, String placementName, RewardedVideoPlacementListener listener) {
        this.mPlacementName = placementName;
        this.mListener = listener;

        IronSource.setRewardedVideoListener(mRewardedListener);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.IRONSOURCE);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        IronSource.showRewardedVideo(mPlacementName);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return IronSource.isRewardedVideoAvailable();
    }

    //------------------------------ RewardedVideoListener methods ---------------------------------
    private final RewardedVideoListener mRewardedListener = new RewardedVideoListener() {
        @Override
        public void onRewardedVideoAvailabilityChanged(boolean available) {
            if (available) {
                mAnalyticsSession.confirmLoaded();
                if (mListener != null) {
                    mListener.onVideoLoaded();
                }
            }
        }

        @Override
        public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
            mAnalyticsSession.confirmError();
            if (mListener != null) {
                mListener.onVideoError(new Exception(ironSourceError.getErrorMessage()));
            }
        }

        @Override
        public void onRewardedVideoAdOpened() {
            mAnalyticsSession.confirmImpression();
            mAnalyticsSession.confirmInterstitialShown();
            if (mListener != null) {
                mListener.onVideoOpened();
            }
        }

        @Override
        public void onRewardedVideoAdClosed() {
            mAnalyticsSession.confirmInterstitialDismissed();
            if (mListener != null) {
                mListener.onVideoClosed();
            }
        }

        @Override
        public void onRewardedVideoAdStarted() {
            mAnalyticsSession.confirmVideoStarted();
            if (mListener != null) {
                mListener.onVideoStarted();
            }
        }

        @Override
        public void onRewardedVideoAdEnded() {
            mAnalyticsSession.confirmVideoFinished();
            if (mListener != null) {
                mListener.onVideoCompleted();
            }
        }

        @Override
        public void onRewardedVideoAdClicked(Placement placement) {
            mAnalyticsSession.confirmClick();
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onRewardedVideoAdRewarded(Placement placement) {
            mAnalyticsSession.confirmReward();
            if (mListener != null) {
                mListener.onReward(new AdReward(placement.getRewardName(), placement.getRewardAmount()));
            }
        }
    };
}
