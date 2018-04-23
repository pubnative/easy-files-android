package net.easynaps.easyfiles.activities.superclasses;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants;
import net.easynaps.easyfiles.ui.dialogs.ColorPickerDialog;
import net.easynaps.easyfiles.ui.dialogs.GeneralDialogCreation;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.theme.AppTheme;

import static net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants.PREFERENCE_ROOTMODE;

public class ThemedActivity extends PreferenceActivity {

    public boolean checkStorage = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // checking if theme should be set light/dark or automatic
        int colorPickerPref = getPrefs().getInt(PreferencesConstants.PREFERENCE_COLOR_CONFIG, ColorPickerDialog.NO_DATA);
        if (colorPickerPref == ColorPickerDialog.RANDOM_INDEX) {
            getColorPreference().randomize().saveToPreferences(getPrefs());
        }

        setTheme();

        //requesting storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkStorage && !checkStoragePermission()) {
            requestStoragePermission();
        }
    }

    public boolean checkStoragePermission() {
        // Verify that all required contact permissions have been granted.
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            final MaterialDialog materialDialog = GeneralDialogCreation.showBasicDialog(this,
                    new String[]{getString(R.string.granttext),
                            getString(R.string.grantper),
                            getString(R.string.grant),
                            getString(R.string.cancel),
                            null});
            materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                ActivityCompat.requestPermissions(ThemedActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
                materialDialog.dismiss();
            });
            materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> {
                finish();
            });
            materialDialog.setCancelable(false);
            materialDialog.show();

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
        }
    }

    void setTheme() {
        AppTheme theme = getAppTheme().getSimpleTheme();
        if (Build.VERSION.SDK_INT >= 21) {

            switch (getColorPreference().getColorAsString(ColorUsage.ACCENT).toUpperCase()) {
                case "#43004f":
                    if (theme.equals(AppTheme.LIGHT))
                        setTheme(R.style.pref_accent_light_red);
                    else
                        setTheme(R.style.pref_accent_dark_red);
                    break;
            }
        } else {
            if (theme.equals(AppTheme.LIGHT)) {
                setTheme(R.style.appCompatLight);
            } else if (theme.equals(AppTheme.BLACK)) {
                setTheme(R.style.appCompatBlack);
            } else {
                setTheme(R.style.appCompatDark);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
    }

}