package net.easynaps.easyfiles.advertising.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdReward;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AdmobRewardedVideoController implements RewardedVideoPlacement {
    private final RewardedVideoAd mAd;
    private final Activity mActivity;
    private final String mAdUnitId;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public AdmobRewardedVideoController(Activity context, String adUnitId, RewardedVideoPlacementListener listener) {
        this.mAd = MobileAds.getRewardedVideoAdInstance(context);
        mAd.setRewardedVideoAdListener(mAdListener);

        this.mAdUnitId = adUnitId;
        this.mActivity = context;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.ADMOB);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAd.loadAd(mAdUnitId, adRequest);
    }

    @Override
    public void show() {
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
            if (mListener != null) {
                mListener.onVideoLoaded();
            }
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int errorCode) {
            if (mListener != null) {
                switch (errorCode) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        mListener.onVideoError(new Exception("Admob - Internal error"));
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        mListener.onVideoError(new Exception("Admob - Invalid request"));
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        mListener.onVideoError(new Exception("Admob - Network error"));
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        mListener.onVideoError(new Exception("Admob - No fill"));
                        break;
                }
            }
        }

        @Override
        public void onRewardedVideoAdOpened() {
            if (mListener != null) {
                mListener.onVideoOpened();
            }
        }

        @Override
        public void onRewardedVideoAdClosed() {
            if (mListener != null) {
                mListener.onVideoClosed();
            }
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onRewardedVideoStarted() {
            if (mListener != null) {
                mListener.onVideoStarted();
            }
        }

        @Override
        public void onRewardedVideoCompleted() {
            if (mListener != null) {
                mListener.onVideoCompleted();
            }
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            if (mListener != null) {
                mListener.onReward(new AdReward(rewardItem.getType(), rewardItem.getAmount()));
            }
        }
    };
}
