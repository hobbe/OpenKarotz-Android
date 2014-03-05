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

package com.github.hobbe.android.openkarotz.adapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ViewGroup;

import com.github.hobbe.android.openkarotz.fragment.RadioTabFragment;
import com.github.hobbe.android.openkarotz.model.RadioGroupModel;

/**
 * Adapter for pager of radio station tabs.
 */
public class RadioTabsPagerAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, OnPageChangeListener {

    /**
     * Initialize the adapter.
     * @param activity the parent activity
     * @param pager the associated view pager
     * @param groups a list of radio groups
     */
    public RadioTabsPagerAdapter(FragmentActivity activity, ViewPager pager, RadioGroupModel[] groups) {
        super(activity.getSupportFragmentManager());

        this.actionBar = activity.getActionBar();

        this.viewPager = pager;
        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);

        this.fragments = new RadioTabFragment[groups.length];
        this.groups = groups;

        // Clean up current action bar
        actionBar.removeAllTabs();

        // Adding Tabs
        for (RadioGroupModel group : groups) {
            addTab(actionBar.newTab(), group);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RadioTabFragment f = (RadioTabFragment) object;
        f.getFragmentManager().beginTransaction().remove(f).commit();

        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return groups == null ? 0 : groups.length;
    }

    /**
     * Get the list of radio groups.
     * @return the groups
     */
    public RadioGroupModel[] getGroups() {
        return groups;
    }

    @Override
    public Fragment getItem(int position) {
        Log.v(LOG_TAG, "Getting fragment at position " + position);
        RadioGroupModel group = groups[position];

        RadioTabFragment fragment = fragments[position];
        if (fragment == null) {
            Log.v(LOG_TAG, "Creating fragment for group: " + group.getName());
            fragment = RadioTabFragment.newInstance(group);
            fragments[position] = fragment;
        }

        return fragment;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Nothing to do
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Nothing to do
    }

    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // Nothing to do
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Object tag = tab.getTag();
        for (int i = 0; i < groups.length; i++) {
            if (groups[i] == tag) {
                viewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // Nothing to do
    }

    private void addTab(Tab tab, RadioGroupModel group) {
        tab.setText(group.getName());
        tab.setTag(group);
        tab.setTabListener(this);

        actionBar.addTab(tab);
        notifyDataSetChanged();
    }


    private final ActionBar actionBar;

    private final ViewPager viewPager;

    private final RadioTabFragment[] fragments;

    private final RadioGroupModel[] groups;

    private static final String LOG_TAG = RadioTabsPagerAdapter.class.getSimpleName();

}