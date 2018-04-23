package net.easynaps.easyfiles.fragments.preference_fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.easynaps.easyfiles.R;

public class AdvancedSearchPref extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.advancedsearch_prefs);
    }

}
