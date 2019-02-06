package net.easynaps.easyfiles.advertising.startapp;

import android.app.Activity;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;
import net.easynaps.easyfiles.advertising.analytics.AdAnalyticsSession;

public class StartAppRewardedController implements RewardedVideoPlacement {
    private final String mAdUnitId;
    private final RewardedVideoPlacementListener mListener;
    private final AdAnalyticsSession mAnalyticsSession;

    public StartAppRewardedController(Activity context, String adUnitId, RewardedVideoPlacementListener listener) {
        this.mAdUnitId = adUnitId;
        this.mListener = listener;

        mAnalyticsSession = new AdAnalyticsSession(context, AdType.REWARDED_VIDEO, AdNetwork.STARTAPP);
    }

    //------------------------------ RewardedVideoPlacement methods --------------------------------
    @Override
    public void loadAd() {
    }

    @Override
    public void show() {
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isReady() {
        return false;
    }
}
