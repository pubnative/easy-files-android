package net.easynaps.easyfiles.advertising.unity;

import android.app.Activity;

import com.unity3d.ads.UnityAds;
import com.unity3d.services.UnityServices;
import com.unity3d.services.monetization.IUnityMonetizationListener;
import com.unity3d.services.monetization.UnityMonetization;
import com.unity3d.services.monetization.placementcontent.ads.IShowAdListener;
import com.unity3d.services.monetization.placementcontent.ads.ShowAdPlacementContent;
import com.unity3d.services.monetization.placementcontent.core.PlacementContent;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdReward;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class UnityAdsRewardedController implements RewardedVideoPlacement, IUnityMonetizationListener, IShowAdListener {
    private final String mPlacementId;
    private final String mGameId;
    private final Activity mActivity;
    private RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public UnityAdsRewardedController(Activity context, String gameId, String placementId, RewardedVideoPlacementListener listener) {
        this.mGameId = gameId;
        this.mPlacementId = placementId;
        this.mActivity = context;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.UNITY);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        UnityMonetization.initialize(mActivity, mGameId, this, true);
    }

    @Override
    public void show() {
        PlacementContent placementContent = UnityMonetization.getPlacementContent(mPlacementId);
        if (placementContent.getType().equalsIgnoreCase("SHOW_AD")) {
            ShowAdPlacementContent showAdPlacementContent = (ShowAdPlacementContent) placementContent;
            showAdPlacementContent.show(mActivity, this);
        }
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
        return UnityMonetization.isReady(mPlacementId);
    }

    //--------------------------------- IShowAdListener methods ------------------------------------
    @Override
    public void onAdFinished(String placementId, UnityAds.FinishState finishState) {
        if (finishState == UnityAds.FinishState.COMPLETED) {
            if (placementId.equalsIgnoreCase(mPlacementId)) {
                mAnalyticsSession.confirmVideoFinished();
                if (mListener != null) {
                    mListener.onVideoCompleted();
                    mListener.onReward(new AdReward("", 0));
                }
            }
        }
    }

    @Override
    public void onAdStarted(String s) {
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        mAnalyticsSession.confirmVideoStarted();
        if (mListener != null) {
            mListener.onVideoStarted();
        }
    }

    //--------------------------- IUnityMonetizationListener methods -------------------------------
    @Override
    public void onPlacementContentReady(String placementId, PlacementContent placementContent) {
        if (placementId.equalsIgnoreCase(mPlacementId)) {
            mAnalyticsSession.confirmLoaded();
            mListener.onVideoLoaded();
        }
    }

    @Override
    public void onPlacementContentStateChange(String placementId, PlacementContent placementContent, UnityMonetization.PlacementContentState placementContentState, UnityMonetization.PlacementContentState placementContentState1) {

    }

    @Override
    public void onUnityServicesError(UnityServices.UnityServicesError unityServicesError, String message) {

    }
}
