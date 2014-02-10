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
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.hobbe.android.openkarotz.karotz.IKarotz.KarotzStatus;
import com.github.hobbe.android.openkarotz.net.NetUtils;

/**
 * Main activity.
 */
public class MainActivity extends Activity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = drawerTitle = getTitle();
        pageTitles = getResources().getStringArray(R.array.pages);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList = (ListView) findViewById(R.id.drawer_list);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerToggle = new DrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        // Creating an ArrayAdapter to add items to the drawer list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.drawer_list_item,
                getResources().getStringArray(R.array.pages));

        // Setting the adapter on drawerList
        drawerList.setAdapter(adapter);

        // Setting item click listener for the drawer list view
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enabling Home button
        getActionBar().setHomeButtonEnabled(true);

        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

        // Check network connection
        if (NetUtils.isNetworkConnectionAvailable(this)) {

            // Set up Karotz instance
            new GetStatusTask().execute();

        } else {
            Toast.makeText(MainActivity.this, getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
        }
    }

    private void doActionAbout() {
        Toast.makeText(MainActivity.this, getString(R.string.version) + " " + getVersion(), Toast.LENGTH_LONG).show();
    }

    private void doActionSettings() {
        Log.d(TAG, "Launching settings activity...");
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, RESULT_SETTINGS);
    }

    private String getPrefKarotzHost() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String h = prefs.getString(SettingsActivity.KEY_PREF_KAROTZ_HOST, null);
        if (h != null && h.length() <= 0) {
            h = null;
        }
        return h;
    }

    private String getVersion() {
        String versionName = "0.0";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            // Ignored
        }
        return versionName;
    }

    private void initializeKarotz() {
        Log.d(TAG, "Initializing Karotz...");
        String hostname = getPrefKarotzHost();
        if (hostname == null) {
            Log.d(TAG, "Preference Karotz Host not set");
            doActionSettings();
        }

        Karotz.initialize(hostname);
    }

    /**
     * @param position
     */
    private void selectDrawerItem(int position) {

        // Create a new fragment based on position
        Fragment fragment = null;

        switch (position) {
        case PAGE_APPEARANCE:
            fragment = new AppearanceFragment();
            break;

        case PAGE_SYSTEM:
            fragment = new SystemFragment();
            break;

        default:
            fragment = new DrawerFragment();
            break;
        }

        Bundle args = new Bundle();
        args.putInt(DrawerFragment.ARG_PAGE_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(pageTitles[position]);

        // Closing the drawer
        drawerLayout.closeDrawer(drawerList);
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
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            getActionBar().setTitle(appTitle);
            invalidateOptionsMenu();
        }

        /*
         * Called when a drawer has settled in a completely open state.
         */
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            getActionBar().setTitle(drawerTitle);
            invalidateOptionsMenu();
        }
    }

    private class GetStatusTask extends AsyncTask<Void, Void, KarotzStatus> {

        @Override
        protected KarotzStatus doInBackground(Void... params) {

            // Set up Karotz instance
            initializeKarotz();

            try {
                return Karotz.getInstance().getStatus();
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz status: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(KarotzStatus result) {
            pd.dismiss();
            pd = null;

            // Check Karotz status
            if (result != null) {
                switch (result) {
                case AWAKE:
                    // Fall-through
                case SLEEPING:
                    break;
                case UNKNOWN:
                    // Fall-through
                case OFFLINE:
                    // Fall-through
                default:
                    Toast.makeText(MainActivity.this, getString(R.string.err_cannot_connect), Toast.LENGTH_LONG).show();
                    break;
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.err_cannot_connect), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO: I18N
            pd = ProgressDialog.show(MainActivity.this, "Loading", "Please wait...");
        }


        private ProgressDialog pd = null;
    }


    private CharSequence appTitle = null;

    private CharSequence drawerTitle = null;

    private String[] pageTitles = null;

    private DrawerLayout drawerLayout = null;

    private ListView drawerList = null;

    private ActionBarDrawerToggle drawerToggle = null;

    private static final int RESULT_SETTINGS = 1;

    private static final int PAGE_APPEARANCE = 0;

    private static final int PAGE_SYSTEM = 1;

    private static final String TAG = "MainActivity";
}
