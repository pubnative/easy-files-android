package net.easynaps.easyfiles.activities

import net.easynaps.easyfiles.activities.superclasses.ThemedActivity
import android.preference.PreferenceFragment
import android.os.Parcelable
import net.easynaps.easyfiles.activities.PreferencesActivity
import android.os.Bundle
import net.easynaps.easyfiles.R
import net.easynaps.easyfiles.fragments.preference_fragments.ColorPref
import android.content.Intent
import net.easynaps.easyfiles.activities.MainActivity
import android.view.MenuItem
import android.app.Activity
import android.os.Build.VERSION
import android.app.ActivityManager.TaskDescription
import android.graphics.drawable.BitmapDrawable
import net.easynaps.easyfiles.utils.color.ColorUsage
import com.readystatesoftware.systembartint.SystemBarTintManager
import android.view.ViewGroup.MarginLayoutParams
import android.view.View
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants
import android.view.WindowManager
import net.easynaps.easyfiles.utils.PreferenceUtils
import android.graphics.Color
import net.easynaps.easyfiles.utils.theme.AppTheme
import java.lang.NullPointerException
import net.easynaps.easyfiles.fragments.preference_fragments.PrefFrag
import net.easynaps.easyfiles.fragments.preference_fragments.FoldersPref
import net.easynaps.easyfiles.fragments.preference_fragments.QuickAccessPref
import net.easynaps.easyfiles.fragments.preference_fragments.AdvancedSearchPref
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import net.easynaps.easyfiles.utils.Utils

class PreferencesActivity : ThemedActivity() {
    var restartActivity = false
        private set

    //The preference fragment currently selected
    private var selectedItem = 0
    private var currentFragment: PreferenceFragment? = null
    private val fragmentsListViewParcelables = arrayOfNulls<Parcelable>(NUMBER_OF_PREFERENCES)

