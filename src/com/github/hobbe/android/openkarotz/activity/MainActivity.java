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

package com.github.hobbe.android.openkarotz.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.github.hobbe.android.openkarotz.R;
import com.github.hobbe.android.openkarotz.adapter.DrawerListAdapter;
import com.github.hobbe.android.openkarotz.fragment.ColorFragment;
import com.github.hobbe.android.openkarotz.fragment.EarsFragment;
import com.github.hobbe.android.openkarotz.fragment.HomeFragment;
import com.github.hobbe.android.openkarotz.fragment.RadioFragment;
import com.github.hobbe.android.openkarotz.fragment.SystemFragment;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.karotz.IKarotz.SoundControlCommand;
import com.github.hobbe.android.openkarotz.karotz.Karotz;
import com.github.hobbe.android.openkarotz.model.DrawerItem;
import com.github.hobbe.android.openkarotz.net.NetUtils;
import com.github.hobbe.android.openkarotz.task.GetStatusAsyncTask;
import com.github.hobbe.android.openkarotz.task.SoundControlAsyncTask;

/**
 * Main activity.
 */
public class MainActivity extends FragmentActivity {

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();

            // Any going back?
            if (count > 0) {
                fragmentManager.popBackStack();
                return;
            }
        }

        // Else default handling
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Reload configuration of drawer
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other action bar items...
        switch (item.getItemId()) {
        case R.id.action_sound_stop:
            doActionStopSound();
            return true;
        case R.id.action_settings:
            doActionSettings();
            return true;
        case R.id.action_about:
            doActionAbout();
            return true;
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the navigation drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_sound_stop).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getActionBar().setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case RESULT_SETTINGS:
            // Nothing to do
            break;
        default:
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, bundle: " + savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.addOnBackStackChangedListener(new BackStackChangedListener());
        }

        appTitle = drawerTitle = getTitle();
        pageTitles = getResources().getStringArray(R.array.pages);

        initializeDrawer();
        initializeActionBar();

        if (savedInstanceState == null) {
            // Select first page (Home)
            selectDrawerItem(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onPostCreate, bundle: " + savedInstanceState);
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

        // Disable all fields
        disableFields();

        // Check network connection
        if (NetUtils.isNetworkConnectionAvailable(this)) {

            // Set up Karotz instance
            initializeKarotz();

            GetStatusTask task = new GetStatusTask(this);
            task.execute();

        } else {
            Toast.makeText(MainActivity.this, getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Disable fields.
     */
    private void disableFields() {
        setFieldsEnabled(false);
    }

    private void doActionAbout() {
        Toast.makeText(MainActivity.this, getString(R.string.version) + " " + getVersion(), Toast.LENGTH_LONG).show();
    }

    private void doActionSettings() {
        Log.d(LOG_TAG, "Launching settings activity...");
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, RESULT_SETTINGS);
    }

    private void doActionStopSound() {
        new SoundControlAsyncTask(this, SoundControlCommand.STOP).execute();
    }

    /**
     * Enable fields.
     */
    private void enableFields() {
        setFieldsEnabled(true);
    }

    private Fragment getColorFragment() {
        if (colorFragment == null) {
            colorFragment = new ColorFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, PAGE_COLOR);
            args.putString(ARG_PAGE_TITLE, pageTitles[PAGE_COLOR]);
            colorFragment.setArguments(args);
        }
        return colorFragment;
    }

    private Fragment getEarsFragment() {
        if (earsFragment == null) {
            earsFragment = new EarsFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, PAGE_EARS);
            args.putString(ARG_PAGE_TITLE, pageTitles[PAGE_EARS]);
            earsFragment.setArguments(args);
        }
        return earsFragment;
    }

    private Fragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, PAGE_HOME);
            args.putString(ARG_PAGE_TITLE, pageTitles[PAGE_HOME]);
            homeFragment.setArguments(args);
        }
        return homeFragment;
    }

    private String getPrefKarotzHost() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String h = prefs.getString(SettingsActivity.KEY_PREF_KAROTZ_HOST, null);
        if (h != null && h.length() <= 0) {
            h = null;
        }
        return h;
    }

    private Fragment getRadioFragment() {
        if (radioFragment == null) {
            radioFragment = new RadioFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, PAGE_RADIO);
            args.putString(ARG_PAGE_TITLE, pageTitles[PAGE_RADIO]);
            radioFragment.setArguments(args);
        }
        return radioFragment;
    }

    private Fragment getSystemFragment() {
        if (systemFragment == null) {
            systemFragment = new SystemFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE_NUMBER, PAGE_SYSTEM);
            args.putString(ARG_PAGE_TITLE, pageTitles[PAGE_SYSTEM]);
            systemFragment.setArguments(args);
        }
        return systemFragment;
    }

    private String getVersion() {
        String versionName = "0.0";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot version.name from package manager: " + e.getMessage(), e);
        }
        return versionName;
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<android.support.v4.app.Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private void initializeActionBar() {
        // Enabling Home button
        getActionBar().setHomeButtonEnabled(true);

        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Put everything in place to provide the drawer: drawer layout, drawer toggle and drawer list.
     */
    private void initializeDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerToggle = new DrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

        // Set the drawer shadow
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        // Load page icons from resources
        TypedArray menuIcons = getResources().obtainTypedArray(R.array.pages_icons);

        // Create a drawer item per page
        ArrayList<DrawerItem> drawerItems = new ArrayList<DrawerItem>();
        for (int i = 0; i < pageTitles.length; i++) {
            drawerItems.add(new DrawerItem(pageTitles[i], menuIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        menuIcons.recycle();

        // Creating the adapter to add items to the drawer list view
        DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), drawerItems);

        // Setting the adapter on drawerList
        drawerList.setAdapter(adapter);

        // Setting item click listener for the drawer list view
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void initializeKarotz() {
        Log.d(LOG_TAG, "Initializing Karotz...");
        String hostname = getPrefKarotzHost();
        if (hostname == null) {
            Log.d(LOG_TAG, "Preference Karotz Host not set");
            doActionSettings();
        }

        Karotz.initialize(hostname);
    }

    private void selectDrawerItem(int position) {
        Log.v(LOG_TAG, "selectDrawerItem #" + position);

        // Create a new fragment based on position
        Fragment fragment = null;
        boolean allowBack = false;
        String tag = pageTitles[position];

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = fragmentManager.findFragmentByTag(tag);

        switch (position) {
        case PAGE_HOME:
            if (fragment == null) {
                fragment = getHomeFragment();
            }
            break;

        case PAGE_RADIO:
            if (fragment == null) {
                fragment = getRadioFragment();
            }
            allowBack = true;
            break;

        case PAGE_COLOR:
            if (fragment == null) {
                fragment = getColorFragment();
            }
            allowBack = true;
            break;

        case PAGE_EARS:
            if (fragment == null) {
                fragment = getEarsFragment();
            }
            allowBack = true;
            break;

        case PAGE_SYSTEM:
            if (fragment == null) {
                fragment = getSystemFragment();
            }
            allowBack = true;
            break;

        default:
            break;
        }

        // Insert the fragment by replacing any existing fragment
        transaction.replace(R.id.content_frame, fragment, tag);

        // Add transaction to back stack
        if (allowBack) {
            transaction.addToBackStack(null);
        }

        // Commit
        transaction.commit();

        // Set selected item in drawer
        updateDrawerSelection(position);

        // Close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    private void setFieldsEnabled(boolean enabled) {
        drawerList.setEnabled(enabled);
    }

    private void updateDrawerSelection(Fragment fragment) {
        // Fetch the selected page number
        int position = fragment.getArguments().getInt(MainActivity.ARG_PAGE_NUMBER);

        updateDrawerSelection(position);
    }

    private void updateDrawerSelection(int position) {
        // Highlight the selected item
        drawerList.setItemChecked(position, true);

        // Update the title
        setTitle(pageTitles[position]);
    }


    private class BackStackChangedListener implements OnBackStackChangedListener {

        @Override
        public void onBackStackChanged() {
            FragmentManager manager = getSupportFragmentManager();
            if (manager != null) {
                updateDrawerSelection(getVisibleFragment());
            }
        }
    }

    /**
     * Click listener for drawer item.
     */
    private final class DrawerItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }

    }

    private final class DrawerToggle extends ActionBarDrawerToggle {

        /**
         * Create the drawer toggle.
         *
         * @param activity the associated activity
         * @param layout the drawer layout
         * @param imageRes the drawer image
         * @param openDescRes the drawer description for open state
         * @param closeDescRes the drawer description for closed state
         */
        public DrawerToggle(Activity activity, DrawerLayout layout, int imageRes, int openDescRes, int closeDescRes) {
            super(activity, layout, imageRes, openDescRes, closeDescRes);
        }

        /*
         * Called when a drawer has settled in a completely closed state.
         */
        @Override
        public void onDrawerClosed(View drawer) {
            super.onDrawerClosed(drawer);
            getActionBar().setTitle(appTitle);
            invalidateOptionsMenu();
        }

        /*
         * Called when a drawer has settled in a completely open state.
         */
        @Override
        public void onDrawerOpened(View drawer) {
            super.onDrawerOpened(drawer);
            getActionBar().setTitle(drawerTitle);
            invalidateOptionsMenu();
        }
    }

    private class GetStatusTask extends GetStatusAsyncTask {

        public GetStatusTask(Activity activity) {
            super(activity);
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            KarotzStatus status = (KarotzStatus) result;

            // Check Karotz status
            if (status != null && status.isOnline()) {
                // Enable fields if Karotz is online
                enableFields();
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.err_cannot_connect), Toast.LENGTH_LONG).show();
            }
        }

    }


    private CharSequence appTitle = null;
    private CharSequence drawerTitle = null;
    private String[] pageTitles = null;

    private DrawerLayout drawerLayout = null;
    private ListView drawerList = null;
    private ActionBarDrawerToggle drawerToggle = null;

    private Fragment homeFragment;
    private Fragment radioFragment;
    private Fragment colorFragment;
    private Fragment earsFragment;
    private Fragment systemFragment;

    // Activity settings
    private static final int RESULT_SETTINGS = 1;

    /**
     * Page number argument.
     */
    public static final String ARG_PAGE_NUMBER = "position";

    /**
     * Page title argument.
     */
    public static final String ARG_PAGE_TITLE = "title";

    // Drawer pages
    private static final int PAGE_HOME = 0;
    private static final int PAGE_RADIO = 1;
    private static final int PAGE_COLOR = 2;
    private static final int PAGE_EARS = 3;
    private static final int PAGE_SYSTEM = 4;

    // Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
}
