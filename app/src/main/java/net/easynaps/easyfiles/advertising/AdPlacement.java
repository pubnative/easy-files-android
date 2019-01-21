package net.easynaps.easyfiles.advertising;

import android.view.View;

public interface AdPlacement {
    View getAdView();

    void loadAd();

    void destroy();
}
