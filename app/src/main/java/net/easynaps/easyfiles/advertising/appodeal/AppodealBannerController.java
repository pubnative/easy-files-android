package net.easynaps.easyfiles.advertising.appodeal;

import android.app.Activity;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AppodealBannerController implements AdPlacement, BannerCallbacks {
    private final BannerView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;
    private final Activity mActivity;

    public AppodealBannerController(BannerView adView, AdPlacementListener listener, Activity activity) {
        this.mAdView = adView;
        Appodeal.setBannerCallbacks(this);

        this.mActivity = activity;

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.BANNER, AdNetwork.APPODEAL);
    }


    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        Appodeal.cache(mActivity, Appodeal.BANNER);
    }

    @Override
    public void destroy() {
        Appodeal.destroy(Appodeal.BANNER);
        mListener = null;
    }

    //----------------------------------- AdListener methods ---------------------------------------


    @Override
    public void onBannerLoaded(int i, boolean b) {
        mAnalyticsSession.confirmLoaded();
        if (Appodeal.isLoaded(Appodeal.BANNER)) {
            Appodeal.show(mActivity, Appodeal.BANNER_VIEW);
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }
    }

    @Override
    public void onBannerFailedToLoad() {
        mAnalyticsSession.confirmError();
        mListener.onAdError(new Exception("Error loading Appodeal Banner"));
    }

    @Override
    public void onBannerShown() {
        mAnalyticsSession.confirmImpression();
    }

    @Override
    public void onBannerClicked() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onBannerExpired() {

    }

}
