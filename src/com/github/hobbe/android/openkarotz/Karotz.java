package com.github.hobbe.android.openkarotz;

import com.github.hobbe.android.openkarotz.karotz.IKarotz;
import com.github.hobbe.android.openkarotz.karotz.OpenKarotz;

/**
 * Karotz instance.
 */
public class Karotz {

    private Karotz() {
        // No instance
    }

    /**
     * Get the Karotz instance.
     * @return the Karotz instance.
     */
    public static IKarotz getInstance() {
        if (k == null) {
            throw new IllegalAccessError();
        }
        return k;
    }

    /**
     * Initialize the Karotz application singleton.
     * @param hostname the Karotz hostname.
     */
    public static void initialize(String hostname) {
        k = new OpenKarotz(hostname);
    }


    private static IKarotz k = null;
}
