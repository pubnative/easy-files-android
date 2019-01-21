package net.easynaps.easyfiles.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.superclasses.BasicActivity;
import net.easynaps.easyfiles.advertising.AdManager;
import net.easynaps.easyfiles.advertising.AdPlacement;
import net.easynaps.easyfiles.advertising.AdPlacementListener;
import net.easynaps.easyfiles.advertising.EasyFilesAdConstants;
import net.easynaps.easyfiles.advertising.MRectPlacementFactory;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.theme.AppTheme;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.PrebidUtils;

public class AboutActivity extends BasicActivity implements View.OnClickListener, /*MoPubView.BannerAdListener,*/ AdPlacementListener {

    private static final String TAG = "AboutActivity";

    private static final int HEADER_HEIGHT = 1024;
    private static final int HEADER_WIDTH = 500;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mTitleTextView;
    private int mCount = 0;
    private Snackbar snackbar;
    private SharedPreferences mSharedPref;

    //private MoPubView mMRectView;
    private AdPlacement mAdPlacement;
    private FrameLayout mAdContainer;

    private static final String KEY_PREF_STUDIO = "studio";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getAppTheme().equals(AppTheme.DARK)) {
            setTheme(R.style.aboutDark);
        } else if (getAppTheme().equals(AppTheme.BLACK)) {
            setTheme(R.style.aboutBlack);
        } else {
            setTheme(R.style.aboutLight);
        }

        setContentView(R.layout.activity_about);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mAppBarLayout = findViewById(R.id.appBarLayout);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        mTitleTextView = findViewById(R.id.text_view_title);

        mAppBarLayout.setLayoutParams(calculateHeaderViewParams());

        Toolbar mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.md_nav_back));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.easyfiles_about_header);

        // It will generate colors based on the image in an AsyncTask.
        Palette.from(bitmap).generate(palette -> {
            int mutedColor = palette.getMutedColor(Utils.getColor(AboutActivity.this, R.color.primary_blue));
            int darkMutedColor = palette.getDarkMutedColor(Utils.getColor(AboutActivity.this, R.color.primary_blue));
            mCollapsingToolbarLayout.setContentScrimColor(mutedColor);
            mCollapsingToolbarLayout.setStatusBarScrimColor(darkMutedColor);
        });

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            mTitleTextView.setAlpha(Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()));
        });

        IronSource.init(this, "86721205", IronSource.AD_UNIT.BANNER);

        /*mMRectView = findViewById(R.id.mrect_mopub);
        mMRectView.setBannerAdListener(this);
        mMRectView.setAutorefreshEnabled(false);*/

        mAdContainer = findViewById(R.id.mrect_container);

        loadAd();
    }

    /**
     * Calculates aspect ratio for the Amaze header
     *
     * @return the layout params with new set of width and height attribute
     */
    private CoordinatorLayout.LayoutParams calculateHeaderViewParams() {

        // calculating cardview height as per the youtube video thumb aspect ratio
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        float vidAspectRatio = (float) HEADER_WIDTH / (float) HEADER_HEIGHT;
        Log.d(TAG, vidAspectRatio + "");
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        float reqHeightAsPerAspectRatio = (float) screenWidth * vidAspectRatio;
        Log.d(TAG, reqHeightAsPerAspectRatio + "");


        Log.d(TAG, "new width: " + screenWidth + " and height: " + reqHeightAsPerAspectRatio);

        layoutParams.width = screenWidth;
        layoutParams.height = (int) reqHeightAsPerAspectRatio;
        return layoutParams;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_layout_version:
                mCount++;
                if (mCount >= 5) {
                    String text = getResources().getString(R.string.easter_egg_title) + " : " + mCount;

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.setText(text);
                    } else {
                        snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT);
                    }

                    snackbar.show();
                    mSharedPref.edit().putInt(KEY_PREF_STUDIO, Integer.parseInt(Integer.toString(mCount) + "000")).apply();
                } else {
                    mSharedPref.edit().putInt(KEY_PREF_STUDIO, 0).apply();
                }
                break;

            case R.id.relative_layout_licenses:
                LibsBuilder libsBuilder = new LibsBuilder()
                        .withLibraries("commonscompress", "apachemina", "volley")//Not autodetected for some reason
                        .withActivityTitle(getString(R.string.libraries))
                        .withAboutIconShown(true)
                        .withAboutVersionShownName(true)
                        .withAboutVersionShownCode(false)
                        .withAboutDescription(getString(R.string.about_easyfiles))
                        .withAboutSpecial1(getString(R.string.license))
                        .withAboutSpecial1Description(getString(R.string.amaze_license))
                        .withLicenseShown(true);

                switch (getAppTheme().getSimpleTheme()) {
                    case LIGHT:
                        libsBuilder.withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR);
                        break;
                    case DARK:
                        libsBuilder.withActivityStyle(Libs.ActivityStyle.DARK);
                        break;
                    case BLACK:
                        libsBuilder.withActivityTheme(R.style.AboutLibrariesTheme_Black);
                        break;
                }

                libsBuilder.start(this);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mMRectView.destroy();
        if (mAdPlacement != null) {
            mAdPlacement.destroy();
        }
    }

    private void loadAd() {
        mAdPlacement = new MRectPlacementFactory().createAdPlacement(this,
                AdManager.getInstance().getNextNetwork(EasyFilesAdConstants.PLACEMENT_MRECT_ABOUT),
                this);
        mAdPlacement.loadAd();
        //mMRectView.setAdUnitId(getString(R.string.mopub_mrect_ad_unit_id));
        //mMRectView.loadAd();
    }

    private void cleanupAd() {
        mAdContainer.setVisibility(View.GONE);
        mAdContainer.removeAllViews();
        mAdPlacement.destroy();
    }

    /*@Override
    public void onBannerLoaded(MoPubView banner) {
        mMRectView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Log.e(TAG, errorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        mMRectView.setVisibility(View.GONE);
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }*/

    @Override
    public void onAdLoaded() {
        Log.d(TAG, "onAdLoaded");
        mAdContainer.removeAllViews();
        mAdContainer.addView(mAdPlacement.getAdView());
        mAdContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAdClicked() {
        Log.d(TAG, "onAdClicked");
        cleanupAd();
        loadAd();
    }

    @Override
    public void onAdError(Throwable error) {
        Log.e(TAG, error.getMessage());
    }
}
