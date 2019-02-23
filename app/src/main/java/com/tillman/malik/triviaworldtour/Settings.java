package com.tillman.malik.triviaworldtour;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Settings extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_settings, container, false);

        Button gotoModes = view.findViewById(R.id.gotoModes);
        Button gotoStats = view.findViewById(R.id.gotoStats);

//        gotoModes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout modesContent = getActivity().findViewById(R.id.modes_content),
//                               thisContent = getActivity().findViewById(R.id.settings_content);
//
//                modesContent.setVisibility(View.VISIBLE);
//                thisContent.setVisibility(View.GONE);
//            }
//        });
//
//        gotoStats.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout statsContent = getActivity().findViewById(R.id.stats_content),
//                        thisContent = getActivity().findViewById(R.id.settings_content);
//
//                statsContent.setVisibility(View.VISIBLE);
//                thisContent.setVisibility(View.GONE);
//            }
//        });

        return view;
    }
}
