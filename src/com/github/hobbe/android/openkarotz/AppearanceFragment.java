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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.github.hobbe.android.openkarotz.task.GetColorAsyncTask;
import com.github.hobbe.android.openkarotz.task.GetPulseAsyncTask;
import com.github.hobbe.android.openkarotz.task.LedAsyncTask;

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

        // Pulse status
        initializePulseSwitch(view);

        // Color button layout
        initializeColorLayout(view);

        // Load default values
        new GetPulseTask(getActivity()).execute();
        new GetColorTask(getActivity()).execute();

        return view;
    }

    private void initializeColorLayout(View view) {
        colorLayout = (LinearLayout) view.findViewById(R.id.layoutColors);

        for (String c : COLORS) {
            int color = Color.parseColor('#' + c);

            // Button
            Button btn = new ColorButton(getActivity(), color);
            btn.setOnClickListener(new ColorButtonOnClickListener(color));

            colorLayout.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initializePulseSwitch(View view) {
        pulseSwitch = (Switch) view.findViewById(R.id.switchPulse);
        pulseSwitchCheckedChangeListener = new PulseSwitchCheckedChangeListener();
        pulseSwitch.setOnCheckedChangeListener(pulseSwitchCheckedChangeListener);
    }


    private class ColorButton extends Button {

        public ColorButton(Context context, int color) {
            super(context);
            setId(color);
            setWidth(100);
            setHeight(100);

            gdNormal = new GradientDrawable();
            gdNormal.setColors(new int[] {
                    color, Color.WHITE
            });
            gdNormal.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gdNormal.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            gdNormal.setCornerRadius(12);
            gdNormal.setStroke(1, Color.GRAY);

            gdTouch = new GradientDrawable();
            gdTouch.setColors(new int[] {
                    color, Color.LTGRAY
            });
            gdTouch.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gdTouch.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            gdTouch.setCornerRadius(12);
            gdTouch.setStroke(1, Color.DKGRAY);

            setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setBackground(gdTouch);
                    } else {
                        setBackground(gdNormal);
                    }
                    // Do not consume event so that the onClick handler works
                    return false;
                }
            });

            setBackground(gdNormal);
        }


        private final GradientDrawable gdNormal;
        private final GradientDrawable gdTouch;
    }

    private class ColorButtonOnClickListener implements OnClickListener {

        public ColorButtonOnClickListener(int color) {
            this.color = color;
        }

        @Override
        public void onClick(View btn) {
            Log.d(LOG_TAG, "Color button clicked: " + Integer.toHexString(color));
            boolean pulse = pulseSwitch.isChecked();
            new LedChangeTask(getActivity(), color, pulse).execute();
        }


        private int color = 0;
    }

    private class GetColorTask extends GetColorAsyncTask {

        public GetColorTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            if (result != null) {
                // TODO: update color selection
            }
        }
    }

    private class GetPulseTask extends GetPulseAsyncTask {

        public GetPulseTask(Activity activity) {
            super(activity);
        }

        @Override
        public void postExecute(Object result) {
            boolean pulsing = ((Boolean) result).booleanValue();
            if (pulsing) {
                // Check switch, without triggering listener
                pulseSwitch.setOnCheckedChangeListener(null);
                pulseSwitch.setChecked(pulsing);
                pulseSwitch.setOnCheckedChangeListener(pulseSwitchCheckedChangeListener);
            }
        }
    }

    private class LedChangeTask extends LedAsyncTask {

        public LedChangeTask(Activity activity, boolean pulse) throws IOException {
            this(activity, Karotz.getInstance().getColor(), pulse);
        }

        public LedChangeTask(Activity activity, int color, boolean pulse) {
            super(activity, color, pulse);
        }
    }

    private class PulseSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public PulseSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(LOG_TAG, "Pulse ON/OFF " + (isChecked ? "" : "un") + "checked");
            LedChangeTask task = null;
            try {
                task = new LedChangeTask(getActivity(), isChecked);
            } catch (IOException e) {
                return;
            }
            task.execute();
        }
    }


    private Switch pulseSwitch = null;
    private PulseSwitchCheckedChangeListener pulseSwitchCheckedChangeListener = null;

    private LinearLayout colorLayout = null;

    private static final String[] COLORS = {
            "FF0000", "00FF00", "0000FF", "FF00FF", "FFFF00", "00FFFF", "FFFFFF", "000000"
    };

    private static final String LOG_TAG = AppearanceFragment.class.getName();

}
