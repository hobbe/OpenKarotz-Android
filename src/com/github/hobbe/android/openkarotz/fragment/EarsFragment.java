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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.github.hobbe.android.openkarotz.R;
import com.github.hobbe.android.openkarotz.activity.MainActivity;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.EarMode;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.EarPosition;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.task.EarModeAsyncTask;
import com.github.hobbe.android.openkarotz.task.EarsAsyncTask;
import com.github.hobbe.android.openkarotz.task.EarsRandomAsyncTask;
import com.github.hobbe.android.openkarotz.task.EarsResetAsyncTask;
import com.github.hobbe.android.openkarotz.task.GetEarModeAsyncTask;
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;
import com.github.hobbe.android.openkarotz.widget.RotaryKnob;
import com.github.hobbe.android.openkarotz.widget.RotaryKnob.RotaryKnobListener;

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

        if (savedInstanceState == null) {
            new GetStatusTask(getActivity()).execute();
            new GetEarModeTask(getActivity()).execute();
        }
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

    private void initializeEarsKnob(View view) {
        earsKnob = (RotaryKnob) view.findViewById(R.id.rotaryKnobEars);

        earsKnob.setKnobListener(new RotaryKnobListener() {

            @Override
            public void onKnobChanged(int direction, int angle) {
                // TODO: show position in TextView?
            }

            @Override
            public void onKnobReleased(int direction, int angle) {
                EarPosition pos = EarPosition.fromAngle(angle);
                // Log.v(LOG_TAG, "Knob released on " + angle + "°, ear position " + pos.toString());
                new RotateEarsTask(getActivity(), pos).execute();
            }
        });
    }

    private void initializeEarsRandom(View view) {
        earsRandomButton = (ImageButton) view.findViewById(R.id.buttonEarsRandom);

        earsRandomButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new EarsRandomTask(getActivity()).execute();
            }
        });
    }

    private void initializeEarsReset(View view) {
        earsResetButton = (ImageButton) view.findViewById(R.id.buttonEarsReset);

        earsResetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new EarsResetTask(getActivity()).execute();
            }
        });
    }

    private void initializeView(View view) {
        // Ears knob
        initializeEarsKnob(view);

        // Ears reset
        initializeEarsReset(view);

        // Ears random
        initializeEarsRandom(view);

        // Ears disabled
        initializeEarsDisabled(view);
    }

    private void setEnableFields(boolean enable) {
        earsResetButton.setEnabled(enable);
        earsRandomButton.setEnabled(enable);
        // earsDisabledSwitch.setEnabled(enable);
    }


    private class EarModeTask extends EarModeAsyncTask {

        public EarModeTask(Activity activity, EarMode mode) {
            super(activity, mode);
        }

        @Override
        public void postExecute(Object result) {
            EarMode newMode = (EarMode) result;
            if (newMode != null) {
                // Check switch, without triggering listener
                earsDisabledSwitch.setOnCheckedChangeListener(null);
                earsDisabledSwitch.setChecked(newMode.isDisabled());
                setEnableFields(newMode.isEnabled());
                earsDisabledSwitch.setOnCheckedChangeListener(earsDisabledSwitchCheckedChangeListener);
            }
        }
    }

    private class EarsDisabledSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public EarsDisabledSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(LOG_TAG, "Ears disabled switch " + (isChecked ? "" : "un") + "checked");
            new EarModeTask(getActivity(), isChecked ? EarMode.DISABLED : EarMode.ENABLED).execute();
        }
    }

    private class EarsRandomTask extends EarsRandomAsyncTask {

        public EarsRandomTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            super.postExecute(result);

            if (result != null) {
                EarPosition[] positions = (EarPosition[]) result;
                // Note: currently one one knob for both ears
                int angle = positions[0].toAngle();
                Log.v(LOG_TAG, "Setting angle to " + angle + "°");
                earsKnob.setAngle(angle * 1.0f);
            }
        }
    }

    private class EarsResetTask extends EarsResetAsyncTask {

        public EarsResetTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            super.postExecute(result);

            earsKnob.setAngle(0f);
        }
    }

    private class GetEarModeTask extends GetEarModeAsyncTask {

        public GetEarModeTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            EarMode earMode = (EarMode) result;
            if (earMode != null) {
                earsDisabledSwitch.setChecked(earMode.isDisabled());
            }
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

    private class RotateEarsTask extends EarsAsyncTask {

        public RotateEarsTask(Activity activity, EarPosition position) {
            // Note: same position for both ears
            super(activity, position, position);
        }

        @Override
        public void postExecute(Object result) {
            // TODO: Nothing to do?
        }
    }


    private RotaryKnob earsKnob = null;
    private ImageButton earsResetButton = null;
    private ImageButton earsRandomButton = null;
    private Switch earsDisabledSwitch = null;
    private EarsDisabledSwitchCheckedChangeListener earsDisabledSwitchCheckedChangeListener = null;

    private static final String LOG_TAG = EarsFragment.class.getSimpleName();
}
