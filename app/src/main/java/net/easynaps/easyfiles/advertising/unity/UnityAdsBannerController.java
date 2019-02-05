package net.easynaps.easyfiles.advertising.unity;

import android.app.Activity;
import android.view.View;

import com.unity3d.services.UnityServices;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.monetization.IUnityMonetizationListener;
import com.unity3d.services.monetization.UnityMonetization;
import com.unity3d.services.monetization.placementcontent.core.PlacementContent;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class UnityAdsBannerController implements AdPlacement, IUnityMonetizationListener, IUnityBannerListener {
    private View mAdView;
    private final Activity mContext;
    private final String mPlacementId;
    private final AdPlacementListener mListener;

    public UnityAdsBannerController(Activity context, String gameId, String placementId, AdPlacementListener listener) {
        mContext = context;
        mPlacementId = placementId;
        mListener = listener;

        UnityMonetization.initialize(context, gameId, this, true);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        UnityBanners.loadBanner(mContext, mPlacementId);
    }

    @Override
    public void destroy() {
        UnityBanners.destroy();
    }

    //--------------------------- IUnityMonetizationListener methods -------------------------------
    @Override
    public void onPlacementContentReady(String placementId, PlacementContent placementContent) {

    }

    @Override
    public void onPlacementContentStateChange(String placementId, PlacementContent placementContent, UnityMonetization.PlacementContentState placementContentState, UnityMonetization.PlacementContentState placementContentState1) {

    }

    @Override
    public void onUnityServicesError(UnityServices.UnityServicesError unityServicesError, String message) {

    }

    //------------------------------ IUnityBannerListener methods ----------------------------------
    @Override
    public void onUnityBannerLoaded(String placementId, View view) {
        mAdView = view;
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onUnityBannerError(String placementId) {
        if (mListener != null) {
            mListener.onAdError(new Exception("Error fetching unity ad."));
        }
    }

    @Override
    public void onUnityBannerUnloaded(String placementId) {

    }

    @Override
    public void onUnityBannerShow(String placementId) {

    }

    @Override
    public void onUnityBannerHide(String placementId) {

    }

    @Override
    public void onUnityBannerClick(String placementId) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
