package net.easynaps.easyfiles.advertising.appodeal;

import android.app.Activity;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.MrecCallbacks;
import com.appodeal.ads.MrecView;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AppodealMRectController implements AdPlacement, MrecCallbacks {
    private final MrecView mAdView;
    private AdPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;
    private final Activity mActivity;

    public AppodealMRectController(MrecView adView, AdPlacementListener listener, Activity activity) {
        this.mAdView = adView;
        Appodeal.setMrecCallbacks(this);

        this.mActivity = activity;

        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(adView.getContext(), AdType.MRECT, AdNetwork.APPODEAL);
    }

    //---------------------------------- AdPlacement methods ---------------------------------------

    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        Appodeal.cache(mActivity, Appodeal.MREC);
    }

    @Override
    public void destroy() {
        Appodeal.destroy(Appodeal.MREC);
        mListener = null;
    }


    //----------------------------------- AdListener methods ---------------------------------------

    @Override
    public void onMrecLoaded(boolean b) {
        mAnalyticsSession.confirmLoaded();
        if (Appodeal.isLoaded(Appodeal.MREC)){
            Appodeal.show(mActivity,Appodeal.MREC);
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }
    }

    @Override
    public void onMrecFailedToLoad() {
        mAnalyticsSession.confirmError();
        mListener.onAdError(new Exception("Error loading Appodeal Mrect"));
    }

    @Override
    public void onMrecShown() {
        mAnalyticsSession.confirmImpression();
    }

    @Override
    public void onMrecClicked() {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onMrecExpired() {

    }

}
