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

package com.github.hobbe.android.openkarotz;

import java.io.IOException;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;

/**
 * System fragment.
 */
public class SystemFragment extends Fragment {

    /**
     * Initialize a new system fragment.
     */
    public SystemFragment() {
        // Nothing to initialize
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Fetch the selected page number
        int index = getArguments().getInt(DrawerFragment.ARG_PAGE_NUMBER);

        // List of pages
        String[] pages = getResources().getStringArray(R.array.pages);

        // Page title
        String pageTitle = pages[index];
        getActivity().setTitle(pageTitle);

        View view = inflater.inflate(R.layout.page_system, container, false);

        // Version
        versionTextView = (TextView) view.findViewById(R.id.textVersion);
        versionTextView.setText("-");

        new GetVersionTask().execute();

        // On/Off status
        onOffSwitch = (Switch) view.findViewById(R.id.switchOnOff);
        onOffSwitch.setOnCheckedChangeListener(new OnOffSwitchCheckedChangeListener());

        new GetStatusTask().execute();

        return view;
    }


    private class GetStatusTask extends AsyncTask<Void, Void, KarotzStatus> {

        @Override
        protected KarotzStatus doInBackground(Void... params) {

            try {
                return Karotz.getInstance().getStatus();
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz status: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(KarotzStatus result) {
            if (result != null) {
                switch (result) {
                case AWAKE:
                    onOffSwitch.setChecked(true);
                    break;
                case SLEEPING:
                    // Fall-through
                case UNKNOWN:
                    // Fall-through
                case OFFLINE:
                    // Fall-through
                default:
                    onOffSwitch.setChecked(false);
                    break;
                }
            }

            pd.dismiss();
            pd = null;
        }

        @Override
        protected void onPreExecute() {
            // TODO: I18N
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        }


        private ProgressDialog pd = null;
    }

    private class GetVersionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                return Karotz.getInstance().getVersion();
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz version: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_getversion), Toast.LENGTH_SHORT).show();
            } else {
                versionTextView.setText(result);
            }
        }
    }

    private class OnOffSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public OnOffSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "ON/OFF " + (isChecked ? "" : "un") + "checked");

            if (isChecked) {
                new WakeupTask().execute();
            } else {
                new SleepTask().execute();
            }
        }
    }

    private class SleepTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... urls) {

            try {
                Karotz.getInstance().sleep();
                // Return OK
                return Boolean.TRUE;
            } catch (IOException e) {
                Log.e(TAG, "Cannot wake up Karotz: " + e.getMessage());
                // Return Error
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            pd.dismiss();
            pd = null;

            if (Boolean.FALSE.equals(result)) {
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_wakeup), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO: I18N
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        }


        private ProgressDialog pd = null;
    }

    private class WakeupTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... urls) {

            try {
                Karotz.getInstance().wakeup(true);
                // Return OK
                return Boolean.TRUE;
            } catch (IOException e) {
                Log.e(TAG, "Cannot put Karotz to sleep: " + e.getMessage());
                // Return Error
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            pd.dismiss();
            pd = null;

            if (Boolean.FALSE.equals(result)) {
                onOffSwitch.setChecked(false);
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_sleep), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO: I18N
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        }


        private ProgressDialog pd = null;
    }


    private Switch onOffSwitch = null;

    private TextView versionTextView = null;

    private static final String TAG = "SystemFragment";
}
