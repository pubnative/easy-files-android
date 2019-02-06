package net.easynaps.easyfiles.advertising.fyber;

import android.app.Activity;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.InterstitialAd;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class FyberInterstitialController implements InterstitialPlacement {
    private final Activity mActivity;
    private final InterstitialPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public FyberInterstitialController(Activity context, InterstitialPlacementListener listener) {
        this.mActivity = context;
        this.mListener = listener;

        InterstitialAd.setOnStatusListener(mInterstitialListener);

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.INTERSTITIAL, AdNetwork.FYBER);
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        InterstitialAd.fetch();
    }

    @Override
    public void show() {
        InterstitialAd.display(mActivity);
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isReady() {
        return InterstitialAd.isAvailable();
    }

    //-------------------------------- OnStatusListener methods ------------------------------------
    private final HeyzapAds.OnStatusListener mInterstitialListener = new HeyzapAds.OnStatusListener() {
        @Override
        public void onShow(String tag) {
            if (mListener != null) {
                mListener.onAdShown();
            }
        }

        @Override
        public void onClick(String tag) {
            if (mListener != null) {
                mListener.onAdClicked();
            }
        }

        @Override
        public void onHide(String tag) {
            if (mListener != null) {
                mListener.onAdDismissed();
            }
        }

        @Override
        public void onFailedToShow(String tag) {

        }

        @Override
        public void onAvailable(String tag) {
            if (mListener != null) {
                mListener.onAdLoaded();
            }
        }

        @Override
        public void onFailedToFetch(String tag) {
            if (mListener != null) {
                mListener.onAdError(new Exception("Fyber - No ad was received."));
            }
        }

        @Override
        public void onAudioStarted() {

        }

        @Override
        public void onAudioFinished() {

        }
    };
}
