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

        String c = Integer.toHexString(rgb);

        URL url = new URL(api, CGI_BIN + "/leds?color=" + c + (pulse ? "&pulse=1" : ""));
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"color":"0000FF","secondary_color":"000000","pulse":"0","no_memory":"0","speed":"700","return":"0"}
        try {
            JSONObject json = new JSONObject(result);
            state.setLedColor(Color.parseColor("#" + json.getString("color")));
            state.setPulsing("1".equals(json.getString("pulse")));
        } catch (JSONException e) {
            e.printStackTrace();
            state.setLedColor(rgb);
            state.setPulsing(pulse);
        }
    }

    @Override
    public void sleep() throws IOException {
        if (isSleeping()) {
            // No change
            Log.d(TAG, "Already sleeping, no need to go to sleep");
            return;
        }

        URL url = new URL(api, CGI_BIN + "/sleep");
        Log.d(TAG, url.toString());

        String result = NetUtils.downloadUrl(url);
        Log.d(TAG, result);

        // Answer: {"return":"0"}
        try {
            JSONObject json = new JSONObject(result);
            state.setStatus("0".equals(json.getString("return")) ? KarotzStatus.SLEEPING : KarotzStatus.UNKNOWN);
        } catch (JSONException e) {
            state.setStatus(KarotzStatus.UNKNOWN);
        }
    }

    @Override
    public void wakeup(boolean silent) throws IOException {
        if (isAwake()) {
            // No change
            Log.d(TAG, "Already awake, no need to wake up");
            return;
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


    private final String hostname;
    private URL api = null;
    private OpenKarotzState state = null;

    private static final String PROTOCOL = "http";

    private static final String CGI_BIN = "/cgi-bin";

    private static final int PORT = 80;

    private static final String TAG = "OpenKarotz";

}
