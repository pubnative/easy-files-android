package net.easynaps.easyfiles.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.superclasses.ThemedActivity;
import net.easynaps.easyfiles.fragments.preference_fragments.AdvancedSearchPref;
import net.easynaps.easyfiles.fragments.preference_fragments.ColorPref;
import net.easynaps.easyfiles.fragments.preference_fragments.FoldersPref;
import net.easynaps.easyfiles.fragments.preference_fragments.PrefFrag;
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants;
import net.easynaps.easyfiles.fragments.preference_fragments.QuickAccessPref;
import net.easynaps.easyfiles.utils.PreferenceUtils;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.theme.AppTheme;
import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.PrebidUtils;

import static android.os.Build.VERSION.SDK_INT;

public class PreferencesActivity extends ThemedActivity implements MoPubView.BannerAdListener {
    private static final String TAG = PreferencesActivity.class.getSimpleName();

    //Start is the first activity you see
    public static final int START_PREFERENCE = 0;
    public static final int COLORS_PREFERENCE = 1;
    public static final int FOLDERS_PREFERENCE = 2;
    public static final int QUICKACCESS_PREFERENCE = 3;
    public static final int ADVANCEDSEARCH_PREFERENCE = 4;

    private boolean restartActivity = false;
    //The preference fragment currently selected
    private int selectedItem = 0;

    private PreferenceFragment currentFragment;

    private static final String KEY_CURRENT_FRAG_OPEN = "current_frag_open";
    private static final int NUMBER_OF_PREFERENCES = 5;

    private Parcelable[] fragmentsListViewParcelables = new Parcelable[NUMBER_OF_PREFERENCES];

