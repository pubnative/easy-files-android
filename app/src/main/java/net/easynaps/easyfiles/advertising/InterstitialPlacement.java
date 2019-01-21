package net.easynaps.easyfiles.advertising;

public interface InterstitialPlacement {
    void loadAd();

    void show();

    void destroy();

    boolean isReady();
}
