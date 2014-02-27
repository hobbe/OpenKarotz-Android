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
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.github.hobbe.android.openkarotz.R;

/**
 * This {@link AsyncTask asynchronous task} is equipped with a {@link ProgressDialog progress dialog} during
 * communication with the Karotz. It is triggered on pre-execution step and dismissed on post-execution step.
 * <p>Developers can override {@link #onPreExecute()} and {@link #onPostExecute(Object)} to add custom code
 * during those steps.
 * <p>Developers can override {@link #onDialogCancelled()} to handle cancellation of progress dialog.
 */
public abstract class KarotzAsyncTask extends AsyncTask<Object, Object, Object> {

    /**
     * Initialize a new task.
     *
     * @param activity the calling activity
     */
    public KarotzAsyncTask(Activity activity) {
        this.activity = activity;
    }

    /**
     * Get this task's calling activity.
     *
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Runs on the UI thread when progress dialog is cancelled.
     */
    protected void onDialogCancelled() {
        // Empty implementation
    }

    @Override
    protected void onPostExecute(Object result) {
        pd.dismiss();
        Log.v(LOG_TAG, "Progress dialog dismissed");
    }

    @Override
    protected void onPreExecute() {
        Log.v(LOG_TAG, "Showing progress dialog...");

        // pd = ProgressDialog.show(activity, activity.getString(R.string.progress_karotz_title),
        // activity.getString(R.string.progress_karotz_description));
        pd = new ProgressDialog(activity);
        pd.setTitle(activity.getString(R.string.progress_karotz_title));
        pd.setMessage(activity.getString(R.string.progress_karotz_description));
        pd.setCancelable(true);

        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                pd.dismiss();
                Log.v(LOG_TAG, "Progress dialog dismissed by user");

                KarotzAsyncTask.this.cancel(true);
                Log.d(LOG_TAG, "Task cancelled by user");

                onDialogCancelled();
            }
        });

        pd.show();
    }


    private final Activity activity;
    private ProgressDialog pd = null;

    // Log tag
    private static final String LOG_TAG = KarotzAsyncTask.class.getSimpleName();
}
