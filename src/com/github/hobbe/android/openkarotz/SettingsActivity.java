package com.github.hobbe.android.openkarotz;

import android.app.Activity;
import android.os.Bundle;

/**
 * Settings activity.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new KarotzSettingsFragment()).commit();

    }


    /** Key for Karotz hostname preference. */
    public static final String KEY_PREF_KAROTZ_HOST = "prefKarotzHost";

}
