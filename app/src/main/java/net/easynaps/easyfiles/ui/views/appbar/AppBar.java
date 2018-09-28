package net.easynaps.easyfiles.ui.views.appbar;

import android.content.SharedPreferences;
import androidx.annotation.StringRes;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants;

import static android.os.Build.VERSION.SDK_INT;

public class AppBar {

    private int TOOLBAR_START_INSET;

    private Toolbar toolbar;
    private SearchView searchView;
    private BottomBar bottomBar;

    private AppBarLayout appbarLayout;

    public AppBar(MainActivity a, SharedPreferences sharedPref, SearchView.SearchListener searchListener) {
        toolbar = (Toolbar) a.findViewById(R.id.action_bar);
        searchView = new SearchView(this, a, searchListener);
        bottomBar = new BottomBar(this, a);

        appbarLayout = (AppBarLayout) a.findViewById(R.id.lin);

        if (SDK_INT >= 21) toolbar.setElevation(0);
        /* For SearchView, see onCreateOptionsMenu(Menu menu)*/
        TOOLBAR_START_INSET = toolbar.getContentInsetStart();

        if (!sharedPref.getBoolean(PreferencesConstants.PREFERENCE_INTELLI_HIDE_TOOLBAR, true)) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            appbarLayout.setExpanded(true, true);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }

    public AppBarLayout getAppbarLayout() {
        return appbarLayout;
    }

    public void setTitle(String title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

    public void setTitle(@StringRes int title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

}
