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

import android.app.Activity;
import android.app.Fragment;
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
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;
import com.github.hobbe.android.openkarotz.task.GetVersionAsyncTask;
import com.github.hobbe.android.openkarotz.task.SleepAsyncTask;
import com.github.hobbe.android.openkarotz.task.WakeupAsyncTask;

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

        initializeView(view);

        new GetStatusTask(getActivity()).execute();
        new GetVersionTask(getActivity()).execute();

        return view;
    }

    private void initializeOnOffSwitch(View view) {
        onOffSwitch = (Switch) view.findViewById(R.id.switchOnOff);
        onOffSwitchCheckedChangeListener = new OnOffSwitchCheckedChangeListener();
        onOffSwitch.setOnCheckedChangeListener(onOffSwitchCheckedChangeListener);
    }

    private void initializeVersioNtextView(View view) {
        versionTextView = (TextView) view.findViewById(R.id.textVersion);
        versionTextView.setText("-");
    }

    private void initializeView(View view) {
        // Version
        initializeVersioNtextView(view);

        // On/Off status
        initializeOnOffSwitch(view);
    }


    private class GetStatusTask extends GetStatusAsyncTask {

        public GetStatusTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            KarotzStatus status = (KarotzStatus) result;
            onOffSwitch.setChecked(status != null && status.isAwake());
        }
    }

    private class GetVersionTask extends GetVersionAsyncTask {

        public GetVersionTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            if (result == null) {
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_getversion), Toast.LENGTH_SHORT).show();
            } else {
                versionTextView.setText(result.toString());
            }
        }
    }

    private class OnOffSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public OnOffSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(LOG_TAG, "ON/OFF " + (isChecked ? "" : "un") + "checked");

            if (isChecked) {
                new WakeupTask(getActivity()).execute();
            } else {
                new SleepTask(getActivity()).execute();
            }
        }
    }

    private class SleepTask extends SleepAsyncTask {

        public SleepTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            if (Boolean.FALSE.equals(result)) {
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_sleep), Toast.LENGTH_SHORT).show();

                // Check switch, without triggering listener
                onOffSwitch.setOnCheckedChangeListener(null);
                onOffSwitch.setChecked(true);
                onOffSwitch.setOnCheckedChangeListener(onOffSwitchCheckedChangeListener);
            }
        }
    }

    private class WakeupTask extends WakeupAsyncTask {

        public WakeupTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            if (Boolean.FALSE.equals(result)) {
                Toast.makeText(SystemFragment.this.getActivity(), getString(R.string.err_cannot_wakeup), Toast.LENGTH_SHORT).show();

                // Uncheck switch, without triggering listener
                onOffSwitch.setOnCheckedChangeListener(null);
                onOffSwitch.setChecked(false);
                onOffSwitch.setOnCheckedChangeListener(onOffSwitchCheckedChangeListener);
            }
        }
    }


    private Switch onOffSwitch = null;
    private OnOffSwitchCheckedChangeListener onOffSwitchCheckedChangeListener = null;

    private TextView versionTextView = null;

    private static final String LOG_TAG = SystemFragment.class.getName();
}
