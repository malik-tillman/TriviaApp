package com.tillman.malik.triviaworldtour;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

@SuppressLint("ClickableViewAccessibility")
public class Modes extends Fragment {
    View rootView;

    /*Gif View for Our Animated Views*/
    GifImageView weatherGif, populationsGif, impendingGif;
    LottieAnimationView leftNav, rightNav;

    /*Gif List Position*/
    int gifPos = 0;

    /*Device Width*/
    int dWidth;

    /*Texts*/
    private TextView summary, header, title;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modes, container, false);

        initiate();

        return rootView;
    }

    private void initiate() {
        /*Get Screen Width*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity()
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        dWidth = displayMetrics.widthPixels;

        /*Set Gifs*/
        weatherGif = rootView.findViewById(R.id.weatherGif);
        populationsGif = rootView.findViewById(R.id.populationsGif);
        impendingGif = rootView.findViewById(R.id.impendingGif);

        if (dWidth == 1440) {
            weatherGif.setGifImageResource(R.drawable.weathers_1000);
            populationsGif.setGifImageResource(R.drawable.populations_1000);
            impendingGif.setGifImageResource(R.drawable.impending_1000);
        }
        else {
            weatherGif.setGifImageResource(R.drawable.weathers_500);
            populationsGif.setGifImageResource(R.drawable.populations_500);
            impendingGif.setGifImageResource(R.drawable.impending_500);
        }

        /*Set Navigators*/
        leftNav = rootView.findViewById(R.id.left_nav);
        rightNav = rootView.findViewById(R.id.right_nav);
        setNavigatorTouchListeners();

        /*Set Texts*/
        title = rootView.findViewById(R.id.modes_title);
        header = rootView.findViewById(R.id.header);
        summary = rootView.findViewById(R.id.mode_summary);

        title.setTypeface(StartScreen.getFont(getContext(), "janda"));
        header.setTypeface(StartScreen.getFont(getContext(), "janda"));
        summary.setTypeface(StartScreen.getFont(getContext(), "janda"));
    }

    /**
     * Set Navigator Touch Listeners
     * Here we set up the onTouchListeners for our Gif Navigators
     */
    private void setNavigatorTouchListeners() {
        /*Place Scale Animators*/
        leftNav.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleXBy(-.2f).setDuration(200).start();
                    v.animate().scaleYBy(-.2f).setDuration(200).start();

                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(1000).start();
                    v.animate().scaleY(1f).setDuration(1000).start();

                    return cycleGif(0);
                }

                return false;
            }
        });

        rightNav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*User Action*/
                int action = event.getAction();

                /*If Down, We Will Just Scale the Button Down*/
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleXBy(-.2f).setDuration(400).start();
                    v.animate().scaleYBy(-.2f).setDuration(400).start();

                    return true;
                }
                /*When They Let Go, We Will Scale Back Up and Cycle Gifs*/
                else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(1000).start();
                    v.animate().scaleY(1f).setDuration(1000).start();

                    return cycleGif(1);
                }

                return false;
            }
        });

    }

    /**
     * Cycle Gif
     * Based on nav clicked, this method will cycle through our gifs. We also set our mode based
     * on the mode focused.
     *
     * @param navClicked : Right or Left (1 or 0)
     * @return an arbitrary true
     */
    private boolean cycleGif(int navClicked) {
        /*Header*/
        TextView header = rootView.findViewById(R.id.header);
        TextView summary = rootView.findViewById(R.id.mode_summary);
        String newHeader, newSummary;

        switch (navClicked) {
            case 0: /*Left Clicked*/
                /*Move Back in List*/
                gifPos--;

                /*Assure Position is Never Less Than 0*/
                if (gifPos < 0)
                    gifPos = 0;

                /*Determine Position and Render Gif*/
                switch (gifPos) {
                    case 0: /*Weather <- Population*/
                        /*Set Gif*/
                        populationsGif.setVisibility(View.GONE);
                        weatherGif.setVisibility(View.VISIBLE);
                        weatherGif.bringToFront();

                        /*Set Mode*/
                        StartScreen.setMode(getContext(), StartScreen.WEATHERS);

                        /*Set Header and Summary*/
                        newHeader = getActivity().getResources().getString(R.string.weathers_header);
                        newSummary = getActivity().getResources().getString(R.string.weather_summary);
                        header.setText(newHeader);
                        summary.setText(newSummary);

                        break;
                    case 1: /*Population <- Impending*/
                        impendingGif.setVisibility(View.GONE);
                        populationsGif.setVisibility(View.VISIBLE);
                        populationsGif.bringToFront();

                        /*Set Mode*/
                        StartScreen.setMode(getContext(), StartScreen.POPULATIONS);

                        /*Set Header*/
                        newHeader = getActivity().getResources().getString(R.string.populations_header);
                        newSummary = getActivity().getResources().getString(R.string.population_summary);
                        header.setText(newHeader);
                        summary.setText(newSummary);

                        break;
                }
                break;

            case 1: /*Right Clicked*/
                /*Move Back in List*/
                gifPos++;

                /*Assure Position is Never Less Than 0*/
                if (gifPos > 2)
                    gifPos = 2;

                /*Determine Position and Render Gif*/
                switch (gifPos) {
                    case 0: /*Weather*/
                        break;
                    case 1: /*Population <- Weather*/
                        weatherGif.setVisibility(View.GONE);
                        populationsGif.setVisibility(View.VISIBLE);
                        populationsGif.bringToFront();

                        /*Set Mode*/
                        StartScreen.setMode(getContext(), StartScreen.POPULATIONS);

                        /*Set Header*/
                        newHeader = getActivity().getResources().getString(R.string.populations_header);
                        newSummary = getActivity().getResources().getString(R.string.population_summary);
                        header.setText(newHeader);
                        summary.setText(newSummary);

                        break;
                    case 2: /*History <- Population*/
                        populationsGif.setVisibility(View.GONE);
                        impendingGif.setVisibility(View.VISIBLE);
                        impendingGif.bringToFront();

                        /*Set Mode*/
                        StartScreen.setMode(getContext(), StartScreen.IMPENDING);

                        /*Set Header*/
                        newHeader = getActivity().getResources().getString(R.string.impending_header);
                        newSummary = getActivity().getResources().getString(R.string.impending_summary);
                        header.setText(newHeader);
                        summary.setText(newSummary);

                        break;
                }
        }

        return false;
    }

}



