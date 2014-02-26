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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.github.hobbe.android.openkarotz.fragment.RadioTabFragment;
import com.github.hobbe.android.openkarotz.model.RadioGroupModel;

/**
 * Adapter for pager of radio station tabs.
 */
public class RadioTabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Initialize the adapter.
     * @param fm the fragment manager
     * @param groups a list of radio groups
     */
    public RadioTabsPagerAdapter(FragmentManager fm, RadioGroupModel[] groups) {
        super(fm);

        this.groups = groups;
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
    public Fragment getItem(int index) {
        Log.v(LOG_TAG, "Getting fragment at index " + index);
        RadioGroupModel group = groups[index];

        Log.v(LOG_TAG, "Creating fragment for group " + group.getName());
        Fragment fragment = RadioTabFragment.newInstance(group);
        return fragment;
    }


    private final RadioGroupModel[] groups;

    private static final String LOG_TAG = RadioTabsPagerAdapter.class.getSimpleName();
}