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
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.layout.FlowLayout;
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;
import com.github.hobbe.android.openkarotz.task.SoundAsyncTask;

/**
 * Radio fragment.
 */
public class RadioFragment extends Fragment {

    /**
     * Initialize a new radio fragment.
     */
    public RadioFragment() {
        // Nothing to initialize
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new GetStatusTask(getActivity()).execute();
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

        View view = inflater.inflate(R.layout.page_radio, container, false);

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
        radioLayout = (FlowLayout) view.findViewById(R.id.layoutRadios);

        buttonMap = new HashMap<String, ImageButton>();

        for (RadioModel radio : loadRadiosFromAsset()) {
            // Button
            ImageButton btn = (ImageButton) LayoutInflater.from(getActivity()).inflate(R.layout.button_radio, null);

            btn.setContentDescription(radio.getName());

            Bitmap bmp = loadBitmapFromAsset("radios/" + radio.getId() + ".png");
            if (bmp != null) {
                btn.setImageBitmap(bmp);
            } else {
                // TODO: Use 80x80 default radio image
                btn.setImageResource(android.R.drawable.ic_btn_speak_now);
            }

            btn.setOnClickListener(new RadioButtonOnClickListener(radio.getUrl()));

            buttonMap.put(radio.id, btn);
            radioLayout.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initializeView(View view) {
        // Radio button layout
        initializeRadioLayout(view);
    }

    private Bitmap loadBitmapFromAsset(String filename) {
        Log.d(LOG_TAG, "Loading bitmap asset " + filename);

        Bitmap bmp = null;
        InputStream is = null;

        try {
            is = getActivity().getAssets().open(filename);
            bmp = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not load bitmap asset " + filename, e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }

        return bmp;
    }

    private JSONObject loadJsonFromAsset(String filename) {
        JSONObject json = null;
        InputStream is = null;

        try {
            is = getActivity().getAssets().open(filename);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            String content = new String(buffer, "UTF-8");
            json = new JSONObject(content);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not load JSON asset " + filename, e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse JSON from asset " + filename, e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }

        return json;
    }

    private RadioModel[] loadRadiosFromAsset() {

        try {
            JSONObject json = loadJsonFromAsset("radios.json");
            JSONObject france = (JSONObject) json.get("france");
            JSONArray list = france.getJSONArray("radios");

            int count = list.length();
            RadioModel[] radios = new RadioModel[count];

            for (int i = 0; i < count; i++) {
                JSONObject element = list.getJSONObject(i);
                String id = element.getString("id");
                String name = element.getString("name");
                String url = element.getString("url");
                RadioModel radio = new RadioModel(id, name, url);

                radios[i] = radio;
            }

            Log.i(LOG_TAG, "Using radio list from asset radios.json");
            return radios;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse JSON content: " + e.getMessage(), e);
        }

        Log.i(LOG_TAG, "Using default radio list");
        // Return default radios
        return new RadioModel[] {};
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

        public PlayRadioTask(Activity activity, String url) {
            super(activity, url);
        }
    }

    private class RadioButtonOnClickListener implements OnClickListener {

        public RadioButtonOnClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View btn) {
            Log.d(LOG_TAG, "Radio button clicked: " + url);
            new PlayRadioTask(getActivity(), url).execute();
        }


        private final String url;
    }

    private class RadioModel {

        public RadioModel(String id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }

        /**
         * Get the identifier.
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Get the name.
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Get the URL.
         * @return the url
         */
        public String getUrl() {
            return url;
        }


        private final String id;
        private final String name;
        private final String url;
    }


    private FlowLayout radioLayout = null;

    private HashMap<String, ImageButton> buttonMap = null;

    private static final String LOG_TAG = RadioFragment.class.getName();

}
