/*
 * OpenKarotz-Android
 * http://github.com/hobbe/OpenKarotz-Android
 *
 * Copyright (c) 2014 Olivier Bagot (http://github.com/hobbe)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * http://opensource.org/licenses/MIT
 *
 */

package com.github.hobbe.android.openkarotz.karotz;

import java.io.IOException;

/**
 * This interface describes a Karotz.
 */
public interface IKarotz {

    /**
     * Get the Karotz LED color.
     *
     * @return the LED color
     * @throws IOException if an I/O error occurs
     */
    int getColor() throws IOException;

    /**
     * Get the Karotz hostname.
     *
     * @return the hostname
     */
    String getHostname();

    /**
     * Get the Karotz status.
     *
     * @return the status
     * @throws IOException if an I/O error occurs
     */
    KarotzStatus getStatus() throws IOException;

    /**
     * Get the Karotz version.
     *
     * @return the version
     * @throws IOException if an I/O error occurs
     */
    String getVersion() throws IOException;

    /**
     * Check if Karotz LED is pulsing.
     *
     * @return the
     * @throws IOException if an I/O error occurs
     */
    boolean isPulsing() throws IOException;

    /**
     * Change LED color and pulse.
     *
     * @param color the LED color
     * @param pulse if {@code true}, LED will pulse
     * @throws IOException if an I/O error occurs
     */
    void led(int color, boolean pulse) throws IOException;

    /**
     * Put Karotz to sleep.
     * @return {@code true} if action was successful, else {@code false}. If Karotz was already sleeping, {@code true} is returned.
     *
     * @throws IOException if an I/O error occurs
     */
    boolean sleep() throws IOException;

    /**
     * Wake up Karotz.
     *
     * @param silent if {@code true}, no sound is played on wake up
     * @return {@code true} if action was successful, else {@code false}.
     * @throws IOException if an I/O error occurs
     */
    boolean wakeup(boolean silent) throws IOException;


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
         *
         * @return {@code true} if this is an offline status
         */
        public boolean isOffline() {
            return (this == UNKNOWN || this == OFFLINE);
        }

        /**
         * Check if this status corresponds to an online status.
         *
         * @return {@code true} if this is an online status
         */
        public boolean isOnline() {
            return (this == SLEEPING || this == AWAKE);
        }
    }
}
