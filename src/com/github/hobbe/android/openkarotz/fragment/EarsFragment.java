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

package com.github.hobbe.android.openkarotz.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.github.hobbe.android.openkarotz.R;
import com.github.hobbe.android.openkarotz.activity.MainActivity;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;

/**
 * Ears fragment.
 */
public class EarsFragment extends Fragment {

    /**
     * Initialize a new ears fragment.
     */
    public EarsFragment() {
        // Nothing to do
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new GetStatusTask(getActivity()).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Fetch the selected page number
        int index = getArguments().getInt(MainActivity.ARG_PAGE_NUMBER);

        // List of pages
        String[] pages = getResources().getStringArray(R.array.pages);

        // Page title
        String pageTitle = pages[index];
        getActivity().setTitle(pageTitle);

        View view = inflater.inflate(R.layout.page_ears, container, false);

        initializeView(view);

        return view;
    }

    private void disableFields() {
        setEnableFields(false);
    }

    private void enableFields() {
        setEnableFields(true);
    }

    private void initializeEarsDisabled(View view) {
        earsDisabledSwitch = (Switch) view.findViewById(R.id.switchEarsDisabled);
        earsDisabledSwitchCheckedChangeListener = new EarsDisabledSwitchCheckedChangeListener();
        earsDisabledSwitch.setOnCheckedChangeListener(earsDisabledSwitchCheckedChangeListener);
    }

    private void initializeEarsReset(View view) {
        earsResetButton = (Button) view.findViewById(R.id.buttonEarsReset);
    }

    private void initializeView(View view) {
        // Ears reset
        initializeEarsReset(view);

        // Ears disabled
        initializeEarsDisabled(view);
    }

    private void setEnableFields(boolean enable) {
        // TODO
    }


    private class EarsDisabledSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public EarsDisabledSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(LOG_TAG, "Ears disabled switch " + (isChecked ? "" : "un") + "checked");

            // new DisableEarsTask(getActivity(), isChecked).execute();
        }
    }

    private class GetStatusTask extends GetStatusAsyncTask {

        public GetStatusTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            KarotzStatus status = (KarotzStatus) result;
            boolean awake = (status != null && status.isAwake());
            if (awake) {
                enableFields();
            } else {
                disableFields();
            }
        }
    }


    private Button earsResetButton = null;
    private Switch earsDisabledSwitch = null;
    private EarsDisabledSwitchCheckedChangeListener earsDisabledSwitchCheckedChangeListener = null;

    private static final String LOG_TAG = EarsFragment.class.getName();

}