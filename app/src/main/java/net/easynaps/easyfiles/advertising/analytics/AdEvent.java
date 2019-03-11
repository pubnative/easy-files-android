package net.easynaps.easyfiles.advertising.analytics;

import net.easynaps.easyfiles.advertising.AdNetwork;
import net.easynaps.easyfiles.advertising.AdType;

public class AdEvent {
    public static final String EVENT_START_LOADING = "ad_start_loading";
    public static final String EVENT_LOADED = "ad_loaded";
    public static final String EVENT_LOAD_ERROR = "ad_load_error";
    public static final String EVENT_IMPRESSION = "ad_impression";
    public static final String EVENT_CLICK = "ad_click";
    public static final String EVENT_OPENED = "ad_opened";
    public static final String EVENT_CLOSED = "ad_closed";
    public static final String EVENT_LEFT_APP = "ad_left_app";
    public static final String EVENT_INTERSTITIAL_SHOW = "ad_interstitial_show";
    public static final String EVENT_INTERSTITIAL_SHOWN = "ad_interstitial_shown";
    public static final String EVENT_INTERSTITIAL_SHOW_ERROR = "ad_interstitial_show_error";
    public static final String EVENT_INTERSTITIAL_DISMISSED = "ad_interstitial_dismissed";
    public static final String EVENT_AUDIO_STARTED = "ad_audio_started";
    public static final String EVENT_AUDIO_FINISHED = "ad_audio_finished";
    public static final String EVENT_VIDEO_STARTED = "ad_video_started";
    public static final String EVENT_VIDEO_FINISHED = "ad_video_finished";
    public static final String EVENT_VIDEO_INCOMPLETE = "ad_video_incomplete";
    public static final String EVENT_VIDEO_ERROR = "ad_video_error";
    public static final String EVENT_REWARD = "ad_reward";
    public static final String EVENT_REWARD_REJECTED = "ad_reward_rejected";
    public static final String EVENT_DECLINED_TO_VIEW_AD = "ad_declined_to_view";
    public static final String EVENT_USER_OVER_QUOTA = "ad_user_over_quota";
    public static final String EVENT_VALIDATION_REQUEST_FAILED = "ad_validation_request_failed";


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
