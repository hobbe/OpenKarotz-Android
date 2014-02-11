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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.github.hobbe.android.openkarotz.R;

/**
 * This {@link AsyncTask asynchronous task} is equipped with a {@link ProgressDialog progress dialog}
 * during communication with the Karotz. It is triggered on pre-execution step and dismissed on
 * post-execution step. Developers can override {@link #preExecute()} and {@link #postExecute(Object)}
 * to add custom code during those steps.
 */
public abstract class KarotzAsyncTask extends AsyncTask<Object, Object, Object> {

    /**
     * Initialize a new task.
     * @param activity the calling activity
     */
    public KarotzAsyncTask(Activity activity) {
        this.activity = activity;
    }

    /**
     * Get this task's calling activity.
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Custom code that is executed after the task finishes, in the post-execution step.
     * @param result the task result
     */
    public void postExecute(Object result) {
        // Empty implementation
    }

    /**
     * Custom code that is executed before the task runs, in the pre-execution step.
     */
    public void preExecute() {
        // Empty implementation
    }

    @Override
    protected void onPostExecute(Object result) {
        pd.dismiss();
        pd = null;
        Log.d(LOG_TAG, "Progress dialog dismissed");

        postExecute(result);
    }

    @Override
    protected void onPreExecute() {
        Log.d(LOG_TAG, "Showing progress dialog...");
        pd = ProgressDialog.show(activity, activity.getString(R.string.progress_karotz_title),
                                 activity.getString(R.string.progress_karotz_description));

        preExecute();
    }


    private final Activity activity;
    private ProgressDialog pd = null;

    // Log tag
    private static final String LOG_TAG = KarotzAsyncTask.class.getName();
}
