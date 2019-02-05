package net.easynaps.easyfiles.advertising;

import android.app.Activity;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.advertising.admob.AdmobInterstitialController;
import net.easynaps.easyfiles.advertising.applovin.AppLovinInterstitialController;
import net.easynaps.easyfiles.advertising.facebook.FacebookInterstitialController;
import net.easynaps.easyfiles.advertising.fyber.FyberInterstitialController;
import net.easynaps.easyfiles.advertising.googleadmanager.GoogleInterstitialController;
import net.easynaps.easyfiles.advertising.ironsource.IronSourceInterstitialController;
import net.easynaps.easyfiles.advertising.mopub.MoPubInterstitialController;
import net.easynaps.easyfiles.advertising.pubnative.PubNativeInterstitialController;
import net.easynaps.easyfiles.advertising.unity.UnityAdsInterstitialController;

public class InterstitialPlacementFactory {
    public InterstitialPlacement createAdPlacement(Activity context, AdNetwork adNetwork, InterstitialPlacementListener listener) {
        switch (adNetwork) {
            case PUBNATIVE:
                return createPubNativePlacement(context, listener);
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
            case UNITY:
                return createUnityPlacement(context, listener);
            default:
                return null;
        }
    }

    private InterstitialPlacement createPubNativePlacement(Activity context, InterstitialPlacementListener listener) {
        return new PubNativeInterstitialController(context, context.getString(R.string.pnlite_interstitial_zone_id), listener);
    }

    private InterstitialPlacement createAppLovinPlacement(Activity context, InterstitialPlacementListener listener) {
        return new AppLovinInterstitialController(context, listener);
    }

    private InterstitialPlacement createIronSourcePlacement(Activity context, InterstitialPlacementListener listener) {
        return new IronSourceInterstitialController("EasyFilesInterstitial", listener);
    }

    private InterstitialPlacement createFyberPlacement(Activity context, InterstitialPlacementListener listener) {
        return new FyberInterstitialController(context, listener);
    }

    private InterstitialPlacement createFacebookPlacement(Activity context, InterstitialPlacementListener listener) {
        return new FacebookInterstitialController(context, "PLACEMENT_ID", listener);
    }

    private InterstitialPlacement createMoPubPlacement(Activity context, InterstitialPlacementListener listener) {
        return new MoPubInterstitialController(context, context.getString(R.string.mopub_interstitial_ad_unit_id), listener);
    }

    private InterstitialPlacement createGooglePlacement(Activity context, InterstitialPlacementListener listener) {
        return new GoogleInterstitialController(context, "/219576711/EasyFilesInterstitial size (768, 1024)", listener);
    }

    private InterstitialPlacement createAdmobPlacement(Activity context, InterstitialPlacementListener listener) {
        return new AdmobInterstitialController(context, "ca-app-pub-9176690371168943/2286648571", listener);
    }

    private InterstitialPlacement createUnityPlacement(Activity context, InterstitialPlacementListener listener) {
        return new UnityAdsInterstitialController(context, "3024006", "interstitial", listener);
    }
}
