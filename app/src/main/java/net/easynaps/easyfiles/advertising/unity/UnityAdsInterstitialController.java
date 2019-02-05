package net.easynaps.easyfiles.advertising.unity;

import android.app.Activity;

import com.unity3d.ads.UnityAds;
import com.unity3d.services.UnityServices;
import com.unity3d.services.monetization.IUnityMonetizationListener;
import com.unity3d.services.monetization.UnityMonetization;
import com.unity3d.services.monetization.placementcontent.ads.IShowAdListener;
import com.unity3d.services.monetization.placementcontent.ads.ShowAdPlacementContent;
import com.unity3d.services.monetization.placementcontent.core.PlacementContent;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class UnityAdsInterstitialController implements InterstitialPlacement, IUnityMonetizationListener, IShowAdListener {
    private final String mPlacementId;
    private final String mGameId;
    private final Activity mActivity;
    private final InterstitialPlacementListener mListener;

    public UnityAdsInterstitialController(Activity context, String gameId, String placementId, InterstitialPlacementListener listener) {
        this.mGameId = gameId;
        this.mPlacementId = placementId;
        this.mActivity = context;
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
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
    public void destroy() {

    }

    @Override
    public boolean isReady() {
        return UnityMonetization.isReady(mPlacementId);
    }

    //--------------------------------- IShowAdListener methods ------------------------------------
    @Override
    public void onAdFinished(String s, UnityAds.FinishState finishState) {

    }

    @Override
    public void onAdStarted(String s) {
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    //--------------------------- IUnityMonetizationListener methods -------------------------------
    @Override
    public void onPlacementContentReady(String placementId, PlacementContent placementContent) {
        if (placementId.equalsIgnoreCase(mPlacementId)) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onPlacementContentStateChange(String placementId, PlacementContent placementContent, UnityMonetization.PlacementContentState placementContentState, UnityMonetization.PlacementContentState placementContentState1) {

    }

    @Override
    public void onUnityServicesError(UnityServices.UnityServicesError unityServicesError, String message) {

    }
}
