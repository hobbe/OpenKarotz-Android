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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

/**
 * Appearance fragment.
 */
public class AppearanceFragment extends Fragment {

    /**
     * Initialize a new system fragment.
     */
    public AppearanceFragment() {
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

        View view = inflater.inflate(R.layout.page_appearance, container, false);

        new GetColorTask().execute();

        // Pulse status
        pulseSwitch = (Switch) view.findViewById(R.id.switchPulse);
        pulseSwitch.setOnCheckedChangeListener(new PulseSwitchCheckedChangeListener());

        new GetPulseTask().execute();

        // Color button layout
        colorLayout = (LinearLayout) view.findViewById(R.id.layoutColors);
        // colorButton.setOnClickListener();
        for (String c : COLORS) {
            int color = Color.parseColor('#' + c);
            Button btn = new Button(getActivity());
            btn.setBackgroundColor(color);
            btn.setWidth(60);
            btn.setHeight(60);
            btn.setPadding(2, 2, 2, 2);
            btn.setOnClickListener(new ColorButtonOnClickListener(color));
            colorLayout.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

        return view;
    }


    private class ColorButtonOnClickListener implements OnClickListener {

        public ColorButtonOnClickListener(int color) {
            this.color = color;
        }

        @Override
        public void onClick(View btn) {
            Log.d(TAG, "Color button clicked: " + Integer.toHexString(color));
            new LedChangeTask(color).execute();
        }


        private int color = 0;
    }

    private class GetColorTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                return Integer.valueOf(Karotz.getInstance().getColor());
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz color: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                // TODO: update color
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

    private class GetPulseTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                return Boolean.valueOf(Karotz.getInstance().isPulsing());
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz pulsing state: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null) {
                pulseSwitch.setChecked(result.booleanValue());
            }
        }
    }

    private class LedChangeTask extends AsyncTask<Void, Void, Void> {

        public LedChangeTask() throws IOException {
            this(Karotz.getInstance().getColor());
        }

        public LedChangeTask(int color) {
            this.color = color;
        }

        @Override
        protected Void doInBackground(Void... params) {

            boolean pulse = pulseSwitch.isChecked();

            try {
                Karotz.getInstance().led(color, pulse);
            } catch (IOException e) {
                Log.e(TAG, "Cannot change Karotz LED color: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Nothing?
        }


        private int color = 0;
    }

    private class PulseSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public PulseSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "Pulse ON/OFF " + (isChecked ? "" : "un") + "checked");
            LedChangeTask task = null;
            try {
                task = new LedChangeTask();
            } catch (IOException e) {
                return;
            }
            task.execute();
        }
    }


    private Switch pulseSwitch = null;
    private LinearLayout colorLayout = null;

    private static final String[] COLORS = {
            "000000", "FFFFFF", "FF0000", "00FF00", "0000FF", "FF00FF", "FFFF00", "00FFFF"
    };

    private static final String TAG = "AppearanceFragment";
}
