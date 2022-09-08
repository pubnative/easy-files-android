package net.easynaps.easyfiles.ui.views.drawer;

import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.ColorInt;

import com.google.android.material.navigation.NavigationView;

public class ActionViewStateManager {

    private ImageButton lastItemSelected = null;
    private @ColorInt int idleIconColor;
    private @ColorInt int selectedIconColor;

    public ActionViewStateManager(NavigationView navView, @ColorInt int idleColor,
                                  @ColorInt int accentColor) {
        idleIconColor = idleColor;
        selectedIconColor = accentColor;
    }

    public void deselectCurrentActionView() {
        if(lastItemSelected != null) {
            lastItemSelected.setColorFilter(idleIconColor);
            lastItemSelected = null;
        }
    }

    public void selectActionView(MenuItem item) {
        if(lastItemSelected != null) {
            lastItemSelected.setColorFilter(idleIconColor);
        }
        if(item.getActionView() != null) {
            lastItemSelected = (ImageButton) item.getActionView();
            lastItemSelected.setColorFilter(selectedIconColor);
        }
    }

}
