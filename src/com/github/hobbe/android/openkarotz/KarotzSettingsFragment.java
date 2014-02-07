package com.github.hobbe.android.openkarotz;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Karotz settings fragment.
 */
public class KarotzSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.karotz_settings);

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();

        String value = preferences.getString(SettingsActivity.KEY_PREF_KAROTZ_HOST, "");
        updatePreferenceSummary(SettingsActivity.KEY_PREF_KAROTZ_HOST, value, R.string.karotz_host_pref_summary);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(SettingsActivity.KEY_PREF_KAROTZ_HOST)) {
            String value = preferences.getString(key, "");
            updatePreferenceSummary(key, value, R.string.karotz_host_pref_summary);
        }
    }

    private void updatePreferenceSummary(String key, String value, int descResId) {
        Preference pref = findPreference(key);
        pref.setSummary(getString(descResId) + ' ' + value);
    }
}
