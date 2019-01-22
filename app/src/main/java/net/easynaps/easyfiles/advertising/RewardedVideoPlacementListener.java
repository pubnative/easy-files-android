package net.easynaps.easyfiles.advertising;

public interface RewardedVideoPlacementListener {
    void onVideoLoaded();

    void onVideoError(Throwable error);

    void onVideoOpened();

    void onVideoClosed();

    void onVideoStarted();

    void onVideoCompleted();

    void onAdClicked();

    void onReward(AdReward reward);
}