    //private MoPubView mBannerView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prefsfrag)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        invalidateRecentsColorAndIcon()
        setSupportActionBar(toolbar)
        supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        invalidateToolbarColor()
        invalidateNavBar()
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt(KEY_CURRENT_FRAG_OPEN, 0)
        } else if (intent.extras != null) {
            selectItem(intent.extras!!.getInt(KEY_CURRENT_FRAG_OPEN))
        } else {
            selectItem(0)
        }

        // todo set a Hybid Banner here
        /*mBannerView = findViewById(R.id.banner_mopub);
        mBannerView.setBannerAdListener(this);
        mBannerView.setAutorefreshEnabled(false);*/loadAd()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_FRAG_OPEN, selectedItem)
    }

    override fun onBackPressed() {
        if (currentFragment is ColorPref) {
            if ((currentFragment as ColorPref).onBackPressed()) return
        }
        if (selectedItem != START_PREFERENCE && restartActivity) {
            restartActivity(this)
        } else if (selectedItem != START_PREFERENCE) {
            selectItem(START_PREFERENCE)
        } else {
            val `in` = Intent(this@PreferencesActivity, MainActivity::class.java)
            `in`.action = Intent.ACTION_MAIN
            `in`.action = Intent.CATEGORY_LAUNCHER
            this.startActivity(`in`)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //mBannerView.destroy();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (currentFragment!!.onOptionsItemSelected(item)) return true
                if (selectedItem != START_PREFERENCE && restartActivity) {
                    restartActivity(this)
                } else if (selectedItem != START_PREFERENCE) {
                    selectItem(START_PREFERENCE)
                } else {
                    val `in` = Intent(this@PreferencesActivity, MainActivity::class.java)
                    `in`.action = Intent.ACTION_MAIN
                    `in`.action = Intent.CATEGORY_LAUNCHER
                    val enter_anim = android.R.anim.fade_in
                    val exit_anim = android.R.anim.fade_out
                    val activity: Activity = this
                    activity.overridePendingTransition(enter_anim, exit_anim)
                    activity.finish()
                    activity.overridePendingTransition(enter_anim, exit_anim)
                    activity.startActivity(`in`)
                }
                return true
            }
        }
        return false
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
    fun saveListViewState(prefFragment: Int, listViewState: Parcelable?) {
        fragmentsListViewParcelables[prefFragment] = listViewState
    }

    /**
     * This is a hack see [PreferencesActivity.saveListViewState]
     */
    fun restoreListViewState(prefFragment: Int): Parcelable? {
        return fragmentsListViewParcelables[prefFragment]
    }

    fun setRestartActivity() {
        restartActivity = true
    }

    fun invalidateRecentsColorAndIcon() {
        if (VERSION.SDK_INT >= 21) {
            val taskDescription = TaskDescription(
                "EasyFiles",
                (resources.getDrawable(R.mipmap.ic_launcher) as BitmapDrawable).bitmap,
                colorPreference.getColor(ColorUsage.getPrimary(MainActivity.currentTab))
            )
            setTaskDescription(taskDescription)
        }
    }

    fun invalidateToolbarColor() {
        supportActionBar!!.setBackgroundDrawable(
            colorPreference.getDrawable(
                ColorUsage.getPrimary(
                    MainActivity.currentTab
                )
            )
        )
    }

    fun invalidateNavBar() {
        if (VERSION.SDK_INT == 20 || VERSION.SDK_INT == 19) {
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setStatusBarTintColor(
                colorPreference.getColor(
                    ColorUsage.getPrimary(
                        MainActivity.currentTab
                    )
                )
            )
            val p = findViewById<View>(R.id.preferences).layoutParams as MarginLayoutParams
            val config = tintManager.config
            p.setMargins(0, config.statusBarHeight, 0, 0)
        } else if (VERSION.SDK_INT >= 21) {
            val Sp = PreferenceManager.getDefaultSharedPreferences(this)
            val colourednavigation = getBoolean(PreferencesConstants.PREFERENCE_COLORED_NAVIGATION)
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val tabStatusColor = PreferenceUtils.getStatusColor(
                colorPreference.getColorAsString(
                    ColorUsage.getPrimary(MainActivity.currentTab)
                )
            )
            window.statusBarColor = tabStatusColor
            if (colourednavigation) {
                window.navigationBarColor = tabStatusColor
            } else if (window.navigationBarColor != Color.BLACK) {
                window.navigationBarColor = Color.BLACK
            }
        }
        if (appTheme == AppTheme.BLACK) window.decorView.setBackgroundColor(
            Utils.getColor(
                this,
                android.R.color.black
            )
        )
    }

    /**
     * This 'elegantly' destroys the activity and recreates it so that the different widgets and texts
     * change their inner states's colors.
     */
    fun restartActivity(activity: Activity?) {
        if (activity == null) throw NullPointerException()
        val enter_anim = android.R.anim.fade_in
        val exit_anim = android.R.anim.fade_out
        activity.overridePendingTransition(enter_anim, exit_anim)
        activity.finish()
        activity.overridePendingTransition(enter_anim, exit_anim)
        if (selectedItem != START_PREFERENCE) {
            val i = activity.intent
            i.putExtra(KEY_CURRENT_FRAG_OPEN, selectedItem)
        }
        activity.startActivity(activity.intent)
    }

    /**
     * When a Preference (that requires an independent fragment) is selected this is called.
     * @param item the Preference in question
     */
    fun selectItem(item: Int) {
        selectedItem = item
        when (item) {
            START_PREFERENCE -> loadPrefFragment(PrefFrag(), R.string.setting)
            COLORS_PREFERENCE -> loadPrefFragment(ColorPref(), R.string.color_title)
            FOLDERS_PREFERENCE -> loadPrefFragment(FoldersPref(), R.string.sidebarfolders_title)
            QUICKACCESS_PREFERENCE -> loadPrefFragment(
                QuickAccessPref(),
                R.string.sidebarquickaccess_title
            )
            ADVANCEDSEARCH_PREFERENCE -> loadPrefFragment(
                AdvancedSearchPref(),
                R.string.advanced_search
            )
        }
    }

    private fun loadPrefFragment(fragment: PreferenceFragment, @StringRes titleBarName: Int) {
        currentFragment = fragment
        val t = fragmentManager.beginTransaction()
        t.replace(R.id.prefsfragment, fragment)
        t.commit()
        supportActionBar!!.setTitle(titleBarName)
    }

    private fun loadAd() {
        /*mBannerView.setAdUnitId(getString(R.string.mopub_banner_ad_unit_id));
        mBannerView.loadAd();*/
    } /*@Override
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

    }*/

    companion object {
        private val TAG = PreferencesActivity::class.java.simpleName

        //Start is the first activity you see
        const val START_PREFERENCE = 0
        const val COLORS_PREFERENCE = 1
        const val FOLDERS_PREFERENCE = 2
        const val QUICKACCESS_PREFERENCE = 3
        const val ADVANCEDSEARCH_PREFERENCE = 4
        private const val KEY_CURRENT_FRAG_OPEN = "current_frag_open"
        private const val NUMBER_OF_PREFERENCES = 5
    }
}