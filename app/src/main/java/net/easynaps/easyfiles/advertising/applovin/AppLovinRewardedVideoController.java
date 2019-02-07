package net.easynaps.easyfiles.advertising.applovin;

import android.app.Activity;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinErrorCodes;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

import java.util.Map;

public class AppLovinRewardedVideoController implements RewardedVideoPlacement,
        AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdClickListener,
        AppLovinAdRewardListener, AppLovinAdVideoPlaybackListener {
    private final AppLovinIncentivizedInterstitial mInterstitial;
    private final Activity mActivity;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public AppLovinRewardedVideoController(Activity context, RewardedVideoPlacementListener listener) {
        this.mInterstitial = AppLovinIncentivizedInterstitial.create(context);

        this.mActivity = context;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.APPLOVIN);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        mInterstitial.preload(this);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.show(mActivity, this, this, this, this);
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
        return mInterstitial.isAdReadyToDisplay();
    }

    //----------------------------- AppLovinAdLoadListener methods ---------------------------------
    @Override
    public void adReceived(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            switch (errorCode) {
                case AppLovinErrorCodes.NO_FILL:
                    mListener.onVideoError(new Exception("AppLovin - No fill"));
                    break;
                default:
                    mListener.onVideoError(new Exception("AppLovin - Error trying to load ad"));
            }
        }
    }

    //--------------------------- AppLovinAdDisplayListener methods --------------------------------
    @Override
    public void adDisplayed(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onVideoOpened();
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    //---------------------------- AppLovinAdClickListener methods ---------------------------------
    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    //------------------------- AppLovinAdVideoPlaybackListener methods ----------------------------
    @Override
    public void videoPlaybackBegan(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmVideoStarted();
        if (mListener != null) {
            mListener.onVideoStarted();
        }
    }

    @Override
    public void videoPlaybackEnded(AppLovinAd appLovinAd, double percentViewed, boolean fullyWatched) {
        mAnalyticsSession.confirmVideoFinished();
        if (mListener != null) {
            mListener.onVideoCompleted();
        }
    }

    //---------------------------- AppLovinAdRewardListener methods --------------------------------
    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map<String, String> response) {
        mAnalyticsSession.confirmReward();
        if (mListener != null) {
            mListener.onReward(null);
        }
    }

    @Override
    public void userOverQuota(AppLovinAd appLovinAd, Map<String, String> response) {
        mAnalyticsSession.confirmUserOverQuota();
    }

    @Override
    public void userRewardRejected(AppLovinAd appLovinAd, Map<String, String> response) {
        mAnalyticsSession.confirmRewardRejected();
    }

    @Override
    public void validationRequestFailed(AppLovinAd appLovinAd, int errorCode) {
        mAnalyticsSession.confirmValidationRequestFailed();
    }

    @Override
    public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmDeclinedToViewAd();
    }
}
