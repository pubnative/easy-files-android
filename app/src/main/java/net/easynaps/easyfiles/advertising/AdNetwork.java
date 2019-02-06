package net.easynaps.easyfiles.advertising;

public enum AdNetwork {
    PUBNATIVE("PubNative"),
    APPLOVIN("AppLovin"),
    IRONSOURCE("IronSource"),
    FYBER("Fyber"),
    FACEBOOK("Facebook"),
    MOPUB("MoPub"),
    GOOGLE_ADS_MANAGER("GoogleAdsManager"),
    ADMOB("Admob"),
    STARTAPP("StartApp"),
    IMA("InteractiveMediaAds"),
    UNITY("UnityAds");

    private String name;

    public String getName() {
        return this.name;
    }

    AdNetwork(String name) {
        this.name = name;
    }
}
