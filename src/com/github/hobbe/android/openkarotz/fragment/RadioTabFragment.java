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

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.hobbe.android.openkarotz.R;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.layout.FlowLayout;
import com.github.hobbe.android.openkarotz.model.RadioGroupModel;
import com.github.hobbe.android.openkarotz.model.RadioModel;
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;
import com.github.hobbe.android.openkarotz.task.SoundAsyncTask;
import com.github.hobbe.android.openkarotz.util.AssetUtils;

/**
 * Tab fragment for radio buttons.
 */
public class RadioTabFragment extends Fragment {

    /**
     * Initialize a new radio fragment.
     */
    public RadioTabFragment() {
        // Nothing to do
    }

    public static RadioTabFragment newInstance(RadioGroupModel group) {
        RadioTabFragment fragment = new RadioTabFragment();

        Bundle bundle = new Bundle();
        // bundle.putString("id", group.getId());
        // bundle.putString("name", group.getName());
        bundle.putSerializable(RadioTabFragment.KEY_GROUP, group);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new GetStatusTask(getActivity()).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_radio, container, false);

        this.group = (RadioGroupModel) getArguments().getSerializable(KEY_GROUP);

        initializeView(view);

        return view;
    }

    private void disableFields() {
        setEnableFields(false);
    }

    private void enableFields() {
        setEnableFields(true);
    }

    private void initializeRadioLayout(View view) {
        if (group == null) {
            return;
        }

        radioLayout = (FlowLayout) view.findViewById(R.id.layoutRadios);

        buttonMap.clear();

        for (RadioModel radio : group.getRadios()) {
            // Button
            ImageButton btn = (ImageButton) LayoutInflater.from(getActivity()).inflate(R.layout.button_radio, null);

            btn.setContentDescription(radio.getName());

            Bitmap bmp = AssetUtils.loadBitmapFromAsset(getActivity(), "radios/" + radio.getId() + ".png");
            if (bmp != null) {
                btn.setImageBitmap(bmp);
            } else {
                // TODO: Use 80x80 default radio image
                btn.setImageResource(android.R.drawable.ic_btn_speak_now);
            }

            btn.setOnClickListener(new RadioButtonOnClickListener(radio.getUrl(), radio.getName()));

            buttonMap.put(radio.getId(), btn);
            radioLayout.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initializeView(View view) {
        // Radio button layout
        initializeRadioLayout(view);
    }

    private void setEnableFields(boolean enable) {
        for (ImageButton button : buttonMap.values()) {
            button.setEnabled(enable);
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

    private class PlayRadioTask extends SoundAsyncTask {

        public PlayRadioTask(Activity activity, String url, String name) {
            super(activity, url);
            this.name = name;
        }

        @Override
        public void postExecute(Object result) {
            Toast.makeText(getActivity(), getString(R.string.radio_starting) + " " + name, Toast.LENGTH_SHORT).show();
        }


        private final String name;
    }

    private class RadioButtonOnClickListener implements OnClickListener {

        public RadioButtonOnClickListener(String url, String name) {
            this.url = url;
            this.name = name;
        }

        @Override
        public void onClick(View btn) {
            Log.d(LOG_TAG, "Radio button clicked: " + url);
            new PlayRadioTask(getActivity(), url, name).execute();
        }


        private final String url;
        private final String name;
    }


    /** Bundle key for group. */
    public static final String KEY_GROUP = "group";

    private FlowLayout radioLayout = null;

    private RadioGroupModel group;
    private final Map<String, ImageButton> buttonMap = new HashMap<String, ImageButton>();

    private static final String LOG_TAG = RadioTabFragment.class.getName();

}
