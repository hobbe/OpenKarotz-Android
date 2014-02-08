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

package com.github.hobbe.android.openkarotz.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Network utilities.
 */
public class NetUtils {

    /**
     * Given a URL, establishes an HttpUrlConnection and retrieves the web page content as a InputStream, which it
     * returns as a string.
     * 
     * @param myurl the URL to download
     * @return the url content as string
     * @throws IOException if an I/O error occurs
     */
    public static String downloadUrl(String myurl) throws IOException {
        return downloadUrl(new URL(myurl));
    }

    /**
     * Given a URL, establishes an HttpUrlConnection and retrieves the web page content as a InputStream, which it
     * returns as a string.
     * 
     * @param url the URL to download
     * @return the url content as string
     * @throws IOException if an I/O error occurs
     */
    public static String downloadUrl(URL url) throws IOException {

        InputStream is = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "Response code: " + response);

            is = conn.getInputStream();
            int len = conn.getContentLength();
            if (len < 0) {
                len = 512;
            }

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            Log.d(TAG, "Response string: " + contentAsString);

            return contentAsString;

        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Checks for availability of network connection.
     * 
     * @param activity the calling activity
     * @return {@code true} if network connection is available, {@code false} otherwise
     */
    public static final boolean isNetworkConnectionAvailable(Activity activity) {

        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        int count = reader.read(buffer);
        return new String(buffer, 0, count);
    }


    private static final String TAG = "NetUtils";
}
