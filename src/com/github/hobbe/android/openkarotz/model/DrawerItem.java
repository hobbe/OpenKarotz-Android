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

package com.github.hobbe.android.openkarotz.model;

/**
 * This class represent an element of the navigation drawer.
 */
public class DrawerItem {

    /**
     * Initialize an empty item.
     */
    public DrawerItem() {
    }

    /**
     * Initialize an item with title and icon.
     * @param title the item's title
     * @param icon the item's icon resource ID
     */
    public DrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    /**
     * Get the item icon.
     * @return the item's icon resource ID
     */
    public int getIcon() {
        return this.icon;
    }

    /**
     * Get the item title.
     * @return the item's title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the item icon.
     * @param icon the item's icon resource ID
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * Set the item title.
     * @param title the item's title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    private int icon = 0;
    private String title = null;

}