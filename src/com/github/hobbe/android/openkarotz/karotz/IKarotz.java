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
     * Change the Karotz ear position.
     * <p>
     * This method returns an array with the two ear positions: left ear at index 0, right ear at index 1.
     * @param left the left ear position
     * @param right the right ear position
     * @return the resulting ear positions as array[left, right]
     * @throws IOException if an I/O error occurs
     */
    EarPosition[] ears(EarPosition left, EarPosition right) throws IOException;

    /**
     * Change the Karotz ear mode. If {@link EarMode#DISABLED disabled}, the Karotz ears will no longer move.
     * @param mode the ear mode to set
     * @return the resulting ear mode
     * @throws IOException if an I/O error occurs
     */
    EarMode earsMode(EarMode mode) throws IOException;

    /**
     * Turn ears in a random position.
     * @return the resulting ear positions as array[left, right]
     * @throws IOException if an I/O error occurs
     */
    EarPosition[] earsRandom() throws IOException;

    /**
     * Reset ear position.
     * @throws IOException if an I/O error occurs
     */
    void earsReset() throws IOException;

    /**
     * Get the Karotz LED color.
     * @return the LED color
     * @throws IOException if an I/O error occurs
     */
    int getColor() throws IOException;

    /**
     * Get the Karotz ear mode.
     * @return the ear mode
     * @throws IOException if an I/O error occurs
     */
    EarMode getEarMode() throws IOException;

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
     * @return {@code true} if action was successful, else {@code false}. If Karotz was already sleeping, {@code true}
     *         is returned.
     * @throws IOException if an I/O error occurs
     */
    boolean sleep() throws IOException;

    /**
     * Make Karotz play a sound from a URL.
     * @param url the sound URL to play
     * @return {@code true} if action was successful, else {@code false}. If {@code url} is {@code null}, {@code true}
     *         is returned.
     * @throws IOException if an I/O error occurs
     */
    boolean sound(String url) throws IOException;

    /**
     * Call sound control with the given command.
     * @param command the sound control command
     * @return {@code true} if action was successful, else {@code false}.
     * @throws IOException if an I/O error occurs
     */
    boolean soundControl(SoundControlCommand command) throws IOException;

    /**
     * Wake up Karotz.
     * @param silent if {@code true}, no sound is played on wake up
     * @return {@code true} if action was successful, else {@code false}.
     * @throws IOException if an I/O error occurs
     */
    boolean wakeup(boolean silent) throws IOException;


    /**
     * Enumeration of ear modes.
     */
    public enum EarMode {

        /** Ears enabled. */
        ENABLED,

        /** Ears disabled. */
        DISABLED;

        /**
         * Check if this ear mode disables ear movement.
         * @return {@code true} if ear movement is disabled
         */
        public boolean isDisabled() {
            return this == DISABLED;
        }

        /**
         * Check if this ear mode enables ear movement.
         * @return {@code true} if ear movement is enabled
         */
        public boolean isEnabled() {
            return this == ENABLED;
        }
    }

    /**
     * Enumeration of possible ear positions.
     */
    public enum EarPosition {

        /** Position 1. */
        POSITION_1((byte) 1),

        /** Position 2. */
        POSITION_2((byte) 2),

        /** Position 3. */
        POSITION_3((byte) 3),

        /** Position 4. */
        POSITION_4((byte) 4),

        /** Position 5. */
        POSITION_5((byte) 5),

        /** Position 6. */
        POSITION_6((byte) 6),

        /** Position 7. */
        POSITION_7((byte) 7),

        /** Position 8. */
        POSITION_8((byte) 8),

        /** Position 9. */
        POSITION_9((byte) 9),

        /** Position 10. */
        POSITION_10((byte) 10),

        /** Position 11. */
        POSITION_11((byte) 11),

        /** Position 12. */
        POSITION_12((byte) 12),

        /** Position 13. */
        POSITION_13((byte) 13),

        /** Position 14. */
        POSITION_14((byte) 14),

        /** Position 15. */
        POSITION_15((byte) 15),

        /** Position 16. */
        POSITION_16((byte) 16);

        private EarPosition(byte position) {
            this.position = position;
        }

        /**
         * Get the ear position corresponding to the given angle.
         * @param angle an angle, 0 to 360°; values above will be modded with 360
         * @return the ear position corresponding to the angle
         */
        public static EarPosition fromAngle(int angle) {
            int angle360 = angle % 360;
            int position = Math.round((angle360 / 360.0f) * 16.0f) + 1;
            return fromIntValue(position);
        }

        /**
         * Get the ear position corresponding to the given position value.
         * @param position the ear position as int (1 to 16)
         * @return the ear position corresponding to the given position value
         */
        public static EarPosition fromIntValue(int position) {
            switch (position) {
            case 1:
                return EarPosition.POSITION_1;
            case 2:
                return EarPosition.POSITION_2;
            case 3:
                return EarPosition.POSITION_3;
            case 4:
                return EarPosition.POSITION_4;
            case 5:
                return EarPosition.POSITION_5;
            case 6:
                return EarPosition.POSITION_6;
            case 7:
                return EarPosition.POSITION_7;
            case 8:
                return EarPosition.POSITION_8;
            case 9:
                return EarPosition.POSITION_9;
            case 10:
                return EarPosition.POSITION_10;
            case 11:
                return EarPosition.POSITION_11;
            case 12:
                return EarPosition.POSITION_12;
            case 13:
                return EarPosition.POSITION_13;
            case 14:
                return EarPosition.POSITION_14;
            case 15:
                return EarPosition.POSITION_15;
            case 16:
                return EarPosition.POSITION_16;
            default:
                return EarPosition.POSITION_1;
            }
        }

        /**
         * Get the position value.
         * @return the position value
         */
        public byte getPosition() {
            return position;
        }

        /**
         * Get the angle corresponding to the ear position.
         * @return the angle, 0 to 360°
         */
        public int toAngle() {
            return Math.round(position * 360.0f / 16.0f);
        }

        @Override
        public String toString() {
            return String.valueOf(position);
        }


        /** Possible positions: 1 to 16. */
        private final byte position;
    }

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
         * Check if this status corresponds to an awake status.
         * @return {@code true} if this is an awake status
         */
        public boolean isAwake() {
            return (this == AWAKE);
        }

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

        /**
         * Check if this status corresponds to a sleeping status.
         * @return {@code true} if this is a sleeping status
         */
        public boolean isSleeping() {
            return (this == SLEEPING);
        }
    }

    /**
     * Enumeration of possible sound control commands.
     */
    public enum SoundControlCommand {

        /** Stop sound. */
        STOP("quit"),

        /** Pause sound. */
        PAUSE("pause");

        private SoundControlCommand(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public String toString() {
            return cmd;
        }


        private final String cmd;
    }

}
