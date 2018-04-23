package net.easynaps.easyfiles.database.models;

import android.content.SharedPreferences;

import net.easynaps.easyfiles.utils.files.FileUtils;

public class Tab {
    public final int tabNumber;
    public final String path;
    public final String home;

    public Tab(int tabNo, String path, String home) {
        this.tabNumber = tabNo;
        this.path = path;
        this.home = home;
    }

    public String getOriginalPath(boolean savePaths, SharedPreferences sharedPreferences){
        if(savePaths && FileUtils.isPathAccesible(path, sharedPreferences)) {
            return path;
        } else {
            return home;
        }
    }

}
