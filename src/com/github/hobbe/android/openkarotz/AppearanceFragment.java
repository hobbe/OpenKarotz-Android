package com.github.hobbe.android.openkarotz;

import java.io.IOException;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Appearance fragment.
 */
public class AppearanceFragment extends Fragment {

    /**
     * Initialize a new system fragment.
     */
    public AppearanceFragment() {
        // Nothing to initialize
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

        View view = inflater.inflate(R.layout.page_appearance, container, false);

        new GetColorTask().execute();

        // Pulse status
        pulseSwitch = (Switch) view.findViewById(R.id.switchPulse);
        pulseSwitch.setOnCheckedChangeListener(new PulseSwitchCheckedChangeListener());

        new GetPulseTask().execute();

        // Color button
        colorButton = (Button) view.findViewById(R.id.btnColor);
        // colorButton.setOnClickListener();

        return view;
    }


    private class GetColorTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                return Integer.valueOf(Karotz.getInstance().getColor());
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz color: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                // TODO: update color
            }

            pd.dismiss();
            pd = null;
        }

        @Override
        protected void onPreExecute() {
            // TODO: I18N
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        }


        private ProgressDialog pd = null;
    }

    private class GetPulseTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                return Boolean.valueOf(Karotz.getInstance().isPulsing());
            } catch (IOException e) {
                Log.e(TAG, "Cannot get Karotz pulsing state: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null) {
                pulseSwitch.setChecked(result.booleanValue());
            }
        }
    }

    private class LedChangeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            int color = Color.WHITE;
            boolean pulse = pulseSwitch.isChecked();

            try {
                Karotz.getInstance().led(color, pulse);
            } catch (IOException e) {
                Log.e(TAG, "Cannot change Karotz LED color: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Nothing?
        }
    }

    private class PulseSwitchCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        public PulseSwitchCheckedChangeListener() {
            // Nothing to do
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "Pulse ON/OFF " + (isChecked ? "" : "un") + "checked");
            new LedChangeTask().execute();
        }
    }


    private Switch pulseSwitch = null;
    private Button colorButton = null;

    private static final String TAG = "AppearanceFragment";
}
