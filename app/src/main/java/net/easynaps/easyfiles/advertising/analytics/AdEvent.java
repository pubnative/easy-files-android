package net.easynaps.easyfiles.advertising.analytics;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;

public class AdEvent {
    public static final String EVENT_START_LOADING = "ad_start_loading";
    public static final String EVENT_LOADED = "ad_start_loading";
    public static final String EVENT_LOAD_ERROR = "ad_start_loading";
    public static final String EVENT_IMPRESSION = "ad_start_loading";
    public static final String EVENT_CLICK = "ad_click";
    public static final String EVENT_OPENED = "ad_opened";
    public static final String EVENT_CLOSED = "ad_closed";
    public static final String EVENT_LEFT_APP = "ad_left_app";


    private final String name;
    private final String type;
    private final String sdkName;
    private final long elapsedMilliseconds;

    public AdEvent(String name, AdType type, AdNetwork network, long elapsedMilliseconds) {
        this.name = name;
        this.type = type.getName();
        this.sdkName = network.getName();
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    public String getName() {
        return name;
    }

    public String getSdkName() {
        return sdkName;
    }

    public String getAdType() {
        return type;
    }

    public long getElapsedMilliseconds() {
        return elapsedMilliseconds;
    }
}
