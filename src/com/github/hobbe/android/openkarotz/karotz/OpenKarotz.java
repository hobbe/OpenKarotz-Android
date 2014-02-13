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
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.util.Log;

import com.github.hobbe.android.openkarotz.net.NetUtils;

/**
 * OpenKarotz implementation.
 */
public class OpenKarotz implements IKarotz {

    /**
     * Initialize a new OpenKarotz instance.
     *
     * @param hostname the hostname or IP
     */
    public OpenKarotz(String hostname) {

        this.hostname = hostname;

        try {
            this.api = new URL(PROTOCOL + "://" + hostname + ":" + PORT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getColor() throws IOException {
        if (state == null) {
            status();
        }
        return state.getLedColor();
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public KarotzStatus getStatus() throws IOException {
        if (isOffline()) {
            // Re-check state
            status();
        }
        return state.getStatus();
    }

    @Override
    public String getVersion() throws IOException {
        if (isOffline()) {
            // Re-check state
            status();
        }
        return state.getVersion();
    }

    @Override
    public boolean isPulsing() throws IOException {
        if (isOffline()) {
            // Re-check state
            status();
        }
        return state.isPulsing();
    }

    @Override
    public void led(int color, boolean pulse) throws IOException {
        int rgb = color & 0x00FFFFFF;

        if (pulse == state.isPulsing() && rgb == state.getLedColor()) {
            // No change
            return;
        }

        String c = toColorCode(rgb);

        URL url = new URL(api, CGI_BIN + "/leds?color=" + c + (pulse ? "&pulse=1" : ""));
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"color":"0000FF","secondary_color":"000000","pulse":"0","no_memory":"0","speed":"700","return":"0"}
        // Answer: {"return":"1","msg":"Unable to perform action, rabbit is sleeping."}
        try {
            JSONObject json = new JSONObject(result);
            boolean ok = "0".equals(json.getString("return"));

            if (ok) {
                state.setLedColor(Color.parseColor("#" + json.getString("color")));
                state.setPulsing("1".equals(json.getString("pulse")));
                return;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot change LED on Karotz: " + e.getMessage(), e);
        }

        // Not OK, set back to previous values
        state.setLedColor(rgb);
        state.setPulsing(pulse);
    }

    @Override
    public boolean sleep() throws IOException {
        if (isSleeping()) {
            // No change
            Log.d(TAG, "Already sleeping, no need to go to sleep");
            return true;
        }

        URL url = new URL(api, CGI_BIN + "/sleep");
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"return":"0"}
        // Answer: {"return":"1","msg":"Unable to perform action, rabbit is already sleeping."}
        try {
            JSONObject json = new JSONObject(result);
            state.setStatus("0".equals(json.getString("return")) ? KarotzStatus.SLEEPING : KarotzStatus.AWAKE);
            return true;
        } catch (JSONException e) {
            state.setStatus(KarotzStatus.UNKNOWN);
            return false;
        }

    }

    @Override
    public boolean sound(String soundUrl) throws IOException {
        if (soundUrl == null || soundUrl.length() <= 0) {
            return true;
        }

        URL url = new URL(api, CGI_BIN + "/sound?url=" + soundUrl);
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"return":"0"}
        // Answer: {"return":"1","msg":"Unable to perform action, rabbit is sleeping."}
        try {
            JSONObject json = new JSONObject(result);
            boolean ok = "0".equals(json.getString("return"));

            if (ok) {
                Log.i(TAG, "Karotz is playing sound");
                return true;
            }
            Log.e(TAG, "Karotz cannot play the sound");
        } catch (JSONException e) {
            Log.e(TAG, "Cannot make Karotz play a sound: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean wakeup(boolean silent) throws IOException {
        if (isAwake()) {
            // No change
            Log.d(TAG, "Already awake, no need to wake up");
            return true;
        }

        URL url = new URL(api, CGI_BIN + "/wakeup" + (silent ? "?silent=1" : ""));
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"return":"0","silent":"1"}
        try {
            JSONObject json = new JSONObject(result);
            state.setStatus("0".equals(json.getString("return")) ? KarotzStatus.AWAKE : KarotzStatus.UNKNOWN);
        } catch (JSONException e) {
            state.setStatus(KarotzStatus.UNKNOWN);
        }

        return (state.getStatus() == KarotzStatus.AWAKE);
    }

    private boolean isAwake() {
        return (state != null && state.getStatus() == KarotzStatus.AWAKE);
    }

    private boolean isOffline() {
        return (state == null || state.getStatus().isOffline());
    }

    private boolean isSleeping() {
        return (state != null && state.getStatus() == KarotzStatus.SLEEPING);
    }

    private void status() throws IOException {
        URL url = new URL(api, CGI_BIN + "/status");
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        state = new OpenKarotzState(result);
        Log.d(TAG, state.toString());
    }

    private String toColorCode(int c) {
        String cc = Integer.toHexString(c);
        while (cc.length() < 6) {
            cc = '0' + cc;
        }
        return cc;
    }


    private final String hostname;

    private URL api = null;

    private OpenKarotzState state = null;

    private static final String PROTOCOL = "http";

    private static final String CGI_BIN = "/cgi-bin";

    private static final int PORT = 80;

    private static final String TAG = "OpenKarotz";

}
