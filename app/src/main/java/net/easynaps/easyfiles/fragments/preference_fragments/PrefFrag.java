package net.easynaps.easyfiles.fragments.preference_fragments;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.AboutActivity;
import net.easynaps.easyfiles.activities.PreferencesActivity;
import net.easynaps.easyfiles.activities.superclasses.BasicActivity;
import net.easynaps.easyfiles.ui.views.preference.CheckBox;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.files.CryptUtil;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.easynaps.easyfiles.utils.theme.AppTheme;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static net.easynaps.easyfiles.activities.PreferencesActivity.START_PREFERENCE;

public class PrefFrag extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String[] PREFERENCE_KEYS = {PreferencesConstants.PREFERENCE_GRID_COLUMNS,
            PreferencesConstants.PREFERENCE_ROOTMODE,
            PreferencesConstants.PREFERENCE_SHOW_HIDDENFILES, PreferencesConstants.FRAGMENT_FEEDBACK,
            PreferencesConstants.FRAGMENT_ABOUT,
            PreferencesConstants.FRAGMENT_FOLDERS, PreferencesConstants.FRAGMENT_QUICKACCESSES,
            PreferencesConstants.FRAGMENT_ADVANCED_SEARCH};

    private UtilitiesProvider utilsProvider;
    private SharedPreferences sharedPref;
    /**This is a hack see {@link PreferencesActivity#saveListViewState(int, Parcelable)}*/
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = ((BasicActivity) getActivity()).getUtilsProvider();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        for (String PREFERENCE_KEY : PREFERENCE_KEYS) {
            findPreference(PREFERENCE_KEY).setOnPreferenceClickListener(this);
        }

        // crypt master password
        final Preference masterPasswordPreference = findPreference(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ||
                sharedPref.getBoolean(PreferencesConstants.PREFERENCE_CRYPT_FINGERPRINT, false)) {
            // encryption feature not available
            masterPasswordPreference.setEnabled(false);
        }
        masterPasswordPreference.setOnPreferenceClickListener(this);

        CheckBox checkBoxFingerprint = (CheckBox) findPreference(PreferencesConstants.PREFERENCE_CRYPT_FINGERPRINT);

        try {

            // finger print sensor
            final FingerprintManager fingerprintManager = (FingerprintManager)
                    getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

            final KeyguardManager keyguardManager = (KeyguardManager)
                    getActivity().getSystemService(Context.KEYGUARD_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager.isHardwareDetected()) {

                checkBoxFingerprint.setEnabled(true);
            }

            checkBoxFingerprint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_no_permission),
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            !fingerprintManager.hasEnrolledFingerprints()) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_not_enrolled),
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            !keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_no_security),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    masterPasswordPreference.setEnabled(false);
                    return true;
                }
            });
        } catch (NoClassDefFoundError error) {
            error.printStackTrace();

            // fingerprint manager class not defined in the framework
            checkBoxFingerprint.setEnabled(false);
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String[] sort;
        MaterialDialog.Builder builder;

        switch (preference.getKey()) {
            case PreferencesConstants.PREFERENCE_GRID_COLUMNS:
                sort = getResources().getStringArray(R.array.columns);
                builder = new MaterialDialog.Builder(getActivity());
                builder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                builder.title(R.string.gridcolumnno);
                int current = Integer.parseInt(sharedPref.getString(PreferencesConstants.PREFERENCE_GRID_COLUMNS, "-1"));
                current = current == -1 ? 0 : current;
                if (current != 0) current = current - 1;
                builder.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sharedPref.edit().putString(PreferencesConstants.PREFERENCE_GRID_COLUMNS, "" + (which != 0 ? sort[which] : "" + -1)).commit();
                        dialog.dismiss();
                        return true;
                    }
                });
                builder.build().show();
                return true;
            case PreferencesConstants.FRAGMENT_THEME:
                sort = getResources().getStringArray(R.array.theme);
                current = Integer.parseInt(sharedPref.getString(PreferencesConstants.FRAGMENT_THEME, "0"));
                builder = new MaterialDialog.Builder(getActivity());
                //builder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                builder.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        utilsProvider.getThemeManager().setAppTheme(AppTheme.getTheme(which));
                        dialog.dismiss();
                        restartPC(getActivity());
                        return true;
                    }
                });
                builder.title(R.string.theme);
                builder.build().show();
                return true;
            case PreferencesConstants.FRAGMENT_FEEDBACK:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "eros.ponte@pubnative.net", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback : EasyFiles Manager");

                PackageManager packageManager = getActivity().getPackageManager();
                List activities = packageManager.queryIntentActivities(emailIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe)
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.feedback)));
                else
                    Toast.makeText(getActivity(), getResources().getString(R.string.send_email_to)
                            + " vishalmeham2@gmail.com", Toast.LENGTH_LONG).show();
                return false;
            case PreferencesConstants.FRAGMENT_ABOUT:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            /*FROM HERE BE FRAGMENTS*/
            case PreferencesConstants.FRAGMENT_COLORS:
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.COLORS_PREFERENCE);
                return true;
            case PreferencesConstants.FRAGMENT_FOLDERS:
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.FOLDERS_PREFERENCE);
                return true;
            case PreferencesConstants.FRAGMENT_QUICKACCESSES:
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.QUICKACCESS_PREFERENCE);
                return true;
            case PreferencesConstants.FRAGMENT_ADVANCED_SEARCH:
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.ADVANCEDSEARCH_PREFERENCE);
                return true;
            case PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD:
                MaterialDialog.Builder masterPasswordDialogBuilder = new MaterialDialog.Builder(getActivity());
                masterPasswordDialogBuilder.title(getResources().getString(R.string.crypt_pref_master_password_title));

                String decryptedPassword = null;
                try {
                    String preferencePassword = sharedPref.getString(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                            PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT);
                    if (!preferencePassword.equals(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)) {

                        // password is set, try to decrypt
                        decryptedPassword = CryptUtil.decryptPassword(getActivity(), preferencePassword);
                    } else {
                        // no password set in preferences, just leave the field empty
                        decryptedPassword = "";
                    }
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }

                masterPasswordDialogBuilder.input(getResources().getString(R.string.authenticate_password),
                        decryptedPassword, false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            }
                        });
                masterPasswordDialogBuilder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                masterPasswordDialogBuilder.positiveText(getResources().getString(R.string.ok));
                masterPasswordDialogBuilder.negativeText(getResources().getString(R.string.cancel));
                masterPasswordDialogBuilder.positiveColor(utilsProvider.getColorPreference().getColor(ColorUsage.ACCENT));
                masterPasswordDialogBuilder.negativeColor(utilsProvider.getColorPreference().getColor(ColorUsage.ACCENT));

                masterPasswordDialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {

                            String inputText = dialog.getInputEditText().getText().toString();
                            if (!inputText.equals(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)) {

                                sharedPref.edit().putString(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                        CryptUtil.encryptPassword(getActivity(),
                                                dialog.getInputEditText().getText().toString())).apply();
                            } else {
                                // empty password, remove the preference
                                sharedPref.edit().putString(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                        "").apply();
                            }
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                            sharedPref.edit().putString(PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    PreferencesConstants.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT).apply();
                        }
                    }
                });

                masterPasswordDialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                });

                masterPasswordDialogBuilder.build().show();
                return true;
        }

        return false;
    }

    public static void restartPC(final Activity activity) {
        if (activity == null) return;

        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.startActivity(activity.getIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);
        /**This is a hack see {@link PreferencesActivity#saveListViewState(int, Parcelable)}*/
        listView = v.findViewById(android.R.id.list);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();


        if(listView != null) {
            /**This is a hack see {@link PreferencesActivity#saveListViewState(int, Parcelable)}*/
            ((PreferencesActivity) getActivity())
                    .saveListViewState(START_PREFERENCE, listView.onSaveInstanceState());

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(listView != null) {
            /**This is a hack see {@link PreferencesActivity#saveListViewState(int, Parcelable)}*/
            Parcelable restored = ((PreferencesActivity) getActivity()).restoreListViewState(START_PREFERENCE);
            if(restored != null) {
                listView.onRestoreInstanceState(restored);
            }
        }
    }

}
