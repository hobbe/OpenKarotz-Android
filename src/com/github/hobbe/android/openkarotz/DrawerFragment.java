package com.github.hobbe.android.openkarotz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Drawer fragment.
 * Fragment that appears in the "content_frame", shows the different view.
 */
public class DrawerFragment extends Fragment {

    /**
     * Initialize a new drawer fragment.
     */
    public DrawerFragment() {
        // Nothing to do
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Fetch the selected page number
        int index = getArguments().getInt(ARG_PAGE_NUMBER);

        // List of pages
        String[] pages = getResources().getStringArray(R.array.pages);

        // Page title
        String pageTitle = pages[index];

        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

        /*
        int imageId = getResources().getIdentifier(pageTitle.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
        ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
        */

        getActivity().setTitle(pageTitle);

        return rootView;
    }


    /**
     * Page number argument.
     */
    public static final String ARG_PAGE_NUMBER = "position";

    // private static final int PAGE_SYSTEM = 0;

}
