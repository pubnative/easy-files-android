package net.easynaps.easyfiles.advertising;

import android.app.Activity;

import net.easynaps.easyfiles.advertising.admob.AdmobRewardedVideoController;
import net.easynaps.easyfiles.advertising.applovin.AppLovinRewardedVideoController;
import net.easynaps.easyfiles.advertising.facebook.FacebookRewardedVideoController;
import net.easynaps.easyfiles.advertising.fyber.FyberRewardedVideoController;
import net.easynaps.easyfiles.advertising.googleadmanager.GoogleRewardedVideoController;
import net.easynaps.easyfiles.advertising.ironsource.IronSourceRewardedVideoController;
import net.easynaps.easyfiles.advertising.mopub.MoPubRewardedVideoController;
import net.easynaps.easyfiles.advertising.startapp.StartAppRewardedController;
import net.easynaps.easyfiles.advertising.unity.UnityAdsRewardedController;

public class RewardedVideoPlacementFactory {
    public RewardedVideoPlacement createAdPlacement(Activity context, AdNetwork adNetwork, RewardedVideoPlacementListener listener) {
        switch (adNetwork) {
            case APPLOVIN:
                return createAppLovinPlacement(context, listener);
            case IRONSOURCE:
                return createIronSourcePlacement(context, listener);
            case FYBER:
                return createFyberPlacement(context, listener);
            case FACEBOOK:
                return createFacebookPlacement(context, listener);
            case MOPUB:
                return createMoPubPlacement(context, listener);
            case GOOGLE_ADS_MANAGER:
                return createGooglePlacement(context, listener);
            case ADMOB:
                return createAdmobPlacement(context, listener);
            case STARTAPP:
                return createStartAppPlacement(context, listener);
            case UNITY:
                return createUnityPlacement(context, listener);
            default:
                return null;
        }
    }

    private RewardedVideoPlacement createAppLovinPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new AppLovinRewardedVideoController(context, listener);
    }

    private RewardedVideoPlacement createIronSourcePlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new IronSourceRewardedVideoController(context, "EasyFilesRewardedVideo", listener);
    }

    private RewardedVideoPlacement createFyberPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new FyberRewardedVideoController(context, listener);
    }

    private RewardedVideoPlacement createFacebookPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new FacebookRewardedVideoController(context, "PLACEMENT_ID", listener);
    }

    private RewardedVideoPlacement createMoPubPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new MoPubRewardedVideoController(context, "5c91eec9ed0e47ea972ae8f6ae97d77c", listener);
    }

    private RewardedVideoPlacement createGooglePlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new GoogleRewardedVideoController(context, "AD_UNIT_ID", listener);
    }

    private RewardedVideoPlacement createAdmobPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new AdmobRewardedVideoController(context, "ca-app-pub-9176690371168943/1061536128", listener);
    }

    private RewardedVideoPlacement createStartAppPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new StartAppRewardedController(context, listener);
    }

    private RewardedVideoPlacement createUnityPlacement(Activity context, RewardedVideoPlacementListener listener) {
        return new UnityAdsRewardedController(context, "3024006", "rewardedVideo", listener);
    }
}
