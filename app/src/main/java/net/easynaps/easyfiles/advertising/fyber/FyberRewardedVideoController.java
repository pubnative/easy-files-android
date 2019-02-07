package net.easynaps.easyfiles.advertising.fyber;

import android.app.Activity;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.IncentivizedAd;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class FyberRewardedVideoController implements RewardedVideoPlacement {
    private final Activity mActivity;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public FyberRewardedVideoController(Activity context, RewardedVideoPlacementListener listener) {
        this.mActivity = context;
        this.mListener = listener;

        IncentivizedAd.setOnStatusListener(mInterstitialListener);
        IncentivizedAd.setOnIncentiveResultListener(mRewardedListener);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.FYBER);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        IncentivizedAd.fetch();
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        IncentivizedAd.display(mActivity);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isReady() {
        return IncentivizedAd.isAvailable();
    }

    //-------------------------------- OnStatusListener methods ------------------------------------
    private final HeyzapAds.OnStatusListener mInterstitialListener = new HeyzapAds.OnStatusListener() {
        @Override
        public void onShow(String tag) {
            mAnalyticsSession.confirmImpression();
            mAnalyticsSession.confirmInterstitialShown();
            if (mListener != null) {
                mListener.onVideoOpened();
            }
        }

        @Override
        public void onClick(String tag) {
            mAnalyticsSession.confirmClick();
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onHide(String tag) {
            mAnalyticsSession.confirmInterstitialDismissed();
            if (mListener != null) {
                mListener.onVideoClosed();
            }
        }

        @Override
        public void onFailedToShow(String tag) {
            mAnalyticsSession.confirmInterstitialShowError();
        }

        @Override
        public void onAvailable(String tag) {
            mAnalyticsSession.confirmLoaded();
            if (mListener != null) {
                mListener.onVideoLoaded();
            }
        }

        @Override
        public void onFailedToFetch(String tag) {
            mAnalyticsSession.confirmError();
            if (mListener != null) {
                mListener.onVideoError(new Exception("Fyber - No ad was received."));
            }
        }

        @Override
        public void onAudioStarted() {
            mAnalyticsSession.confirmAudioStarted();
        }

        @Override
        public void onAudioFinished() {
            mAnalyticsSession.confirmAudioFinished();
        }
    };

    //----------------------------------- AdListener methods ---------------------------------------
    private final HeyzapAds.OnIncentiveResultListener mRewardedListener = new HeyzapAds.OnIncentiveResultListener() {
        @Override
        public void onIncomplete(String s) {
            mAnalyticsSession.confirmVideoIncomplete();
        }

        @Override
        public void onComplete(String s) {
            mAnalyticsSession.confirmVideoFinished();
            if (mListener != null) {
                mListener.onVideoCompleted();
                mListener.onReward(null);
            }
        }
    };
}
