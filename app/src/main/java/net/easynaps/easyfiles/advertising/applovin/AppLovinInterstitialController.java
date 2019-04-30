package net.easynaps.easyfiles.advertising.applovin;

import android.app.Activity;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinSdk;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class AppLovinInterstitialController implements InterstitialPlacement,
        AppLovinAdLoadListener, AppLovinAdDisplayListener, AppLovinAdClickListener {
    private final AppLovinInterstitialAdDialog mInterstitial;
    private final Activity mActivity;
    private InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    private AppLovinAd mAd;

    public AppLovinInterstitialController(Activity context, InterstitialPlacementListener listener) {
        this.mInterstitial = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(context), context);
        mInterstitial.setAdDisplayListener(this);
        mInterstitial.setAdClickListener(this);

        this.mActivity = context;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.APPLOVIN);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mAnalyticsSession.start();
        AppLovinSdk.getInstance(mActivity).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, this);
    }

    @Override
    public void show() {
        mAnalyticsSession.confirmInterstitialShow();
        mInterstitial.showAndRender(mAd);
    }

    @Override
    public void destroy() {
        mListener = null;
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isAdReadyToDisplay();
    }

    //----------------------------- AppLovinAdLoadListener methods ---------------------------------
    @Override
    public void adReceived(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmLoaded();
        mAd = appLovinAd;
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        mAnalyticsSession.confirmError();
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
        mAnalyticsSession.confirmImpression();
        mAnalyticsSession.confirmInterstitialShown();
        if (mListener != null) {
            mListener.onAdShown();
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmInterstitialDismissed();
        if (mListener != null) {
            mListener.onAdDismissed();
        }
    }

    //---------------------------- AppLovinAdClickListener methods ---------------------------------
    @Override
    public void adClicked(AppLovinAd appLovinAd) {
        mAnalyticsSession.confirmClick();
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
