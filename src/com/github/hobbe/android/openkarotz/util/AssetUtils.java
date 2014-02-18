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

package com.github.hobbe.android.openkarotz.util;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Utilities to manage assets.
 */
public class AssetUtils {

    /**
     * Load a bitmap image from the asset filename.
     * 
     * @param context the context
     * @param filename the name of the image
     * @return the bitmap or {@code null}
     */
    public static Bitmap loadBitmapFromAsset(Context context, String filename) {
        Log.v(LOG_TAG, "Loading bitmap asset " + filename);

        Bitmap bmp = null;
        InputStream is = null;

        try {
            is = context.getAssets().open(filename);
            bmp = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not load bitmap asset " + filename, e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }

        return bmp;
    }

    /**
     * Load a JSON resource from the asset filename.
     * 
     * @param context the context
     * @param filename the name of the JSON object
     * @return the JSON object
     */
    public static JSONObject loadJsonFromAsset(Context context, String filename) {
        JSONObject json = null;
        InputStream is = null;

        try {
            is = context.getAssets().open(filename);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            String content = new String(buffer, "UTF-8");
            json = new JSONObject(content);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not load JSON asset " + filename, e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse JSON from asset " + filename, e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }

        return json;
    }


    private static final String LOG_TAG = AssetUtils.class.getName();
}
