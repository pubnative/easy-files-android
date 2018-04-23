package net.easynaps.easyfiles.activities.superclasses;

import android.support.v7.app.AppCompatActivity;

import net.easynaps.easyfiles.utils.application.AppConfig;
import net.easynaps.easyfiles.utils.color.ColorPreference;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.easynaps.easyfiles.utils.theme.AppTheme;

public class BasicActivity extends AppCompatActivity {

    protected AppConfig getAppConfig() {
        return (AppConfig) getApplication();
    }

    public ColorPreference getColorPreference() {
        return getAppConfig().getUtilsProvider().getColorPreference();
    }

    public AppTheme getAppTheme() {
        return getAppConfig().getUtilsProvider().getAppTheme();
    }

    public UtilitiesProvider getUtilsProvider() {
        return getAppConfig().getUtilsProvider();
    }
}
