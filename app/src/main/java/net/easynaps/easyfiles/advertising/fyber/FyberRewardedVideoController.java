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
        IncentivizedAd.fetch();
    }

    @Override
    public void show() {
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
            if (mListener != null) {
                mListener.onVideoOpened();
            }
        }

        @Override
        public void onClick(String tag) {
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onHide(String tag) {
            if (mListener != null) {
                mListener.onVideoClosed();
            }
        }

        @Override
        public void onFailedToShow(String tag) {

        }

        @Override
        public void onAvailable(String tag) {
            if (mListener != null) {
                mListener.onVideoLoaded();
            }
        }

        @Override
        public void onFailedToFetch(String tag) {
            if (mListener != null) {
                mListener.onVideoError(new Exception("Fyber - No ad was received."));
            }
        }

        @Override
        public void onAudioStarted() {

        }

        @Override
        public void onAudioFinished() {

        }
    };

    //----------------------------------- AdListener methods ---------------------------------------
    private final HeyzapAds.OnIncentiveResultListener mRewardedListener = new HeyzapAds.OnIncentiveResultListener() {
        @Override
        public void onIncomplete(String s) {

        }

        @Override
        public void onComplete(String s) {
            if (mListener != null) {
                mListener.onVideoCompleted();
            }
        }
    };
}
