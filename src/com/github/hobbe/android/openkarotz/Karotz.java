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

import com.github.hobbe.android.openkarotz.karotz.IKarotz;
import com.github.hobbe.android.openkarotz.karotz.OpenKarotz;

/**
 * Karotz instance.
 */
public class Karotz {

    private Karotz() {
        // No instance
    }

    /**
     * Get the Karotz instance.
     * 
     * @return the Karotz instance.
     */
    public static IKarotz getInstance() {
        if (k == null) {
            throw new IllegalAccessError();
        }
        return k;
    }

    /**
     * Initialize the Karotz application singleton.
     * 
     * @param hostname the Karotz hostname.
     */
    public static void initialize(String hostname) {
        k = new OpenKarotz(hostname);
    }


    private static IKarotz k = null;
}
