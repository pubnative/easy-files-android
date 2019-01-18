package net.easynaps.easyfiles.advertising;

public interface AdPlacementListener {
    void onAdLoaded();

    void onAdError(Throwable error);

    void onAdClicked();
}
