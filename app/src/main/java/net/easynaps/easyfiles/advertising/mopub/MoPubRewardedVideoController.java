package net.easynaps.easyfiles.advertising.mopub;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdReward;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

import java.util.Set;

public class MoPubRewardedVideoController implements RewardedVideoPlacement, MoPubRewardedVideoListener {
    private final String mAdUnitId;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public MoPubRewardedVideoController(Activity context, String adUnitId, RewardedVideoPlacementListener listener) {
        this.mAdUnitId = adUnitId;
        this.mListener = listener;

        MoPubRewardedVideos.setRewardedVideoListener(this);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.MOPUB);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        MoPubRewardedVideos.loadRewardedVideo(mAdUnitId);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        MoPubRewardedVideos.showRewardedVideo(mAdUnitId);
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
        return MoPubRewardedVideos.hasRewardedVideo(mAdUnitId);
    }

    //--------------------------- MoPubRewardedVideoListener methods -------------------------------
    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        mAnalyticsSession.confirmLoaded();
        if (mListener != null) {
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onVideoError(new Exception(errorCode.toString()));
        }
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        mAnalyticsSession.confirmVideoStarted();
        if (mListener != null) {
            mListener.onVideoStarted();
        }
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
        mAnalyticsSession.confirmVideoFinished();
        if (mListener != null) {
            mListener.onVideoCompleted();
            mListener.onReward(new AdReward(reward.getLabel(), reward.getAmount()));
        }
    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onVideoClosed();
        }
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        mAnalyticsSession.confirmVideoError();
        if (mListener != null) {
            mListener.onVideoError(new Exception(errorCode.toString()));
        }
    }
}
