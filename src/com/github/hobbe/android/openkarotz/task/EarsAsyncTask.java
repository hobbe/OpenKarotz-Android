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

package com.github.hobbe.android.openkarotz.task;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;

import com.github.hobbe.android.openkarotz.karotz.IKarotz.EarPosition;
import com.github.hobbe.android.openkarotz.karotz.Karotz;

/**
 * Task to change Karotz ear positions in the background.
 */
public class EarsAsyncTask extends KarotzAsyncTask {

    /**
     * Initialize a new task.
     * 
     * @param activity the calling activity
     * @param left the left ear position to set
     * @param right the right ear position to set
     */
    public EarsAsyncTask(Activity activity, EarPosition left, EarPosition right) {
        super(activity);
        this.left = left;
        this.right = right;
    }

    /**
     * This tasks returns the ear positions.
     */
    @Override
    protected EarPosition[] doInBackground(Object... params) {

        try {
            return Karotz.getInstance().ears(left, right);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot change Karotz ear position: " + e.getMessage(), e);
            return null;
        }
    }


    private final EarPosition left;
    private final EarPosition right;

    // Log tag
    private static final String LOG_TAG = EarsAsyncTask.class.getSimpleName();
}
