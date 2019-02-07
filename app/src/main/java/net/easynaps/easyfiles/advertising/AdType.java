package net.easynaps.easyfiles.advertising;

public enum AdType {
    BANNER("Banner"),
    MRECT("MRect"),
    INTERSTITIAL("Interstitial"),
    REWARDED_VIDEO("RewardedVideo");

    private String name;

    public String getName() {
        return this.name;
    }

    AdType(String name) {
        this.name = name;
    }
}
