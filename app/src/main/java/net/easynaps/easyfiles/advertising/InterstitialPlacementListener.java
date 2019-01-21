package net.easynaps.easyfiles.advertising;

public interface InterstitialPlacementListener {
    void onAdLoaded();

    void onAdError(Throwable error);

    void onAdShown();

    void onAdDismissed();

    void onAdClicked();
}