    private MoPubView mBannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefsfrag);

        Toolbar toolbar = findViewById(R.id.toolbar);
        invalidateRecentsColorAndIcon();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        invalidateToolbarColor();
        invalidateNavBar();

        if (savedInstanceState != null){
            selectedItem = savedInstanceState.getInt(KEY_CURRENT_FRAG_OPEN, 0);
        } else if(getIntent().getExtras() != null) {
            selectItem(getIntent().getExtras().getInt(KEY_CURRENT_FRAG_OPEN));
        } else {
            selectItem(0);
        }

        mBannerView = findViewById(R.id.banner_mopub);
        mBannerView.setBannerAdListener(this);
        mBannerView.setAutorefreshEnabled(false);

        loadAd();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_FRAG_OPEN, selectedItem);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof ColorPref) {
            if(((ColorPref) currentFragment).onBackPressed()) return;
        }

        if (selectedItem != START_PREFERENCE && restartActivity) {
            restartActivity(this);
        } else if (selectedItem != START_PREFERENCE) {
            selectItem(START_PREFERENCE);
        } else {
            Intent in = new Intent(PreferencesActivity.this, MainActivity.class);
            in.setAction(Intent.ACTION_MAIN);
            in.setAction(Intent.CATEGORY_LAUNCHER);
            this.startActivity(in);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBannerView.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(currentFragment.onOptionsItemSelected(item)) return true;

                if (selectedItem != START_PREFERENCE && restartActivity) {
                    restartActivity(this);
                } else if (selectedItem != START_PREFERENCE) {
                    selectItem(START_PREFERENCE);
                } else {
                    Intent in = new Intent(PreferencesActivity.this, MainActivity.class);
                    in.setAction(Intent.ACTION_MAIN);
                    in.setAction(Intent.CATEGORY_LAUNCHER);

                    final int enter_anim = android.R.anim.fade_in;
                    final int exit_anim = android.R.anim.fade_out;
                    Activity activity = this;
                    activity.overridePendingTransition(enter_anim, exit_anim);
                    activity.finish();
                    activity.overridePendingTransition(enter_anim, exit_anim);
                    activity.startActivity(in);
                }
                return true;
        }
        return false;
    }

    /**
     * This is a hack, each PreferenceFragment has a ListView that loses it's state (specifically
     * the scrolled position) when the user accesses another PreferenceFragment. To prevent this, the
     * Activity saves the ListView's state, so that it can be restored when the user returns to the
     * PreferenceFragment.
     *
     * We cannot use the normal save/restore state functions because they only get called when the
     * OS kills the fragment, not the user. See https://stackoverflow.com/a/12793395/3124150 for a
     * better explanation.
     *
     * We cannot save the Parcelable in the fragment because the fragment is destroyed.
     */
    public void saveListViewState(int prefFragment, Parcelable listViewState) {
        fragmentsListViewParcelables[prefFragment] = listViewState;
    }

    /**
     * This is a hack see {@link PreferencesActivity#saveListViewState(int, Parcelable)}
     */
    public Parcelable restoreListViewState(int prefFragment) {
        return fragmentsListViewParcelables[prefFragment];
    }

    public void setRestartActivity() {
        restartActivity = true;
    }

    public boolean getRestartActivity() {
        return restartActivity;
    }

    public void invalidateRecentsColorAndIcon() {
        if (SDK_INT >= 21) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("EasyFiles",
                    ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap(),
                    getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
            setTaskDescription(taskDescription);
        }
    }

    public void invalidateToolbarColor() {
        getSupportActionBar().setBackgroundDrawable(getColorPreference().getDrawable(ColorUsage.getPrimary(MainActivity.currentTab)));
    }

    public void invalidateNavBar() {
        if (SDK_INT == 20 || SDK_INT == 19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));

            FrameLayout.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) findViewById(R.id.preferences).getLayoutParams();
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            p.setMargins(0, config.getStatusBarHeight(), 0, 0);
        } else if (SDK_INT >= 21) {
            SharedPreferences Sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean colourednavigation = Sp.getBoolean(PreferencesConstants.PREFERENCE_COLORED_NAVIGATION, true);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int tabStatusColor = PreferenceUtils.getStatusColor(getColorPreference().getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab)));
            window.setStatusBarColor(tabStatusColor);
            if (colourednavigation) {
                window.setNavigationBarColor(tabStatusColor);
            } else if(window.getNavigationBarColor() != Color.BLACK){
                window.setNavigationBarColor(Color.BLACK);
            }
        }

        if (getAppTheme().equals(AppTheme.BLACK)) getWindow().getDecorView().setBackgroundColor(Utils.getColor(this, android.R.color.black));
    }

    /**
     * This 'elegantly' destroys the activity and recreates it so that the different widgets and texts
     * change their inner states's colors.
     */
    public void restartActivity(final Activity activity) {
        if (activity == null) throw new NullPointerException();

        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        if(selectedItem != START_PREFERENCE) {
            Intent i = activity.getIntent();
            i.putExtra(KEY_CURRENT_FRAG_OPEN, selectedItem);
        }
        activity.startActivity(activity.getIntent());
    }

    /**
     * When a Preference (that requires an independent fragment) is selected this is called.
     * @param item the Preference in question
     */
    public void selectItem(int item) {
        selectedItem = item;
        switch (item) {
            case START_PREFERENCE:
                loadPrefFragment(new PrefFrag(), R.string.setting);
                break;
            case COLORS_PREFERENCE:
                loadPrefFragment(new ColorPref(), R.string.color_title);
                break;
            case FOLDERS_PREFERENCE:
                loadPrefFragment(new FoldersPref(), R.string.sidebarfolders_title);
                break;
            case QUICKACCESS_PREFERENCE:
                loadPrefFragment(new QuickAccessPref(), R.string.sidebarquickaccess_title);
                break;
            case ADVANCEDSEARCH_PREFERENCE:
                loadPrefFragment(new AdvancedSearchPref(), R.string.advanced_search);
                break;
        }
    }

    private void loadPrefFragment(PreferenceFragment fragment, @StringRes int titleBarName) {
        currentFragment = fragment;

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.prefsfragment, fragment);
        t.commit();
        getSupportActionBar().setTitle(titleBarName);
    }

    private void loadAd() {
        RequestManager bannerRequestManager = new BannerRequestManager();
        bannerRequestManager.setZoneId(getString(R.string.pnlite_banner_zone_id));
        bannerRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                mBannerView.setAdUnitId(getString(R.string.mopub_banner_ad_unit_id));
                mBannerView.setKeywords(PrebidUtils.getPrebidKeywords(ad, getString(R.string.pnlite_banner_zone_id)));
                mBannerView.loadAd();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                mBannerView.setAdUnitId(getString(R.string.mopub_banner_ad_unit_id));
                mBannerView.loadAd();
            }
        });

        bannerRequestManager.requestAd();
    }

    @Override
    public void onBannerLoaded(MoPubView banner) {
        mBannerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Log.e(TAG, errorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        mBannerView.setVisibility(View.GONE);
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }
}
