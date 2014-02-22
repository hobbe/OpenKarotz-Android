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

package com.github.hobbe.android.openkarotz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.github.hobbe.android.openkarotz.R;

/**
 * Rotary knob view.
 * <p>
 * Inspired by <a href="from http://go-lambda.blogspot.fr/2012/02/rotary-knob-widget-on-android.html">RotaryKnobView
 * example</a>.
 */
public class RotaryKnob extends ImageView {

    /**
     * Initiliaze the widget.
     * @param context the context
     */
    public RotaryKnob(Context context) {
        super(context);
        initialize();
    }

    /**
     * Initiliaze the widget.
     * @param context the context
     * @param attrs attribute set
     */
    public RotaryKnob(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * Initiliaze the widget.
     * @param context the context
     * @param attrs attribute set
     * @param defStyle default style
     */
    public RotaryKnob(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Get the knob angle.
     * @return the knob angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Set the knob listener.
     * @param l the listener to set
     */
    public void setKnobListener(RotaryKnobListener l) {
        listener = l;
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(angle, getWidth() / 2, getHeight() / 2);
        super.onDraw(c);
    }

    private float getTheta(float x, float y) {
        float sx = x - (getWidth() / 2.0f);
        float sy = y - (getHeight() / 2.0f);

        float length = (float) Math.sqrt(sx * sx + sy * sy);
        float nx = sx / length;
        float ny = sy / length;
        float theta = (float) Math.atan2(ny, nx);

        final float rad2deg = (float) (180.0 / Math.PI);
        float thetaDeg = theta * rad2deg;

        return (thetaDeg < 0) ? thetaDeg + 360.0f : thetaDeg;
    }

    /**
     * Initialize.
     */
    private void initialize() {
        this.setImageResource(R.drawable.jog);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX(0);
                float y = event.getY(0);
                float theta = getTheta(x, y);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    theta_old = theta;
                    break;
                case MotionEvent.ACTION_MOVE:
                    invalidate();
                    float delta_theta = theta - theta_old;
                    theta_old = theta;
                    int direction = (delta_theta > 0) ? 1 : -1;
                    angle += 3 * direction;
                    notifyChangeListener(direction, angle);
                    break;
                case MotionEvent.ACTION_UP:
                    invalidate();
                    delta_theta = theta - theta_old;
                    theta_old = theta;
                    direction = (delta_theta > 0) ? 1 : -1;
                    angle += 3 * direction;
                    notifyReleaseListener(direction, angle);
                    break;
                default:
                    break;
                }
                return true;
            }
        });
    }

    private void notifyChangeListener(int direction, float angle) {
        if (null != listener) {
            int arg = Math.round(angle);
            listener.onKnobChanged(direction, arg);
        }
    }

    private void notifyReleaseListener(int direction, float angle) {
        if (null != listener) {
            int arg = Math.round(angle);
            listener.onKnobReleased(direction, arg);
        }
    }


    /**
     * THe interface describes the knob listener.
     */
    public interface RotaryKnobListener {

        /**
         * This method is called on knob change, every time the knob turns.
         * @param direction the direction of the rotation: {@code 1} for clockwise, else {@code -1}
         * @param angle the angle of the knob, starting from top position
         */
        public void onKnobChanged(int direction, int angle);

        /**
         * This method is called on knob selection, each time the user releases it.
         * @param direction the direction of the rotation: {@code 1} for clockwise, else {@code -1}
         * @param angle the angle of the knob, starting from top position
         */
        public void onKnobReleased(int direction, int angle);
    }


    private float angle = 0f;

    private float theta_old = 0f;

    private RotaryKnobListener listener;
}