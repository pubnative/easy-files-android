package net.easynaps.easyfiles.utils;

import android.support.annotation.DrawableRes;

public interface BottomBarButtonPath {
    void changePath(String path);

    String getPath();

    @DrawableRes int getRootDrawable();
}
