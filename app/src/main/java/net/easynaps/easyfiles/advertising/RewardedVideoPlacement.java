package net.easynaps.easyfiles.advertising;

public interface RewardedVideoPlacement {
    void loadAd();

    void show();

    void destroy();

    boolean isReady();

    void onResume();

    void onPause();
}
