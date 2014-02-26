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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.hobbe.android.openkarotz.R;
import com.github.hobbe.android.openkarotz.activity.MainActivity;
import com.github.hobbe.android.openkarotz.adapter.RadioTabsPagerAdapter;
import com.github.hobbe.android.openkarotz.model.RadioGroupModel;
import com.github.hobbe.android.openkarotz.model.RadioModel;
import com.github.hobbe.android.openkarotz.util.AssetUtils;

/**
 * Radio fragment.
 */
public class RadioFragment extends Fragment implements TabListener {

    /**
     * Initialize a new radio fragment.
     */
    public RadioFragment() {
        // Nothing to initialize
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, bundle: " + savedInstanceState);
        super.onCreate(savedInstanceState);

        radioGroups = loadRadios();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, bundle: " + savedInstanceState);

        // Fetch the selected page number
        int index = getArguments().getInt(MainActivity.ARG_PAGE_NUMBER);

        // List of pages
        String[] pages = getResources().getStringArray(R.array.pages);

        // Page title
        String pageTitle = pages[index];
        getActivity().setTitle(pageTitle);

        // Action bar tab navigation
        actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        View view = inflater.inflate(R.layout.page_radio, container, false);

        initializeView(view);

        return view;
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "onPause");
        super.onPause();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume");
        super.onResume();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // Nothing to do
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.v(LOG_TAG, "onTabSelected position #" + tab.getPosition());
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // Nothing to do
    }

    private void initializePagerAdapter(View view) {
        Log.v(LOG_TAG, "Initializing pager adapter");

        pagerAdapter = new RadioTabsPagerAdapter(getActivity().getSupportFragmentManager(), radioGroups);

        // Adding Tabs
        actionBar.removeAllTabs();
        for (RadioGroupModel group : radioGroups) {
            actionBar.addTab(actionBar.newTab().setText(group.getName()).setTabListener(this));
        }
    }

    private void initializeView(View view) {
        Log.v(LOG_TAG, "Initializing radio fragment view");

        // View pager
        initializeViewPager(view);

        // Pager adapter
        initializePagerAdapter(view);

        // Associate viewer and adapter
        viewPager.setAdapter(pagerAdapter);
    }

    private void initializeViewPager(View view) {
        Log.v(LOG_TAG, "Initializing view pager");

        viewPager = (ViewPager) view.findViewById(R.id.pagerRadio);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // Nothing to do
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // Nothing to do
            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
    }

    private RadioGroupModel[] loadRadios() {
        Log.d(LOG_TAG, "Loading radio list");

        RadioGroupModel[] radios = null;

        try {
            JSONObject json = AssetUtils.loadJsonFromAsset(getActivity(), "radios.json");
            JSONArray list = json.getJSONArray("radios");

            int count = list.length();
            radios = new RadioGroupModel[count];

            for (int i = 0; i < count; i++) {
                JSONObject element = list.getJSONObject(i);
                RadioGroupModel group = loadRadioGroup(element);
                if (group != null) {
                    Log.v(LOG_TAG, "Loaded radio group " + group.getName());
                    radios[i] = group;
                }
            }

            Log.i(LOG_TAG, "Using radio list from asset radios.json");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse JSON content: " + e.getMessage(), e);
        }

        return radios;
    }

    /**
     * Load a radio group.
     *
     * @param json the JSON object containing the radio group definition
     * @return the radio group
     */
    private static RadioGroupModel loadRadioGroup(JSONObject json) {

        RadioGroupModel group = null;
        try {
            String groupId = json.getString("id");
            String groupName = json.getString("name");
            Log.d(LOG_TAG, "Loading radio group: " + groupName);

            group = new RadioGroupModel(groupId, groupName);

            JSONArray list = json.getJSONArray("radios");

            int count = list.length();
            for (int i = 0; i < count; i++) {
                JSONObject element = list.getJSONObject(i);
                String id = element.getString("id");
                String name = element.getString("name");
                String url = element.getString("url");

                Log.v(LOG_TAG, "Adding radio " + name + " to group " + group.getName());
                group.addRadio(new RadioModel(id, name, url));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse JSON content: " + e.getMessage(), e);
        }

        return group;
    }


    private ViewPager viewPager = null;
    private RadioTabsPagerAdapter pagerAdapter = null;
    private ActionBar actionBar = null;

    private RadioGroupModel[] radioGroups = null;

    private static final String LOG_TAG = RadioFragment.class.getSimpleName();
}
