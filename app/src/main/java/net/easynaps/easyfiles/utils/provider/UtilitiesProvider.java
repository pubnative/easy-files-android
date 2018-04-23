package net.easynaps.easyfiles.utils.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.easynaps.easyfiles.utils.color.ColorPreference;
import net.easynaps.easyfiles.utils.theme.AppTheme;
import net.easynaps.easyfiles.utils.theme.AppThemeManager;

/**
 * Created by piotaixr on 16/01/17.
 */

public class UtilitiesProvider {
    private ColorPreference colorPreference;
    private AppThemeManager appThemeManager;

    public UtilitiesProvider(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        colorPreference = ColorPreference.loadFromPreferences(context, sharedPreferences);
        appThemeManager = new AppThemeManager(sharedPreferences);
    }

    public ColorPreference getColorPreference() {
        return colorPreference;
    }

    public AppTheme getAppTheme() {
        return appThemeManager.getAppTheme();
    }

    public AppThemeManager getThemeManager() {
        return appThemeManager;
    }
}
