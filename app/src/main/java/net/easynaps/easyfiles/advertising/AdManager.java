package net.easynaps.easyfiles.advertising;

public class AdManager {

    private static AdManager sInstance;

    private AdManager() {

    }

    public static AdManager getInstance() {
        if (sInstance == null) {
            sInstance = new AdManager();
        }

        return sInstance;
    }
}
