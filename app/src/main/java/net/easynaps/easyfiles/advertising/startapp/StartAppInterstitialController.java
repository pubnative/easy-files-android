package net.easynaps.easyfiles.advertising.startapp;

import android.app.Activity;

import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import net.easynaps.easyfiles.advertising.InterstitialPlacement;
import net.easynaps.easyfiles.advertising.InterstitialPlacementListener;

public class StartAppInterstitialController implements InterstitialPlacement, AdEventListener, AdDisplayListener {
    private final StartAppAd mInterstitial;
    private final InterstitialPlacementListener mListener;

    public StartAppInterstitialController(Activity context, String adUnitId, InterstitialPlacementListener listener) {
        this.mInterstitial = new StartAppAd(context);
        this.mListener = listener;
    }


    //------------------------------ InterstitialPlacement methods ---------------------------------
    @Override
    public void loadAd() {
        mInterstitial.loadAd(this);
    }

    @Override
    public void show() {
        mInterstitial.showAd(this);
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean isReady() {
        return mInterstitial.isReady();
    }

    //--------------------------------- AdEventListener methods ------------------------------------
    @Override
    public void onReceiveAd(Ad ad) {

    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {

    }

    //-------------------------------- AdDisplayListener methods -----------------------------------
    @Override
    public void adHidden(Ad ad) {

    }

    @Override
    public void adDisplayed(Ad ad) {

    }

    @Override
    public void adClicked(Ad ad) {

    }

    @Override
    public void adNotDisplayed(Ad ad) {

    }
}
