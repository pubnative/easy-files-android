package net.easynaps.easyfiles.fragments.preference_fragments

import android.Manifest
import android.preference.PreferenceFragment
import android.preference.Preference.OnPreferenceClickListener
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider
import android.content.SharedPreferences
import android.widget.ListView
import android.os.Bundle
import net.easynaps.easyfiles.activities.superclasses.BasicActivity
import net.easynaps.easyfiles.R
import android.preference.PreferenceManager
import android.preference.Preference
import android.os.Build
import android.hardware.fingerprint.FingerprintManager
import android.app.KeyguardManager
import android.preference.Preference.OnPreferenceChangeListener
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Toast
import java.lang.NoClassDefFoundError
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice
import android.view.View
import net.easynaps.easyfiles.utils.theme.AppTheme
import android.content.Intent
import android.net.Uri
import net.easynaps.easyfiles.activities.PreferencesActivity
import net.easynaps.easyfiles.utils.files.CryptUtil
import java.security.GeneralSecurityException
import java.io.IOException
import com.afollestad.materialdialogs.MaterialDialog.InputCallback
import net.easynaps.easyfiles.utils.color.ColorUsage
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.afollestad.materialdialogs.DialogAction
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import net.easynaps.easyfiles.activities.AboutActivity
import net.easynaps.easyfiles.ui.views.preference.CheckBox

class PrefFrag() : PreferenceFragment(), OnPreferenceClickListener {

    private var utilsProvider: UtilitiesProvider? = null
    private var sharedPref: SharedPreferences? = null

    /**
     * This is a hack see [PreferencesActivity.saveListViewState]
     */
    private var listView: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utilsProvider = (activity as BasicActivity).utilsProvider

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        for (PREFERENCE_KEY: String? in PREFERENCE_KEYS) {
            findPreference(PREFERENCE_KEY).onPreferenceClickListener = this
        }

