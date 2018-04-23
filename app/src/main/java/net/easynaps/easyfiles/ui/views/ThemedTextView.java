package net.easynaps.easyfiles.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.theme.AppTheme;

public class ThemedTextView extends AppCompatTextView {

    public ThemedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (((MainActivity) context).getAppTheme().equals(AppTheme.LIGHT)) {
            setTextColor(Utils.getColor(getContext(), android.R.color.black));
        } else if (((MainActivity) context).getAppTheme().equals(AppTheme.DARK) || ((MainActivity) context).getAppTheme().equals(AppTheme.BLACK)) {
            setTextColor(Utils.getColor(getContext(), android.R.color.white));
        }
    }
}
