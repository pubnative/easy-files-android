package net.easynaps.easyfiles.advertising.applovin;

import android.view.View;

import com.applovin.adview.AppLovinAdView;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinErrorCodes;

import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;

public class AppLovinBannerController implements AdPlacement,
        AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdClickListener {
    private final AppLovinAdView mAdView;
    private final AdPlacementListener mListener;

    public AppLovinBannerController(AppLovinAdView adView, AdPlacementListener listener) {
        this.mAdView = adView;
        mAdView.setAdLoadListener(this);
        mAdView.setAdDisplayListener(this);
        mAdView.setAdClickListener(this);
        mListener = listener;
    }

    //---------------------------------- AdPlacement methods ---------------------------------------
    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public void loadAd() {
        mAdView.loadNextAd();
    }

    @Override
    public void destroy() {
        mAdView.destroy();
    }

    //----------------------------- AppLovinAdLoadListener methods ---------------------------------
    @Override
    public void adReceived(AppLovinAd appLovinAd) {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        if (mListener != null) {
            switch (errorCode) {
                case AppLovinErrorCodes.NO_FILL:
                    mListener.onAdError(new Exception("AppLovin - No fill"));
                    break;
                default:
                    mListener.onAdError(new Exception("AppLovin - Error trying to load ad"));
            }
        }
    }

    //--------------------------- AppLovinAdDisplayListener methods --------------------------------
    @Override
    public void adDisplayed(AppLovinAd appLovinAd) {

    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {

    }

    //---------------------------- AppLovinAdClickListener methods ---------------------------------
    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
