package net.easynaps.easyfiles.advertising.googleadmanager;

import android.app.Activity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdReward;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class GoogleRewardedVideoController implements RewardedVideoPlacement {
    private final RewardedVideoAd mAd;
    private final Activity mActivity;
    private final String mAdUnitId;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public GoogleRewardedVideoController(Activity context, String adUnitId, RewardedVideoPlacementListener listener) {
        this.mAd = MobileAds.getRewardedVideoAdInstance(context);
        mAd.setRewardedVideoAdListener(mAdListener);

        this.mAdUnitId = adUnitId;
        this.mActivity = context;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.GOOGLE_ADS_MANAGER);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAnalyticsSession.start();
        mAd.loadAd(mAdUnitId, adRequest);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mAd.show();
    }

    @Override
    public void onResume() {
        mAd.resume(mActivity);
    }

    @Override
    public void onPause() {
        mAd.pause(mActivity);
    }

    @Override
    public void destroy() {
        mAd.destroy(mActivity);
    }

    @Override
    public boolean isReady() {
        return mAd.isLoaded();
    }

    //----------------------------- RewardedVideoAdListener methods --------------------------------
    private final RewardedVideoAdListener mAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            mAnalyticsSession.confirmLoaded();
            if (mListener != null) {
                mListener.onVideoLoaded();
            }
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int errorCode) {
            mAnalyticsSession.confirmError();
            if (mListener != null) {
                switch (errorCode) {
                    case PublisherAdRequest.ERROR_CODE_INTERNAL_ERROR:
                        mListener.onVideoError(new Exception("Google Ad Manager - Internal error"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_INVALID_REQUEST:
                        mListener.onVideoError(new Exception("Google Ad Manager - Invalid request"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_NETWORK_ERROR:
                        mListener.onVideoError(new Exception("Google Ad Manager - Network error"));
                        break;
                    case PublisherAdRequest.ERROR_CODE_NO_FILL:
                        mListener.onVideoError(new Exception("Google Ad Manager - No fill"));
                        break;
                }
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
        public void onRewardedVideoAdLeftApplication() {
            mAnalyticsSession.confirmLeftApplication();
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onRewardedVideoStarted() {
            mAnalyticsSession.confirmVideoStarted();
            if (mListener != null) {
                mListener.onVideoStarted();
            }
        }

        @Override
        public void onRewardedVideoCompleted() {
            mAnalyticsSession.confirmVideoFinished();
            if (mListener != null) {
                mListener.onVideoCompleted();
            }
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            mAnalyticsSession.confirmReward();
            if (mListener != null) {
                mListener.onReward(new AdReward(rewardItem.getType(), rewardItem.getAmount()));
            }
        }
    };
}