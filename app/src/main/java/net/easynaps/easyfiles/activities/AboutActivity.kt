package net.easynaps.easyfiles.activities

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import net.easynaps.easyfiles.R
import net.easynaps.easyfiles.activities.superclasses.BasicActivity
import net.easynaps.easyfiles.utils.Utils
import net.easynaps.easyfiles.utils.theme.AppTheme
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod
import net.pubnative.lite.sdk.views.HyBidAdView
import kotlin.math.abs

class AboutActivity : BasicActivity(), View.OnClickListener, HyBidAdView.Listener {

    private var mAppBarLayout: AppBarLayout? = null
    private var mCollapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var mTitleTextView: TextView? = null
    private var mCount = 0
    private var snackbar: Snackbar? = null
    private var mSharedPref: SharedPreferences? = null

    private lateinit var hybidBanner: HyBidAdView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        when (appTheme) {
            AppTheme.DARK -> {
                setTheme(R.style.aboutDark)
            }
            AppTheme.BLACK -> {
                setTheme(R.style.aboutBlack)
            }
            else -> {
                setTheme(R.style.aboutLight)
            }
        }

        setContentView(R.layout.activity_about)

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mAppBarLayout = findViewById(R.id.appBarLayout)
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)
        mTitleTextView = findViewById(R.id.text_view_title)
        hybidBanner = findViewById(R.id.hybid_banner)

        mAppBarLayout?.layoutParams = calculateHeaderViewParams()
        val mToolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(resources.getDrawable(R.drawable.md_nav_back))
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.easyfiles_about_header
        )

        // It will generate colors based on the image in an AsyncTask.
        Palette.from(bitmap).generate { palette: Palette? ->
            val mutedColor =
                palette!!.getMutedColor(Utils.getColor(this@AboutActivity, R.color.primary_blue))
            val darkMutedColor =
                palette.getDarkMutedColor(Utils.getColor(this@AboutActivity, R.color.primary_blue))
            mCollapsingToolbarLayout?.setContentScrimColor(mutedColor)
            mCollapsingToolbarLayout?.setStatusBarScrimColor(darkMutedColor)
        }
        mAppBarLayout?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            mTitleTextView?.alpha = abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
        })

        loadPNAd()
    }

    /**
     * Calculates aspect ratio for the Amaze header
     * @return the layout params with new set of width and height attribute
     */
    private fun calculateHeaderViewParams(): CoordinatorLayout.LayoutParams {

        // calculating cardview height as per the youtube video thumb aspect ratio
        val layoutParams = mAppBarLayout!!.layoutParams as CoordinatorLayout.LayoutParams
        val vidAspectRatio = HEADER_WIDTH.toFloat() / HEADER_HEIGHT.toFloat()
        Log.d(TAG, vidAspectRatio.toString() + "")
        val screenWidth = resources.displayMetrics.widthPixels
        val reqHeightAsPerAspectRatio = screenWidth.toFloat() * vidAspectRatio
        Log.d(TAG, reqHeightAsPerAspectRatio.toString() + "")
        Log.d(TAG, "new width: $screenWidth and height: $reqHeightAsPerAspectRatio")
        layoutParams.width = screenWidth
        layoutParams.height = reqHeightAsPerAspectRatio.toInt()
        return layoutParams
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.relative_layout_version -> {
                mCount++
                if (mCount >= 5) {
                    val text = resources.getString(R.string.easter_egg_title) + " : " + mCount
                    if (snackbar != null && snackbar!!.isShown) {
                        snackbar!!.setText(text)
                    } else {
                        snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT)
                    }
                    snackbar!!.show()
                    mSharedPref!!.edit()
                        .putInt(KEY_PREF_STUDIO, (Integer.toString(mCount) + "000").toInt()).apply()
                } else {
                    mSharedPref!!.edit().putInt(KEY_PREF_STUDIO, 0).apply()
                }
            }
            R.id.relative_layout_licenses -> {
                val libsBuilder = LibsBuilder()
                    .withLibraries(
                        "commonscompress",
                        "apachemina",
                        "volley"
                    ) //Not autodetected for some reason
                    .withActivityTitle(getString(R.string.libraries))
                    .withAboutIconShown(true)
                    .withAboutVersionShownName(true)
                    .withAboutVersionShownCode(false)
                    .withAboutDescription(getString(R.string.about_easyfiles))
                    .withAboutSpecial1(getString(R.string.license))
                    .withAboutSpecial1Description(getString(R.string.amaze_license))
                    .withLicenseShown(true)
                when (appTheme.simpleTheme) {
                    AppTheme.LIGHT -> libsBuilder.withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    AppTheme.DARK -> libsBuilder.withActivityStyle(Libs.ActivityStyle.DARK)
                    AppTheme.BLACK -> libsBuilder.withActivityTheme(R.style.AboutLibrariesTheme_Black)
                    else -> {

                    }
                }
                libsBuilder.start(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //mMRectView.destroy();
    }

    fun loadPNAd() {

        val adSize = AdSize.SIZE_300x250

        hybidBanner.setAdSize(adSize)

        val layoutParams = LinearLayout.LayoutParams(
            convertDpToPx(adSize.width.toFloat()),
            convertDpToPx(adSize.height.toFloat())
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL

        hybidBanner.layoutParams = layoutParams

        hybidBanner.isAutoCacheOnLoad = true

        hybidBanner.setTrackingMethod(ImpressionTrackingMethod.AD_VIEWABLE)
        hybidBanner.load("5", this)
    }

    companion object {
        private const val TAG = "AboutActivity"
        private const val HEADER_HEIGHT = 1024
        private const val HEADER_WIDTH = 500

        //private MoPubView mMRectView;
        private const val KEY_PREF_STUDIO = "studio"
    }

    fun convertDpToPx(dp: Float) = (dp * resources.displayMetrics.density).toInt()

    override fun onAdLoaded() {
        hybidBanner.show()
    }

    override fun onAdLoadFailed(p0: Throwable?) {
        Log.d("error", p0.toString())
    }

    override fun onAdImpression() {
        Log.d("About", "onAdImpression)")
    }

    override fun onAdClick() {
        Log.d("About", "onAdClick")
    }
}