        // crypt master password
        val masterPasswordPreference =
            findPreference(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ||
            sharedPref?.getBoolean(PreferencesConstants.PREFERENCE_CRYPT_FINGERPRINT, false) == true
        ) {
            // encryption feature not available
            masterPasswordPreference.isEnabled = false
        }
        masterPasswordPreference.onPreferenceClickListener = this
        val checkBoxFingerprint =
            findPreference(PreferencesConstants.PREFERENCE_CRYPT_FINGERPRINT) as CheckBox
        try {

            // finger print sensor
            var fingerprintManager: FingerprintManager? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager =
                    activity.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            }
            val keyguardManager =
                activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (fingerprintManager != null) && fingerprintManager.isHardwareDetected) {
                checkBoxFingerprint.isEnabled = true
            }
            val finalFingerprintManager = fingerprintManager
            checkBoxFingerprint.onPreferenceChangeListener = object : OnPreferenceChangeListener {
                override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
                    if (ActivityCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.USE_FINGERPRINT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.crypt_fingerprint_no_permission),
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        !finalFingerprintManager!!.hasEnrolledFingerprints()
                    ) {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.crypt_fingerprint_not_enrolled),
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        !keyguardManager.isKeyguardSecure
                    ) {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.crypt_fingerprint_no_security),
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    }
                    masterPasswordPreference.isEnabled = false
                    return true
                }
            }
        } catch (error: NoClassDefFoundError) {
            error.printStackTrace()

            // fingerprint manager class not defined in the framework
            checkBoxFingerprint.isEnabled = false
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val sort: Array<String>
        val builder: MaterialDialog.Builder
        when (preference.key) {
            PreferencesConstants.PREFERENCE_GRID_COLUMNS -> {
                sort = resources.getStringArray(R.array.columns)
                builder = MaterialDialog.Builder(activity)
                builder.theme(utilsProvider!!.appTheme.materialDialogTheme)
                builder.title(R.string.gridcolumnno)
                var current =
                    sharedPref!!.getString(PreferencesConstants.PREFERENCE_GRID_COLUMNS, "-1")!!
                        .toInt()
                current = if (current == -1) 0 else current
                if (current != 0) current = current - 1
                builder.items(*sort).itemsCallbackSingleChoice(
                    current,
                    ListCallbackSingleChoice { dialog, view, which, text ->
                        sharedPref!!.edit().putString(
                            PreferencesConstants.PREFERENCE_GRID_COLUMNS,
                            "" + (if (which != 0) sort[which] else "" + -1)
                        ).commit()
                        dialog.dismiss()
                        true
                    })
                builder.build().show()
                return true
            }
            PreferencesConstants.FRAGMENT_THEME -> {
                sort = resources.getStringArray(R.array.theme)
                val current = sharedPref!!.getString(PreferencesConstants.FRAGMENT_THEME, "0")!!.toInt()
                builder = MaterialDialog.Builder(activity)
                //builder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                builder.items(*sort)
                    .itemsCallbackSingleChoice(current) { dialog, view, which, text ->
                        utilsProvider!!.themeManager.appTheme = AppTheme.getTheme(which)
                        dialog.dismiss()
                        restartPC(activity)
                        true
                    }
                builder.title(R.string.theme)
                builder.build().show()
                return true
            }
            PreferencesConstants.FRAGMENT_FEEDBACK -> {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "eros.ponte@pubnative.net", null
                    )
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback : EasyFiles Manager")
                val packageManager = activity.packageManager
                val activities: List<*> = packageManager.queryIntentActivities(
                    emailIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                val isIntentSafe = activities.size > 0
                if (isIntentSafe) startActivity(
                    Intent.createChooser(
                        emailIntent,
                        resources.getString(R.string.feedback)
                    )
                ) else Toast.makeText(
                    activity, (resources.getString(R.string.send_email_to)
                            + " vishalmeham2@gmail.com"), Toast.LENGTH_LONG
                ).show()
                return false
            }
            PreferencesConstants.FRAGMENT_ABOUT -> {
                startActivity(Intent(activity, AboutActivity::class.java))
                return false
            }
            PreferencesConstants.FRAGMENT_COLORS -> {
                (activity as PreferencesActivity)
                    .selectItem(PreferencesActivity.COLORS_PREFERENCE)
                return true
            }
            PreferencesConstants.FRAGMENT_FOLDERS -> {
                (activity as PreferencesActivity)
                    .selectItem(PreferencesActivity.FOLDERS_PREFERENCE)
                return true
            }
            PreferencesConstants.FRAGMENT_QUICKACCESSES -> {
                (activity as PreferencesActivity)
                    .selectItem(PreferencesActivity.QUICKACCESS_PREFERENCE)
                return true
            }
            PreferencesConstants.FRAGMENT_ADVANCED_SEARCH -> {
                (activity as PreferencesActivity)
                    .selectItem(PreferencesActivity.ADVANCEDSEARCH_PREFERENCE)
                return true
            }
            PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD -> {
                val masterPasswordDialogBuilder = MaterialDialog.Builder(activity)
                masterPasswordDialogBuilder.title(resources.getString(R.string.crypt_pref_master_password_title))
                var decryptedPassword: String? = null
                try {
                    val preferencePassword = sharedPref!!.getString(
                        PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                        PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT
                    )
                    if (preferencePassword != PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT) {

                        // password is set, try to decrypt
                        decryptedPassword = CryptUtil.decryptPassword(activity, preferencePassword)
                    } else {
                        // no password set in preferences, just leave the field empty
                        decryptedPassword = ""
                    }
                } catch (e: GeneralSecurityException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                masterPasswordDialogBuilder.input(
                    resources.getString(R.string.authenticate_password),
                    decryptedPassword, false,
                    object : InputCallback {
                        override fun onInput(dialog: MaterialDialog, input: CharSequence) {}
                    })
                masterPasswordDialogBuilder.theme(utilsProvider!!.appTheme.materialDialogTheme)
                masterPasswordDialogBuilder.positiveText(resources.getString(R.string.ok))
                masterPasswordDialogBuilder.negativeText(resources.getString(R.string.cancel))
                masterPasswordDialogBuilder.positiveColor(
                    utilsProvider!!.colorPreference.getColor(
                        ColorUsage.ACCENT
                    )
                )
                masterPasswordDialogBuilder.negativeColor(
                    utilsProvider!!.colorPreference.getColor(
                        ColorUsage.ACCENT
                    )
                )
                masterPasswordDialogBuilder.onPositive(object : SingleButtonCallback {
                    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        try {
                            val inputText = dialog.inputEditText!!.text.toString()
                            if (inputText != PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT) {
                                sharedPref!!.edit().putString(
                                    PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    CryptUtil.encryptPassword(
                                        activity,
                                        dialog.inputEditText!!.text.toString()
                                    )
                                ).apply()
                            } else {
                                // empty password, remove the preference
                                sharedPref!!.edit().putString(
                                    PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    ""
                                ).apply()
                            }
                        } catch (e: GeneralSecurityException) {
                            e.printStackTrace()
                            sharedPref!!.edit().putString(
                                PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT
                            ).apply()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            sharedPref!!.edit().putString(
                                PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT
                            ).apply()
                        }
                    }
                })
                masterPasswordDialogBuilder.onNegative(object : SingleButtonCallback {
                    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        dialog.cancel()
                    }
                })
                masterPasswordDialogBuilder.build().show()
                return true
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        /**This is a hack see [PreferencesActivity.saveListViewState] */
        listView = v!!.findViewById(android.R.id.list)
        return v
    }

    override fun onPause() {
        super.onPause()
        if (listView != null) {
            /**This is a hack see [PreferencesActivity.saveListViewState] */
            (activity as PreferencesActivity)
                .saveListViewState(
                    PreferencesActivity.START_PREFERENCE,
                    listView!!.onSaveInstanceState()
                )
        }
    }

    override fun onResume() {
        super.onResume()
        if (listView != null) {
            /**This is a hack see [PreferencesActivity.saveListViewState] */
            val restored =
                (activity as PreferencesActivity).restoreListViewState(PreferencesActivity.START_PREFERENCE)
            if (restored != null) {
                listView!!.onRestoreInstanceState(restored)
            }
        }
    }

    companion object {
        private val PREFERENCE_KEYS = arrayOf(
            PreferencesConstants.PREFERENCE_GRID_COLUMNS,
            PreferencesConstants.PREFERENCE_ROOTMODE,
            PreferencesConstants.PREFERENCE_SHOW_HIDDENFILES,
            PreferencesConstants.FRAGMENT_FEEDBACK,
            PreferencesConstants.FRAGMENT_ABOUT,
            PreferencesConstants.FRAGMENT_FOLDERS,
            PreferencesConstants.FRAGMENT_QUICKACCESSES,
            PreferencesConstants.FRAGMENT_ADVANCED_SEARCH
        )

        fun restartPC(activity: Activity?) {
            if (activity == null) return
            val enter_anim = android.R.anim.fade_in
            val exit_anim = android.R.anim.fade_out
            activity.overridePendingTransition(enter_anim, exit_anim)
            activity.finish()
            activity.overridePendingTransition(enter_anim, exit_anim)
            activity.startActivity(activity.intent)
        }
    }
}