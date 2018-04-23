package net.easynaps.easyfiles.utils;

import android.graphics.Color;

public class PreferenceUtils {

    public static final String KEY_CURRENT_TAB = "current_tab";

    public static final int DEFAULT_PRIMARY = 4;
    public static final int DEFAULT_ACCENT = 1;
    public static final int DEFAULT_ICON = -1;
    public static final int DEFAULT_CURRENT_TAB = 1;

    public static int getStatusColor(String skin) {
        return darker(Color.parseColor(skin));
    }

    public static int getStatusColor(int skin) {
        return darker(skin);
    }

    private static int darker(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * 0.6f), 0),
                Math.max((int) (g * 0.6f), 0),
                Math.max((int) (b * 0.6f), 0));
    }

}
