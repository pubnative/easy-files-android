package net.easynaps.easyfiles.advertising.unity;

import android.app.Activity;
import android.view.View;

import com.unity3d.ads.UnityAds;
import com.unity3d.services.UnityServices;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.monetization.IUnityMonetizationListener;
import com.unity3d.services.monetization.UnityMonetization;
import com.unity3d.services.monetization.placementcontent.core.PlacementContent;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class UnityAdsBannerController implements AdPlacement, IUnityMonetizationListener, IUnityBannerListener {
    private View mAdView;
    private final Activity mContext;
    private final String mPlacementId;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public UnityAdsBannerController(Activity context, String gameId, String placementId, AdPlacementListener listener) {
        mContext = context;
        mPlacementId = placementId;
        mListener = listener;

        UnityMonetization.initialize(context, gameId, this, true);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.BANNER, AdNetwork.UNITY);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        UnityBanners.loadBanner(mContext, mPlacementId);
    }

    @Override
    public void destroy() {
        UnityBanners.destroy();
        mListener = null;
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
        mAnalyticsSession.confirmLoaded();
        mAdView = view;
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onUnityBannerError(String placementId) {
        mAnalyticsSession.confirmError();
        if (mListener != null) {
            mListener.onAdError(new Exception("Error fetching unity ad."));
        }
    }

    @Override
    public void onUnityBannerUnloaded(String placementId) {

    }

    @Override
    public void onUnityBannerShow(String placementId) {
        mAnalyticsSession.confirmImpression();
    }

    @Override
    public void onUnityBannerHide(String placementId) {

    }

    @Override
    public void onUnityBannerClick(String placementId) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
