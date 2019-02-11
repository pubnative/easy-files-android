package net.easynaps.easyfiles.advertising;

import android.app.Activity;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.applovin.adview.AppLovinAdView;
import com.applovin.sdk.AppLovinAdSize;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.heyzap.sdk.ads.BannerAdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.mopub.mobileads.MoPubView;
import com.startapp.android.publish.ads.banner.Mrec;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.advertising.admob.AdmobMRectController;
import net.easynaps.easyfiles.advertising.applovin.AppLovinMRectController;
import net.easynaps.easyfiles.advertising.facebook.FacebookMRectController;
import net.easynaps.easyfiles.advertising.fyber.FyberMRectController;
import net.easynaps.easyfiles.advertising.googleadmanager.GoogleMRectController;
import net.easynaps.easyfiles.advertising.ironsource.IronSourceMRectController;
import net.easynaps.easyfiles.advertising.mopub.MoPubMRectController;
import net.easynaps.easyfiles.advertising.pubnative.PubNativeMRectController;
import net.easynaps.easyfiles.advertising.startapp.StartAppMRectController;
import net.easynaps.easyfiles.utils.Utils;
import net.pubnative.lite.sdk.views.HyBidMRectAdView;

public class MRectPlacementFactory {
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
            default:
                return null;
        }
    }

    private AdPlacement createPubNativePlacement(Activity context, AdPlacementListener listener) {
        HyBidMRectAdView adView = new HyBidMRectAdView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 300), Utils.dpToPx(context, 250));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new PubNativeMRectController(adView,
                context.getString(R.string.pnlite_mrect_zone_id), listener);
    }

    private AdPlacement createAppLovinPlacement(Activity context, AdPlacementListener listener) {
        AppLovinAdView adview = new AppLovinAdView(AppLovinAdSize.MREC, context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, AppLovinAdSize.MREC.getWidth()), Utils.dpToPx(context, AppLovinAdSize.MREC.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adview.setLayoutParams(layoutParams);

        return new AppLovinMRectController(adview, listener);
    }

    private AdPlacement createIronSourcePlacement(Activity context, AdPlacementListener listener) {
        IronSourceBannerLayout adView = IronSource.createBanner(context, ISBannerSize.RECTANGLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, ISBannerSize.RECTANGLE.getWidth()), Utils.dpToPx(context, ISBannerSize.RECTANGLE.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new IronSourceMRectController(adView, "PLACEMENT_ID", listener);
    }

    private AdPlacement createFyberPlacement(Activity context, AdPlacementListener listener) {
        BannerAdView adView = new BannerAdView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 300), Utils.dpToPx(context, 250));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new FyberMRectController(adView, listener);
    }

    private AdPlacement createFacebookPlacement(Activity context, AdPlacementListener listener) {
        AdView adView = new AdView(context, "252067689023951_252074882356565", AdSize.RECTANGLE_HEIGHT_250);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, AdSize.RECTANGLE_HEIGHT_250.getWidth()), Utils.dpToPx(context, AdSize.RECTANGLE_HEIGHT_250.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new FacebookMRectController(adView, listener);
    }

    private AdPlacement createMoPubPlacement(Activity context, AdPlacementListener listener) {
        MoPubView adView = new MoPubView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, 300), Utils.dpToPx(context, 250));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new MoPubMRectController(adView, context.getString(R.string.mopub_mrect_ad_unit_id), listener);
    }

    private AdPlacement createGooglePlacement(Activity context, AdPlacementListener listener) {
        PublisherAdView adView = new PublisherAdView(context);
        adView.setAdSizes(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE.getWidth()),
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new GoogleMRectController(adView, "/219576711/EasyFilesMrect   size (300, 250)", listener);
    }

    private AdPlacement createStartAppPlacement(Activity context, AdPlacementListener listener) {
        Mrec adView = new Mrec(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Utils.dpToPx(context, 300), Utils.dpToPx(context, 250));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new StartAppMRectController(adView, listener);
    }

    private AdPlacement createAdmobPlacement(Activity context, AdPlacementListener listener) {
        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(context);
        adView.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE.getWidth()),
                Utils.dpToPx(context, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE.getHeight()));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(layoutParams);

        return new AdmobMRectController(adView, "PLACEMENT_ID", listener);
    }
}
