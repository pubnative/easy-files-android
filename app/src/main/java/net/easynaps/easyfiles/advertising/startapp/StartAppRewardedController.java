package net.easynaps.easyfiles.advertising.startapp;

import net.easynaps.easyfiles.advertising.RewardedVideoPlacement;
import net.easynaps.easyfiles.advertising.RewardedVideoPlacementListener;

public class StartAppRewardedController implements RewardedVideoPlacement {
    private final String mAdUnitId;
    private final RewardedVideoPlacementListener mListener;

    public StartAppRewardedController(String adUnitId, RewardedVideoPlacementListener listener) {
        this.mAdUnitId = adUnitId;
        this.mListener = listener;
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
