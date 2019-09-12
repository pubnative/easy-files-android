package net.easynaps.easyfiles.advertising.appodeal;

import android.app.Activity;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AppodealRewardedVideoController implements RewardedVideoPlacement, RewardedVideoCallbacks {
    private final Activity mActivity;
    private RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;


    public AppodealRewardedVideoController(Activity context, RewardedVideoPlacementListener listener) {
        this.mActivity = context;
        this.mListener = listener;
        Appodeal.setRewardedVideoCallbacks(this);


        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.APPODEAL);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        Appodeal.cache(mActivity, Appodeal.REWARDED_VIDEO);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        Appodeal.show(mActivity, Appodeal.REWARDED_VIDEO);
    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return Appodeal.isLoaded(Appodeal.REWARDED_VIDEO);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    //----------------------------------- Callback methods -----------------------------------------

    @Override
    public void onRewardedVideoLoaded(boolean b) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void onRewardedVideoFailedToLoad() {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onVideoError(new Exception("Error loading Appodeal Interstitial"));
        }
    }

    @Override
    public void onRewardedVideoShown() {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onVideoOpened();
        }
    }

    @Override
    public void onRewardedVideoFinished(double v, String s) {
        mAnalyticsSession.confirmVideoFinished();
        if (mListener != null) {
            mListener.onVideoCompleted();
        }
    }

    @Override
    public void onRewardedVideoClosed(boolean b) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    @Override
    public void onRewardedVideoExpired() {

    }

    @Override
    public void onRewardedVideoClicked() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
