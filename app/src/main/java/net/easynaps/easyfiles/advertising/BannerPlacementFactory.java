package net.easynaps.easyfiles.advertising;

import android.app.Activity;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.applovin.adview.AppLovinAdView;
import com.applovin.sdk.AppLovinAdSize;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.heyzap.sdk.ads.BannerAdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.mopub.mobileads.MoPubView;
import com.startapp.android.publish.ads.banner.Banner;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.advertising.admob.AdmobBannerController;
import net.easynaps.easyfiles.advertising.applovin.AppLovinBannerController;
import net.easynaps.easyfiles.advertising.appodeal.AppodealBannerController;
import net.easynaps.easyfiles.advertising.facebook.FacebookBannerController;
import net.easynaps.easyfiles.advertising.fyber.FyberBannerController;
import net.easynaps.easyfiles.advertising.googleadmanager.GoogleBannerController;
import net.easynaps.easyfiles.advertising.ironsource.IronSourceBannerController;
import net.easynaps.easyfiles.advertising.mopub.MoPubBannerController;
import net.easynaps.easyfiles.advertising.pubnative.PubNativeBannerController;
import net.easynaps.easyfiles.advertising.startapp.StartAppBannerController;
import net.easynaps.easyfiles.advertising.unity.UnityAdsBannerController;
import net.easynaps.easyfiles.utils.Utils;
import net.pubnative.lite.sdk.views.HyBidBannerAdView;

public class BannerPlacementFactory {
    public AdPlacement createAdPlacement(Activity context, AdNetwork adNetwork, AdPlacementListener listener) {
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
            case STARTAPP:
                return createStartAppPlacement(context, listener);
            case UNITY:
                return createUnityPlacement(context, listener);
            case APPODEAL:
                return createAppodealPlacement(context, listener);
            default:
                return null;
        }
    }

    private AdPlacement createPubNativePlacement(Activity context, AdPlacementListener listener) {
        HyBidBannerAdView adView = new HyBidBannerAdView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 320), Utils.dpToPx(context, 50));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new PubNativeBannerController(adView,
                context.getString(R.string.pnlite_banner_zone_id), listener);
    }

    private AdPlacement createAppLovinPlacement(Activity context, AdPlacementListener listener) {
        AppLovinAdView adview = new AppLovinAdView(AppLovinAdSize.BANNER, context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, AppLovinAdSize.BANNER.getWidth()), Utils.dpToPx(context, AppLovinAdSize.BANNER.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adview.setLayoutParams(layoutParams);

        return new AppLovinBannerController(adview, listener);
    }

    private AdPlacement createIronSourcePlacement(Activity context, AdPlacementListener listener) {
        IronSourceBannerLayout adView = IronSource.createBanner(context, ISBannerSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, ISBannerSize.BANNER.getWidth()), Utils.dpToPx(context, ISBannerSize.BANNER.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new IronSourceBannerController(adView, "EasyFilesBanner", listener);
    }

    private AdPlacement createFyberPlacement(Activity context, AdPlacementListener listener) {
        BannerAdView adView = new BannerAdView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 320), Utils.dpToPx(context, 50));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new FyberBannerController(adView, listener);
    }

    private AdPlacement createFacebookPlacement(Activity context, AdPlacementListener listener) {
        AdView adView = new AdView(context, "PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, AdSize.BANNER_HEIGHT_50.getWidth()), Utils.dpToPx(context, AdSize.BANNER_HEIGHT_50.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new FacebookBannerController(adView, listener);
    }

    private AdPlacement createMoPubPlacement(Activity context, AdPlacementListener listener) {
        MoPubView adView = new MoPubView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 320), Utils.dpToPx(context, 50));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new MoPubBannerController(adView, context.getString(R.string.mopub_banner_ad_unit_id), listener);
    }

    private AdPlacement createGooglePlacement(Activity context, AdPlacementListener listener) {
        PublisherAdView adView = new PublisherAdView(context);
        adView.setAdSizes(com.google.android.gms.ads.AdSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.BANNER.getWidth()),
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.BANNER.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new GoogleBannerController(adView, "/219576711/EasyFilesBanner   size (320, 50)", listener);
    }

    private AdPlacement createAdmobPlacement(Activity context, AdPlacementListener listener) {
        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(context);
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.BANNER.getWidth()),
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.BANNER.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new AdmobBannerController(adView, "ca-app-pub-9176690371168943/7642028987", listener);
    }

    private AdPlacement createStartAppPlacement(Activity context, AdPlacementListener listener) {
        Banner adView = new Banner(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Utils.dpToPx(context, 320), Utils.dpToPx(context, 50));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new StartAppBannerController(adView, listener);
    }

    private AdPlacement createUnityPlacement(Activity context, AdPlacementListener listener) {
        return new UnityAdsBannerController(context, "3024006", "banner", listener);
    }

    private AdPlacement createAppodealPlacement(Activity context, AdPlacementListener listener){
        BannerView adView = Appodeal.getBannerView(context);
        return new AppodealBannerController(adView, listener, context);
    }

}
