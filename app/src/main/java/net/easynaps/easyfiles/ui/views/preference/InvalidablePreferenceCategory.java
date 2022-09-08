package net.easynaps.easyfiles.ui.views.preference;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import net.easynaps.easyfiles.utils.PreferenceUtils;
import net.easynaps.easyfiles.utils.color.ColorPreference;
import net.easynaps.easyfiles.utils.color.ColorUsage;

public class InvalidablePreferenceCategory extends PreferenceCategory {

    private int titleColor;

    public InvalidablePreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        AppCompatTextView title = view.findViewById(android.R.id.title);
        title.setTextColor(titleColor);
    }

    public void invalidate(ColorPreference colorPreference) {
        titleColor = PreferenceUtils.getStatusColor(colorPreference.getColor(ColorUsage.ACCENT));
        notifyChanged();
    }
}
