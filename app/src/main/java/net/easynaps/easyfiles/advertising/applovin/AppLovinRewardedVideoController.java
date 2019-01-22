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

import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;

import java.util.Map;

public class AppLovinRewardedVideoController implements RewardedVideoPlacement,
        AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdClickListener,
        AppLovinAdRewardListener, AppLovinAdVideoPlaybackListener {
    private final AppLovinIncentivizedInterstitial mInterstitial;
    private final Activity mActivity;
    private final RewardedVideoPlacementListener mListener;

    public AppLovinRewardedVideoController(Activity context, RewardedVideoPlacementListener listener) {
        this.mInterstitial = AppLovinIncentivizedInterstitial.create(context);

        this.mActivity = context;
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mInterstitial.preload(this);
    }

    @Override
    public void show() {
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
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
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
        if (mListener != null) {
            mListener.onVideoOpened();
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    //---------------------------- AppLovinAdClickListener methods ---------------------------------
    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    //------------------------- AppLovinAdVideoPlaybackListener methods ----------------------------
    @Override
    public void videoPlaybackBegan(AppLovinAd appLovinAd) {
        if (mListener != null) {
            mListener.onVideoStarted();
        }
    }

    @Override
    public void videoPlaybackEnded(AppLovinAd appLovinAd, double percentViewed, boolean fullyWatched) {
        if (mListener != null) {
            mListener.onVideoCompleted();
        }
    }

    //---------------------------- AppLovinAdRewardListener methods --------------------------------
    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map<String, String> response) {
        if (mListener != null) {
            mListener.onReward(null);
        }
    }

    @Override
    public void userOverQuota(AppLovinAd appLovinAd, Map<String, String> response) {

    }

    @Override
    public void userRewardRejected(AppLovinAd appLovinAd, Map<String, String> response) {

    }

    @Override
    public void validationRequestFailed(AppLovinAd appLovinAd, int errorCode) {

    }

    @Override
    public void userDeclinedToViewAd(AppLovinAd appLovinAd) {

    }
}
