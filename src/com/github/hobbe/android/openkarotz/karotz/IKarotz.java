package com.github.hobbe.android.openkarotz.karotz;

import java.io.IOException;

/**
 * This interface describes a Karotz.
 */
public interface IKarotz {

    /**
     * Get the Karotz LED color.
     * @return the LED color
     * @throws IOException if an I/O error occurs
     */
    int getColor() throws IOException;

    /**
     * Get the Karotz hostname.
     * @return the hostname
     */
    String getHostname();

    /**
     * Get the Karotz status.
     * @return the status
     * @throws IOException if an I/O error occurs
     */
    KarotzStatus getStatus() throws IOException;

    /**
     * Get the Karotz version.
     * @return the version
     * @throws IOException if an I/O error occurs
     */
    String getVersion() throws IOException;

    /**
     * Check if Karotz LED is pulsing.
     * @return the
     * @throws IOException if an I/O error occurs
     */
    boolean isPulsing() throws IOException;

    /**
     * Change LED color and pulse.
     * @param color the LED color
     * @param pulse if {@code true}, LED will pulse
     * @throws IOException if an I/O error occurs
     */
    void led(int color, boolean pulse) throws IOException;

    /**
     * Put Karotz to sleep.
     * @throws IOException if an I/O error occurs
     */
    void sleep() throws IOException;

    /**
     * Wake up Karotz.
     * @param silent if {@code true}, no sound is played on wake up
     * @throws IOException if an I/O error occurs
     */
    void wakeup(boolean silent) throws IOException;


    /**
     * Possible Karotz status.
     */
    public enum KarotzStatus {

        /** Unknown status. */
        UNKNOWN,

        /** Off line status, cannot be reached. */
        OFFLINE,

        /** Sleeping status, Karotz is not active, but some actions can still be done. */
        SLEEPING,

        /** Awake status, Karotz is active. */
        AWAKE;

        /**
         * Check if this status corresponds to an offline status.
         * @return {@code true} if this is an offline status
         */
        public boolean isOffline() {
            return (this == UNKNOWN || this == OFFLINE);
        }

        /**
         * Check if this status corresponds to an online status.
         * @return {@code true} if this is an online status
         */
        public boolean isOnline() {
            return (this == SLEEPING || this == AWAKE);
        }
    }
}
